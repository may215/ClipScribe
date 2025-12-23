package com.meir.clipscribe

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.meir.clipscribe.util.YouTubeUrlParser

class ShareEntryActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val url = extractUrl(intent)
    val parsed = url?.let { YouTubeUrlParser.parse(it) }

    val go = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
      putExtra("shared_url", url)
      putExtra("video_id", parsed?.videoId)
      putExtra("start_sec", parsed?.startSec ?: 0)
    }
    startActivity(go)
    finish()
  }

  private fun extractUrl(intent: Intent): String? =
    when (intent.action) {
      Intent.ACTION_SEND -> intent.getStringExtra(Intent.EXTRA_TEXT)
      Intent.ACTION_VIEW -> intent.dataString
      else -> null
    }?.trim()
}
