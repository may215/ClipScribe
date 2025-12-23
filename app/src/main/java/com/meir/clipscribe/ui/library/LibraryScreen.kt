package com.meir.clipscribe.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meir.clipscribe.util.Formatters

@Composable
fun LibraryScreen(
  onOpenClip: (String) -> Unit,
  vm: LibraryViewModel = viewModel()
) {
  val clips by vm.clips.collectAsState()

  Scaffold(topBar = { TopAppBar(title = { Text("ספרייה") }) }) { pad ->
    Column(
      Modifier.padding(pad).fillMaxSize().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      if (clips.isEmpty()) {
        Text("עדיין אין קליפים שמורים. שתף קישור YouTube כדי להתחיל.")
      } else {
        clips.forEach { c ->
          Card(
            modifier = Modifier.fillMaxWidth().clickable { onOpenClip(c.id) }
          ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(c.title.ifBlank { "ClipScribe" }, style = MaterialTheme.typography.titleMedium)
              Text("${Formatters.secToMmSs(c.startSec)} → ${Formatters.secToMmSs(c.endSec)}", style = MaterialTheme.typography.labelSmall)
              Text(c.url, maxLines = 1)
            }
          }
        }
      }
    }
  }
}
