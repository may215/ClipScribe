package com.meir.clipscribe.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
  background = Color(0xFF0B0F14),
  surface = Color(0xFF101826),
  primary = Color(0xFF6D5EF6),
  secondary = Color(0xFF2EE6A6),
  onBackground = Color(0xFFE8EEF7),
  onSurface = Color(0xFFE8EEF7),
  onPrimary = Color.White
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
  MaterialTheme(colorScheme = DarkColors, typography = Typography(), content = content)
}
