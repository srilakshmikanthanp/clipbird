package com.srilakshmikanthanp.clipbird.ui.permission

import androidx.compose.runtime.Composable

@Composable
expect fun BluetoothPermissionGate(content: @Composable () -> Unit)
