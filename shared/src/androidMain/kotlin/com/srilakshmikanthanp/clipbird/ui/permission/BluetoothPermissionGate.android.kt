package com.srilakshmikanthanp.clipbird.ui.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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

@Composable
private fun BluetoothPermissionRequired(
  onGrant: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxSize().padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = "Bluetooth permission is required to find and pair devices.",
      textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(16.dp))
    Button(onClick = onGrant) {
      Text("Grant permission")
    }
  }
}

@Composable
actual fun BluetoothPermissionGate(content: @Composable () -> Unit) {
  val required = remember {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
    } else {
      emptyArray()
    }
  }

  val context = LocalContext.current

  fun isAllGranted() = required.all {
    context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
  }

  var granted by remember { mutableStateOf(isAllGranted()) }

  val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
    granted = isAllGranted()
  }

  LaunchedEffect(Unit) {
    if (!granted) launcher.launch(required)
  }

  if (granted) {
    content()
  } else {
    BluetoothPermissionRequired {
      launcher.launch(required)
    }
  }
}
