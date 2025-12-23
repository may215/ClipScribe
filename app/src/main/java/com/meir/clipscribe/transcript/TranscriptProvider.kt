package com.meir.clipscribe.transcript

interface TranscriptProvider {
  suspend fun getLines(videoId: String, startSec: Int, endSec: Int): List<TranscriptLine>
}
