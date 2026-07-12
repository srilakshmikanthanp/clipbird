package com.srilakshmikanthanp.clipbird

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.srilakshmikanthanp.clipbird.ui.BluetoothPermissionGate
import com.srilakshmikanthanp.clipbird.ui.navigation.ClipbirdNavHost
import com.srilakshmikanthanp.clipbird.ui.theme.ClipbirdTheme

@Composable
fun App() {
  ClipbirdTheme {
    Surface(modifier = Modifier.fillMaxSize()) {
      BluetoothPermissionGate {
        ClipbirdNavHost()
      }
    }
  }
}
