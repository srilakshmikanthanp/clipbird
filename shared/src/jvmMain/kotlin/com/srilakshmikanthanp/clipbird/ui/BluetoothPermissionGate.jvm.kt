package com.srilakshmikanthanp.clipbird.ui

import androidx.compose.runtime.Composable

@Composable
actual fun BluetoothPermissionGate(content: @Composable () -> Unit) {
  content()
}
