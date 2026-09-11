package com.srilakshmikanthanp.clipbird.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun ClipbirdTheme(isDark: Boolean, content: @Composable () -> Unit) {
  val colorScheme = if (isDark) darkColorScheme() else lightColorScheme()
  MaterialTheme(colorScheme = colorScheme, content = content)
}
