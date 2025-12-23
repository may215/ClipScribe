package com.meir.clipscribe.nav

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.meir.clipscribe.ui.clip.ClipScreen
import com.meir.clipscribe.ui.library.LibraryScreen
import java.net.URLEncoder

@Composable
fun AppNav(sharedUrl: String?, videoId: String?, startSec: Int) {
  val nav = rememberNavController()

  NavHost(navController = nav, startDestination = "library") {
    composable("library") {
      LibraryScreen(
        onOpenClip = { id -> nav.navigate("clipById/$id") }
      )
    }

    composable(
      route = "clip?url={url}&vid={vid}&start={start}",
      arguments = listOf(
        navArgument("url") { type = NavType.StringType; defaultValue = "" },
        navArgument("vid") { type = NavType.StringType; defaultValue = "" },
        navArgument("start") { type = NavType.IntType; defaultValue = 0 }
      )
    ) { backStack ->
      val url = backStack.arguments?.getString("url").orEmpty()
      val vid = backStack.arguments?.getString("vid").orEmpty()
      val s = backStack.arguments?.getInt("start") ?: 0
      ClipScreen(nav = nav, sharedUrl = url, videoId = vid, startSec = s)
    }

    composable(
      route = "clipById/{id}",
      arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) { backStack ->
      val id = backStack.arguments?.getString("id").orEmpty()
      ClipScreen(nav = nav, clipId = id)
    }
  }

  LaunchedEffect(sharedUrl, videoId, startSec) {
    if (!sharedUrl.isNullOrBlank() && !videoId.isNullOrBlank()) {
      nav.navigate(
        "clip?url=${enc(sharedUrl)}&vid=${enc(videoId)}&start=$startSec"
      ) { launchSingleTop = true }
    }
  }
}

private fun enc(s: String): String = URLEncoder.encode(s, Charsets.UTF_8.name())
