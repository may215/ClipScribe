package com.meir.clipscribe.transcript

data class TranscriptLine(
  val startMs: Long,
  val endMs: Long,
  val text: String
)
