package com.meir.clipscribe.util

import android.net.Uri
import kotlin.math.max

data class ParsedYouTube(val videoId: String, val startSec: Int)

object YouTubeUrlParser {
  fun parse(input: String): ParsedYouTube? {
    val uri = runCatching { Uri.parse(input.trim()) }.getOrNull() ?: return null
    val host = uri.host?.lowercase() ?: return null

    val videoId = when {
      host.contains("youtu.be") -> uri.pathSegments.firstOrNull()
      host.contains("youtube.com") -> uri.getQueryParameter("v")
      else -> null
    }?.takeIf { it.isNotBlank() } ?: return null

    val start = parseStartSeconds(uri)
    return ParsedYouTube(videoId, start)
  }

  private fun parseStartSeconds(uri: Uri): Int {
    val t = uri.getQueryParameter("t") ?: uri.getQueryParameter("start") ?: ""
    if (t.isBlank()) return 0
    val raw = t.lowercase()
    if (raw.all { it.isDigit() }) return max(0, raw.toInt())

    val h = Regex("(\\d+)h").find(raw)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    val m = Regex("(\\d+)m").find(raw)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    val s = Regex("(\\d+)s").find(raw)?.groupValues?.get(1)?.toIntOrNull()
      ?: raw.filter { it.isDigit() }.toIntOrNull() ?: 0

    return max(0, h * 3600 + m * 60 + s)
  }
}
