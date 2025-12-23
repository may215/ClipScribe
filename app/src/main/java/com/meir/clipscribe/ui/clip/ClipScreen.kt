package com.meir.clipscribe.ui.clip

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.meir.clipscribe.export.MarkdownExporter
import com.meir.clipscribe.export.PdfExporter
import com.meir.clipscribe.util.Formatters
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import androidx.compose.ui.viewinterop.AndroidView

object PlayerBus {
  val seekToMs = mutableStateOf<Long?>(null)
}

@Composable
fun ClipScreen(
  nav: NavController,
  sharedUrl: String? = null,
  videoId: String? = null,
  startSec: Int? = null,
  clipId: String? = null,
  vm: ClipViewModel = viewModel()
) {
  val ctx = LocalContext.current
  val st by vm.state.collectAsState()

  LaunchedEffect(sharedUrl, videoId, startSec, clipId) {
    when {
      !clipId.isNullOrBlank() -> vm.loadFromDb(clipId)
      !sharedUrl.isNullOrBlank() && !videoId.isNullOrBlank() && startSec != null ->
        vm.loadFromShare(sharedUrl, videoId, startSec)
    }
  }

  val exportMd = rememberLauncherForActivityResult(
    ActivityResultContracts.CreateDocument("text/markdown")
  ) { uri ->
    if (uri != null) {
      val md = MarkdownExporter.toMarkdown(
        title = st.title,
        url = st.url,
        startSec = st.editableStartSec,
        endSec = st.editableEndSec,
        lines = st.transcriptLines
      )
      ctx.contentResolver.openOutputStream(uri)?.use { it.write(md.toByteArray(Charsets.UTF_8)) }
    }
  }

  val exportPdf = rememberLauncherForActivityResult(
    ActivityResultContracts.CreateDocument("application/pdf")
  ) { uri ->
    if (uri != null) {
      val bytes = PdfExporter.toPdfBytes(
        title = st.title,
        url = st.url,
        startSec = st.editableStartSec,
        endSec = st.editableEndSec,
        lines = st.transcriptLines
      )
      ctx.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("תמלול קליפ מיוטיוב") },
        actions = { TextButton(onClick = { nav.navigate("library") }) { Text("ספרייה") } }
      )
    }
  ) { pad ->
    Column(
      Modifier.padding(pad).fillMaxSize().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      UrlCard(
        url = st.url,
        startEndLabel = "${Formatters.secToMmSs(st.editableStartSec)} → ${Formatters.secToMmSs(st.editableEndSec)}",
        isLoading = st.isGenerating,
        onSave = { vm.saveClip() },
        onGenerate = { vm.autoGenerateTranscript() }
      )

      PlayerAndEditorCard(
        videoId = st.videoId,
        startSec = st.editableStartSec,
        endSec = st.editableEndSec,
        defaultLength = st.defaultLength,
        isLocked = st.isRangeLocked,
        onToggleLock = { vm.toggleRangeLock() },
        onSelectLength = { vm.changeDefaultLength(it) },
        onRangeChange = { a, b -> vm.updateRange(a, b) },
        onPlaybackMs = { vm.updatePlaybackMs(it) }
      )

      val highlighted = st.transcriptLines.indexOfFirst { st.currentPlaybackMs in it.startMs..it.endMs }

      TranscriptCard(
        lines = st.transcriptLines,
        highlightedIndex = highlighted,
        onSeek = { ms -> PlayerBus.seekToMs.value = ms },
        onCopy = {
          val md = MarkdownExporter.toMarkdown(st.title, st.url, st.editableStartSec, st.editableEndSec, st.transcriptLines)
          copyToClipboard(ctx, md)
        },
        onExportMd = { exportMd.launch("clipscribe.md") },
        onExportPdf = { exportPdf.launch("clipscribe.pdf") },
        onShare = {
          val md = MarkdownExporter.toMarkdown(st.title, st.url, st.editableStartSec, st.editableEndSec, st.transcriptLines)
          shareText(ctx, md)
        }
      )
    }
  }
}

@Composable
private fun UrlCard(
  url: String,
  startEndLabel: String,
  isLoading: Boolean,
  onSave: () -> Unit,
  onGenerate: () -> Unit
) {
  Card {
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Text(
        text = if (url.isBlank()) "אין קישור" else url,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Text("קטע: $startEndLabel", style = MaterialTheme.typography.labelSmall)

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(onClick = onGenerate, enabled = !isLoading, modifier = Modifier.weight(1f)) {
          if (isLoading) {
            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
            Text("מפיק תמלול…")
          } else Text("הפק תמלול")
        }
        OutlinedButton(onClick = onSave) { Text("שמור") }
      }
    }
  }
}

