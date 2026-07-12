package com.srilakshmikanthanp.clipbird.ui.theme

import androidx.compose.runtime.Composable

/**
 * App theme. On Android 12+ it uses Material You dynamic color derived from the wallpaper; on other
 * platforms/older Android it falls back to a default Material 3 light/dark scheme.
 */
@Composable
expect fun ClipbirdTheme(content: @Composable () -> Unit)
