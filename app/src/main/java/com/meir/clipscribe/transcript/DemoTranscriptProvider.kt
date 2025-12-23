package com.meir.clipscribe.transcript

class DemoTranscriptProvider : TranscriptProvider {
  override suspend fun getLines(videoId: String, startSec: Int, endSec: Int): List<TranscriptLine> {
    val startMs = startSec * 1000L
    val endMs = endSec * 1000L
    val out = mutableListOf<TranscriptLine>()
    var t = startMs
    var i = 1
    while (t < endMs) {
      val e = (t + 8000L).coerceAtMost(endMs)
      out.add(TranscriptLine(t, e, "שורת תמלול לדוגמה #$i (כאן ייכנס תמלול אמיתי ממקור מורשה)"))
      t = e
      i++
    }
    return out
  }
}
