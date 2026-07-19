package com.srilakshmikanthanp.clipbird

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    setContent {
      PermissionGate(onGranted = ::onPermissionsGranted) {
        App()
      }
    }
  }

  private fun onPermissionsGranted() {
    val intent = Intent(this, ClipbirdService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(intent)
    } else {
      startService(intent)
    }
  }
}

@Preview
@Composable
fun AppAndroidPreview() {
  App()
}
