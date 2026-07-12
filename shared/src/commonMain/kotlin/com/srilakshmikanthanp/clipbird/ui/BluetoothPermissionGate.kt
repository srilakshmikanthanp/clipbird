package com.srilakshmikanthanp.clipbird.ui

import androidx.compose.runtime.Composable

@Composable
expect fun BluetoothPermissionGate(content: @Composable () -> Unit)
