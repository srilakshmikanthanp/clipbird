package com.srilakshmikanthanp.clipbird.ui.permission

import androidx.compose.runtime.Composable

@Composable
actual fun BluetoothPermissionGate(content: @Composable () -> Unit) {
  content()
}
