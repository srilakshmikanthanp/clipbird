package com.srilakshmikanthanp.clipbird

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.srilakshmikanthanp.clipbird.extension.startClipbirdService
import com.srilakshmikanthanp.clipbird.permission.PermissionGate
import com.srilakshmikanthanp.clipbird.permission.PermissionViewModel
import com.srilakshmikanthanp.clipbird.ui.navigation.DevicesRoute
import com.srilakshmikanthanp.clipbird.ui.navigation.DrawerRoute
import com.srilakshmikanthanp.clipbird.ui.navigation.HistoryRoute
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private val permissionViewModel: PermissionViewModel by viewModels()
  private val initialRouteState = mutableStateOf<DrawerRoute>(DevicesRoute)

  private fun resolveInitialRoute(intent: Intent?): DrawerRoute {
    return when (intent?.getStringExtra(EXTRA_INITIAL_ROUTE)) {
      ROUTE_HISTORY -> HistoryRoute
      else -> DevicesRoute
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    initialRouteState.value = resolveInitialRoute(intent)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    initialRouteState.value = resolveInitialRoute(intent)

    setContent {
      PermissionGate {
        App(initialRoute = initialRouteState.value)
      }
    }

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        permissionViewModel.isReady.collect { ready ->
          if (ready) startClipbirdService()
        }
      }
    }
  }

  companion object {
    const val EXTRA_INITIAL_ROUTE = "com.srilakshmikanthanp.clipbird.MainActivity.InitialRoute"
    const val ROUTE_HISTORY = "history"
  }
}

@Preview
@Composable
fun AppAndroidPreview() {
  App()
}
