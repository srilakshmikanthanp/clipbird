package com.srilakshmikanthanp.clipbird

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.srilakshmikanthanp.clipbird.extension.startClipbirdService
import com.srilakshmikanthanp.clipbird.permission.PermissionGate
import com.srilakshmikanthanp.clipbird.permission.PermissionViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private val permissionViewModel: PermissionViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    setContent { PermissionGate { App() } }
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        permissionViewModel.isReady.collect { ready ->
          if (ready) startClipbirdService()
        }
      }
    }
  }
}

@Preview
@Composable
fun AppAndroidPreview() {
  App()
}
