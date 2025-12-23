package com.meir.clipscribe.export

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.meir.clipscribe.transcript.TranscriptLine
import com.meir.clipscribe.util.Formatters

object PdfExporter {
  fun toPdfBytes(title: String, url: String, startSec: Int, endSec: Int, lines: List<TranscriptLine>): ByteArray {
    val doc = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4-ish @72dpi
    val page = doc.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint().apply { textSize = 12f }

    var y = 40f
    fun line(text: String) {
      canvas.drawText(text, 40f, y, paint)
      y += 18f
    }

    line(title.ifBlank { "ClipScribe" })
    line("Source: $url")
    line("Range: ${Formatters.secToMmSs(startSec)} -> ${Formatters.secToMmSs(endSec)}")
    y += 10f
    line("Transcript:")
    y += 6f

    for (l in lines) {
      val t = "${Formatters.msToMmSs(l.startMs)}  ${l.text}"
      if (y > 800f) break // MVP: single page
      line(t.take(120))
    }

    doc.finishPage(page)
    val out = java.io.ByteArrayOutputStream()
    doc.writeTo(out)
    doc.close()
    return out.toByteArray()
  }
}
