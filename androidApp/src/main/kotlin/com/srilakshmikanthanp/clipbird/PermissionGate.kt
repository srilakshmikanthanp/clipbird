package com.srilakshmikanthanp.clipbird

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.srilakshmikanthanp.clipbird.extension.startClipbirdService

private fun Context.hasRequiredPermissions(permissions: Array<String>) = permissions.all {
  checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
}

private fun PowerManager.isIgnoringBatteryOptimizations(context: Context): Boolean {
  return isIgnoringBatteryOptimizations(context.packageName)
}

private val RequiredPermissions: Array<String> get() = buildList {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    add(Manifest.permission.POST_NOTIFICATIONS)
  }

  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    add(Manifest.permission.BLUETOOTH_CONNECT)
    add(Manifest.permission.BLUETOOTH_SCAN)
    add(Manifest.permission.BLUETOOTH_ADVERTISE)
  }
}.toTypedArray()

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
  val context = LocalContext.current

  val powerManager = remember {
    context.getSystemService(Context.POWER_SERVICE) as PowerManager
  }

  val permissions = remember {
    RequiredPermissions
  }

  var batteryOptimizationDisabled by remember {
    mutableStateOf(powerManager.isIgnoringBatteryOptimizations(context))
  }

  var permissionsGranted by remember {
    mutableStateOf(context.hasRequiredPermissions(permissions))
  }

  val batteryOptimizationDisableLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) {
    batteryOptimizationDisabled = powerManager.isIgnoringBatteryOptimizations(context)
  }

  val permissionGrantLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) {
    permissionsGranted = context.hasRequiredPermissions(permissions)
  }

  LaunchedEffect(permissionsGranted, batteryOptimizationDisabled) {
    if (permissionsGranted && batteryOptimizationDisabled) {
      context.startClipbirdService()
    }
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
