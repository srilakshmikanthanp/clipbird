package com.srilakshmikanthanp.clipbird

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import dev.nucleusframework.application.DecoratedWindow
import dev.nucleusframework.application.NucleusBackend
import dev.nucleusframework.application.nucleusApplication
import dev.nucleusframework.composenativetray.tray.api.Tray
import dev.nucleusframework.core.runtime.SingleInstanceManager
import dev.nucleusframework.darkmodedetector.isSystemInDarkMode
import dev.nucleusframework.window.NucleusDecoratedWindowTheme
import dev.nucleusframework.window.TitleBar
import dev.nucleusframework.window.styling.TitleBarColors
import dev.nucleusframework.window.styling.TitleBarMetrics
import dev.nucleusframework.window.styling.TitleBarStyle
import javax.imageio.ImageIO

fun main() {
  initKoin()

  nucleusApplication(backend = NucleusBackend.Tao) {
    val clipbirdApplication = remember { ClipbirdApplication(::exitApplication) }
    var isWindowVisible by remember { mutableStateOf(true) }
    val isDark = isSystemInDarkMode()

    val isSingleInstance = SingleInstanceManager.isSingleInstance(
      onRestoreRequest = {
        isWindowVisible = true
      }
    )

    if (!isSingleInstance) {
      exitApplication()
      return@nucleusApplication
    }

    val appLogo = remember {
      BitmapPainter(ImageIO.read(Thread.currentThread().contextClassLoader.getResource("logo.png")).toComposeImageBitmap())
    }

    val titleBarStyle = TitleBarStyle(
      colors = TitleBarColors(
        background = if (isDark) Color(0xFF1A1D24) else Color(0xFFF0F0F0),
        inactiveBackground = if (isDark) Color(0xFF15181D) else Color(0xFFE8E8E8),
        content = if (isDark) Color(0xFFE6E6E6) else Color(0xFF1A1A1A),
        border = Color.Transparent,
      ),
      metrics = TitleBarMetrics(height = 36.dp),
    )

    Tray(
      icon = appLogo,
      tooltip = "Clipbird",
      primaryAction = { isWindowVisible = true },
      menuContent = {
        Item(label = "Show Window") { isWindowVisible = true }
        Divider()
        Item(label = "Exit") { clipbirdApplication.exit() }
      },
    )

    NucleusDecoratedWindowTheme(
      isDark = isDark,
      titleBarStyle = titleBarStyle
    ) {
      DecoratedWindow(
        onCloseRequest = { isWindowVisible = false },
        title = "Clipbird",
        visible = isWindowVisible,
        icon = appLogo,
      ) {
        TitleBar {
          Text(
            text = "Clipbird",
            color = titleBarStyle.colors.content,
            modifier = Modifier.align(Alignment.CenterHorizontally),
          )
        }
        Box(Modifier.fillMaxSize()) {
          App()
        }
      }
    }
  }
}
