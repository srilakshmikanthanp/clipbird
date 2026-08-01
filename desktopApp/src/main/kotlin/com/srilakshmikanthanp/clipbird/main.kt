package com.srilakshmikanthanp.clipbird

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.srilakshmikanthanp.clipbird.ui.history.CompactHistoryScreen
import com.srilakshmikanthanp.clipbird.ui.theme.ClipbirdTheme
import dev.nucleusframework.application.DecoratedWindow
import dev.nucleusframework.application.NucleusApplicationScope
import dev.nucleusframework.application.NucleusBackend
import dev.nucleusframework.application.nucleusApplication
import dev.nucleusframework.composenativetray.tray.api.TrayApp
import dev.nucleusframework.composenativetray.tray.api.rememberTrayAppState
import dev.nucleusframework.core.runtime.SingleInstanceManager
import dev.nucleusframework.darkmodedetector.isSystemInDarkMode
import dev.nucleusframework.window.NucleusDecoratedWindowTheme
import dev.nucleusframework.window.TitleBar
import dev.nucleusframework.window.styling.TitleBarColors
import dev.nucleusframework.window.styling.TitleBarMetrics
import dev.nucleusframework.window.styling.TitleBarStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import dev.nucleusframework.composenativetray.menu.api.TrayMenuBuilder
import io.github.vinceglb.autolaunch.AutoLaunch
import kotlinx.coroutines.launch
import javax.imageio.ImageIO

@Composable
fun NucleusApplicationScope.ClipbirdApplication() {
  val clipbirdApplication = remember { ClipbirdApplication(::exitApplication) }
  val autoLaunch = remember { AutoLaunch(appPackageName = AppConstants.APP_PACKAGE_NAME) }
  val coroutineScope = rememberCoroutineScope()
  var isWindowVisible by remember { mutableStateOf(true) }
  var launchAtLogin by remember { mutableStateOf(false) }
  val isDark = isSystemInDarkMode()
  val trayAppState = rememberTrayAppState(initialWindowSize = DpSize(430.dp, 540.dp))
  val shape = RoundedCornerShape(12.dp)

  val isSingleInstance = SingleInstanceManager.isSingleInstance(
    onRestoreRequest = { isWindowVisible = true }
  )

  val titleBarStyle = TitleBarStyle(
    metrics = TitleBarMetrics(
      height = 36.dp
    ),
    colors = TitleBarColors(
      background = if (isDark) Color(0xFF1A1D24) else Color(0xFFF0F0F0),
      inactiveBackground = if (isDark) Color(0xFF15181D) else Color(0xFFE8E8E8),
      content = if (isDark) Color(0xFFE6E6E6) else Color(0xFF1A1A1A),
      border = Color.Transparent,
    )
  )

  val appLogo = remember {
    BitmapPainter(ImageIO.read(Thread.currentThread().contextClassLoader.getResource("logo.png")).toComposeImageBitmap())
  }

  fun onAutoLaunchChange(enabled: Boolean) {
    coroutineScope.launch {
      launchAtLogin = if (enabled) {
        autoLaunch.enable()
        true
      } else {
        autoLaunch.disable()
        false
      }
    }
  }

  fun TrayMenuBuilder.clipbirdTrayMenu() {
    Item(
      label = "Open App",
      onClick = { isWindowVisible = true }
    )

    CheckableItem(
      isEnabled = AutoLaunch.isRunningFromDistributable,
      label = "Launch at Login",
      icon = Icons.Default.PowerSettingsNew,
      checked = launchAtLogin,
      onCheckedChange = { onAutoLaunchChange(it) }
    )

    Divider()

    Item(
      label = "Exit",
      onClick = { clipbirdApplication.exit() }
    )
  }

  if (!isSingleInstance) {
    exitApplication()
    return
  }

  LaunchedEffect(Unit) {
    if (AutoLaunch.isRunningFromDistributable) {
      launchAtLogin = autoLaunch.isEnabled()
    }
  }

  NucleusDecoratedWindowTheme(
    titleBarStyle = titleBarStyle,
    isDark = isDark,
  ) {
    DecoratedWindow(
      onCloseRequest = { isWindowVisible = false },
      title = AppConstants.APP_NAME,
      visible = isWindowVisible,
      icon = appLogo,
    ) {
      TitleBar {
        Text(
          modifier = Modifier.align(Alignment.CenterHorizontally),
          text = AppConstants.APP_NAME,
          color = titleBarStyle.colors.content,
        )
      }
      Box(
        Modifier.fillMaxSize()
      ) {
        App()
      }
    }
  }

  TrayApp(
    icon = appLogo,
    tooltip = AppConstants.APP_NAME,
    state = trayAppState,
    menu = { clipbirdTrayMenu() },
  ) {
    ClipbirdTheme {
      Surface(
        modifier = Modifier.fillMaxSize().border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape),
        shape = shape,
      ) {
        CompactHistoryScreen()
      }
    }
  }
}

fun main() {
  initKoin()

  nucleusApplication(backend = NucleusBackend.Tao) {
    ClipbirdApplication()
  }
}
