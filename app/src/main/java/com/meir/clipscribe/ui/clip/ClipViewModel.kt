package com.meir.clipscribe.ui.clip

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.meir.clipscribe.data.db.ClipEntity
import com.meir.clipscribe.data.repo.ClipRepository
import com.meir.clipscribe.export.MarkdownExporter
import com.meir.clipscribe.transcript.DemoTranscriptProvider
import com.meir.clipscribe.transcript.TranscriptLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class DefaultClipLength(val seconds: Int, val label: String) {
  SEC_30(30, "30 שנ׳"),
  MIN_1(60, "1 דק׳"),
  MIN_2(120, "2 דק׳"),
  MIN_5(300, "5 דק׳")
}

data class ClipUiState(
  val clipId: String? = null,
  val url: String = "",
  val videoId: String = "",
  val title: String = "ClipScribe",
  val editableStartSec: Int = 0,
  val editableEndSec: Int = 120,
  val defaultLength: DefaultClipLength = DefaultClipLength.MIN_2,
  val isRangeLocked: Boolean = false,
  val isGenerating: Boolean = false,
  val transcriptLines: List<TranscriptLine> = emptyList(),
  val currentPlaybackMs: Long = 0L
)

class ClipViewModel(app: Application) : AndroidViewModel(app) {
  private val repo = ClipRepository(app)
  private val provider = DemoTranscriptProvider()

  private val _state = MutableStateFlow(ClipUiState())
  val state: StateFlow<ClipUiState> = _state

  fun loadFromShare(url: String, videoId: String, startSec: Int) {
    val length = DefaultClipLength.MIN_2.seconds
    _state.value = ClipUiState(
      url = url,
      videoId = videoId,
      editableStartSec = startSec,
      editableEndSec = startSec + length,
      defaultLength = DefaultClipLength.MIN_2,
      isGenerating = true
    )
    autoGenerateTranscript()
  }

  fun loadFromDb(id: String) {
    viewModelScope.launch {
      val c = repo.getById(id) ?: return@launch
      _state.update {
        it.copy(
          clipId = c.id,
          url = c.url,
          videoId = c.videoId,
          title = c.title,
          editableStartSec = c.startSec,
          editableEndSec = c.endSec,
          isGenerating = false,
          currentPlaybackMs = c.lastPlaybackMs
        )
      }
      // rebuild demo lines
      autoGenerateTranscript()
    }
  }

  fun updateRange(start: Int, end: Int) {
    _state.update { it.copy(editableStartSec = start, editableEndSec = end) }
  }

  fun updatePlaybackMs(ms: Long) {
    _state.update { it.copy(currentPlaybackMs = ms) }
  }

  fun toggleRangeLock() {
    _state.update { it.copy(isRangeLocked = !it.isRangeLocked) }
  }

  fun changeDefaultLength(length: DefaultClipLength) {
    _state.update {
      if (it.isRangeLocked) it.copy(defaultLength = length)
      else it.copy(defaultLength = length, editableEndSec = it.editableStartSec + length.seconds)
    }
    autoGenerateTranscript()
  }

  fun autoGenerateTranscript() {
    _state.update { it.copy(isGenerating = true) }
    viewModelScope.launch {
      val s = _state.value
      val lines = provider.getLines(s.videoId, s.editableStartSec, s.editableEndSec)
      _state.update { it.copy(isGenerating = false, transcriptLines = lines) }
    }
  }

  fun saveClip() {
    viewModelScope.launch {
      val s = _state.value
      val id = s.clipId ?: UUID.randomUUID().toString()
      val md = MarkdownExporter.toMarkdown(
        title = s.title,
        url = s.url,
        startSec = s.editableStartSec,
        endSec = s.editableEndSec,
        lines = s.transcriptLines
      )
      repo.upsert(
        ClipEntity(
          id = id,
          url = s.url,
          videoId = s.videoId,
          startSec = s.editableStartSec,
          endSec = s.editableEndSec,
          createdAt = System.currentTimeMillis(),
          title = s.title,
          transcriptMd = md,
          lastPlaybackMs = s.currentPlaybackMs
        )
      )
      _state.update { it.copy(clipId = id) }
    }
  }
}
