package com.srilakshmikanthanp.clipbird.permission

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.srilakshmikanthanp.clipbird.extension.hasRequiredPermissions
import com.srilakshmikanthanp.clipbird.extension.isBatteryOptimizationDisabled
import com.srilakshmikanthanp.clipbird.extension.requiredPermissions

@Composable
private fun ActionRequired(message: String, action: String, onClick: () -> Unit) {
  Column(
    modifier = Modifier.fillMaxSize().padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = message,
      textAlign = TextAlign.Center,
    )

    Spacer(Modifier.height(16.dp))

    Button(onClick = onClick) {
      Text(action)
    }
  }
}

@SuppressLint("BatteryLife")
@Composable
fun PermissionGate(content: @Composable () -> Unit) {
  val permissionViewModel: PermissionViewModel = viewModel()
  val context = LocalContext.current

  val permissions = remember { requiredPermissions }

  var batteryOptimizationDisabled by remember {
    mutableStateOf(context.isBatteryOptimizationDisabled())
  }

  var permissionsGranted by remember {
    mutableStateOf(context.hasRequiredPermissions())
  }

  val batteryOptimizationDisableLauncher = rememberLauncherForActivityResult(
    StartActivityForResult()
  ) {
    batteryOptimizationDisabled = context.isBatteryOptimizationDisabled()
  }

  val permissionGrantLauncher = rememberLauncherForActivityResult(
    RequestMultiplePermissions()
  ) {
    permissionsGranted = context.hasRequiredPermissions()
  }

  LaunchedEffect(permissionsGranted, batteryOptimizationDisabled) {
    permissionViewModel.setReady(permissionsGranted && batteryOptimizationDisabled)
  }

  LaunchedEffect(Unit) {
    if (!permissionsGranted) {
      permissionGrantLauncher.launch(permissions)
    }
  }

  when {
    !permissionsGranted -> ActionRequired(
      message = "Clipbird requires Bluetooth and notification permissions.",
      action = "Grant permissions",
    ) {
      permissionGrantLauncher.launch(permissions)
    }

    !batteryOptimizationDisabled -> ActionRequired(
      message = "Disable battery optimization so Clipbird can sync your clipboard while running in the background.",
      action = "Open settings",
    ) {
      batteryOptimizationDisableLauncher.launch(
        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply { data = "package:${context.packageName}".toUri() }
      )
    }

    else -> content()
  }
}