package com.meir.clipscribe.export

import com.meir.clipscribe.transcript.TranscriptLine
import com.meir.clipscribe.util.Formatters

object MarkdownExporter {
  fun toMarkdown(title: String, url: String, startSec: Int, endSec: Int, lines: List<TranscriptLine>): String {
    val sb = StringBuilder()
    sb.append("# ").append(title.ifBlank { "ClipScribe" }).append("\n\n")
    sb.append("- מקור: ").append(url).append("\n")
    sb.append("- טווח: ").append(Formatters.secToMmSs(startSec)).append(" → ")
      .append(Formatters.secToMmSs(endSec)).append("\n\n")
    sb.append("## תמלול\n\n")
    for (l in lines) {
      sb.append("- **").append(Formatters.msToMmSs(l.startMs)).append("** ")
        .append(l.text).append("\n")
    }
    return sb.toString()
  }
}
