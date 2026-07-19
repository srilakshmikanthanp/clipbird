package com.srilakshmikanthanp.clipbird

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import javax.imageio.ImageIO

fun main() {
  initKoin()

  application {
    val clipbirdApplication = remember { ClipbirdApplication(::exitApplication) }
    var isVisible by remember { mutableStateOf(true) }

    val trayIcon = remember {
      BitmapPainter(ImageIO.read(Thread.currentThread().contextClassLoader.getResource("logo.png")).toComposeImageBitmap())
    }

    Tray(
      icon = trayIcon,
      tooltip = "Clipbird",
      menu = {
        Item("Show Window", onClick = { isVisible = true })
        Separator()
        Item("Exit", onClick = clipbirdApplication::exit)
      },
    )

    Window(
      onCloseRequest = { isVisible = false },
      visible = isVisible,
      title = "clipbird",
    ) {
      App()
    }
  }
}