@Composable
private fun PlayerAndEditorCard(
  videoId: String,
  startSec: Int,
  endSec: Int,
  defaultLength: DefaultClipLength,
  isLocked: Boolean,
  onToggleLock: () -> Unit,
  onSelectLength: (DefaultClipLength) -> Unit,
  onRangeChange: (Int, Int) -> Unit,
  onPlaybackMs: (Long) -> Unit
) {
  Card {
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Text("נגן", style = MaterialTheme.typography.titleMedium)

      // Length toggle + lock
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        DefaultClipLength.values().forEach { opt ->
          FilterChip(selected = opt == defaultLength, onClick = { onSelectLength(opt) }, label = { Text(opt.label) })
        }
        FilterChip(
          selected = isLocked,
          onClick = onToggleLock,
          label = { Text(if (isLocked) "קטע נעול" else "נעל קטע") }
        )
      }

      var yt: YouTubePlayer? by remember { mutableStateOf(null) }

      val seekReq = PlayerBus.seekToMs.value
      LaunchedEffect(seekReq) {
        val ms = seekReq ?: return@LaunchedEffect
        yt?.seekTo((ms / 1000f))
        PlayerBus.seekToMs.value = null
      }

      AndroidView(
        modifier = Modifier.fillMaxWidth().height(210.dp),
        factory = { context ->
          YouTubePlayerView(context).apply {
            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
              override fun onReady(player: YouTubePlayer) {
                yt = player
                if (videoId.isNotBlank()) player.loadVideo(videoId, startSec.toFloat())
              }

              override fun onCurrentSecond(player: YouTubePlayer, second: Float) {
                onPlaybackMs((second * 1000).toLong())
                if (second >= endSec.toFloat()) player.pause()
              }
            })
          }
        }
      )

      // Simple controls
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedButton(onClick = { yt?.seekTo(startSec.toFloat()) }) { Text("חזור להתחלה") }
        OutlinedButton(onClick = { yt?.play() }) { Text("נגן") }
        OutlinedButton(onClick = { yt?.pause() }) { Text("עצור") }
      }

      // Range editor
      ClipRangeEditor(
        startSec = startSec,
        endSec = endSec,
        minSec = 0,
        maxSec = 3600,
        onChange = { a, b ->
          onRangeChange(a, b)
          yt?.seekTo(a.toFloat())
        }
      )
    }
  }
}

@Composable
private fun ClipRangeEditor(
  startSec: Int,
  endSec: Int,
  minSec: Int,
  maxSec: Int,
  onChange: (Int, Int) -> Unit
) {
  var range by remember { mutableStateOf(startSec.toFloat()..endSec.toFloat()) }
  LaunchedEffect(startSec, endSec) { range = startSec.toFloat()..endSec.toFloat() }

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text("עריכת קטע", style = MaterialTheme.typography.titleSmall)

    RangeSlider(
      value = range,
      onValueChange = {
        val a = it.start.toInt().coerceIn(minSec, maxSec)
        val b = it.endInclusive.toInt().coerceIn(minSec, maxSec)
        if (b - a >= 5) {
          range = a.toFloat()..b.toFloat()
          onChange(a, b)
        }
      },
      valueRange = minSec.toFloat()..maxSec.toFloat()
    )

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text("התחלה: ${Formatters.secToMmSs(range.start.toInt())}", style = MaterialTheme.typography.labelSmall)
      Text("סיום: ${Formatters.secToMmSs(range.endInclusive.toInt())}", style = MaterialTheme.typography.labelSmall)
    }
  }
}

@Composable
private fun TranscriptCard(
  lines: List<com.meir.clipscribe.transcript.TranscriptLine>,
  highlightedIndex: Int,
  onSeek: (Long) -> Unit,
  onCopy: () -> Unit,
  onExportMd: () -> Unit,
  onExportPdf: () -> Unit,
  onShare: () -> Unit
) {
  Card {
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("תמלול", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        TextButton(onClick = onCopy) { Text("העתק") }
        TextButton(onClick = onExportMd) { Text("MD") }
        TextButton(onClick = onExportPdf) { Text("PDF") }
        TextButton(onClick = onShare) { Text("שיתוף") }
      }
      TranscriptList(lines = lines, highlightedIndex = highlightedIndex, onSeek = onSeek)
    }
  }
}

@Composable
private fun TranscriptList(
  lines: List<com.meir.clipscribe.transcript.TranscriptLine>,
  highlightedIndex: Int,
  onSeek: (Long) -> Unit
) {
  val listState = rememberLazyListState()
  var userScrolling by remember { mutableStateOf(false) }

  LaunchedEffect(listState.isScrollInProgress) { userScrolling = listState.isScrollInProgress }
  LaunchedEffect(highlightedIndex) {
    if (highlightedIndex >= 0 && !userScrolling) listState.animateScrollToItem(highlightedIndex)
  }

  LazyColumn(
    state = listState,
    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 320.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    itemsIndexed(lines) { idx, line ->
      val active = idx == highlightedIndex
      Card(
        colors = CardDefaults.cardColors(
          containerColor = if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth().clickable { onSeek(line.startMs) }
      ) {
        Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(Formatters.msToMmSs(line.startMs), style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(64.dp))
          Text(line.text, style = MaterialTheme.typography.bodyMedium)
        }
      }
    }
  }
}

private fun copyToClipboard(ctx: Context, text: String) {
  val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  cm.setPrimaryClip(ClipData.newPlainText("clipscribe", text))
}

private fun shareText(ctx: Context, text: String) {
  val i = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, text)
  }
  ctx.startActivity(Intent.createChooser(i, "שיתוף"))
}
