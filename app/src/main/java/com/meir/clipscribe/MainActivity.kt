package com.meir.clipscribe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.meir.clipscribe.nav.AppNav
import com.meir.clipscribe.ui.theme.AppTheme
import com.meir.clipscribe.ui.theme.AppRoot

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val sharedUrl = intent.getStringExtra("shared_url")
    val videoId = intent.getStringExtra("video_id")
    val startSec = intent.getIntExtra("start_sec", 0)

    setContent {
      AppTheme {
        AppRoot {
          AppNav(sharedUrl = sharedUrl, videoId = videoId, startSec = startSec)
        }
      }
    }
  }
}
