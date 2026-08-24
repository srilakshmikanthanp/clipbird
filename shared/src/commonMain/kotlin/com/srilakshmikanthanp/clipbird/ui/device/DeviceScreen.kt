package com.srilakshmikanthanp.clipbird.ui.device

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import com.srilakshmikanthanp.clipbird.pairing.PairingCandidate
import com.srilakshmikanthanp.clipbird.peer.client.ConnectionInitiationDecider
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private fun DeviceViewModel.PairingEvent.toMessage(): String = when (this) {
  is DeviceViewModel.PairingEvent.AlreadyPairing -> "Already pairing with $deviceName"
  is DeviceViewModel.PairingEvent.Failed -> "Pairing failed with $deviceName"
  is DeviceViewModel.PairingEvent.Unsupported -> "$deviceName is not supported"
  is DeviceViewModel.PairingEvent.Error -> "Could not pair with $deviceName"
}

@Composable
private fun DeviceGroup(title: String, content: @Composable () -> Unit) {
  Column {
    Text(
      text = title.uppercase(),
      modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.primary,
    )
    Card(modifier = Modifier.fillMaxWidth()) {
      content()
    }
  }
}

@Composable
private fun EmptyGroup(text: String) {
  Box(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun DeviceAvatar(name: String) {
  Surface(
    shape = CircleShape,
    color = MaterialTheme.colorScheme.primaryContainer,
    modifier = Modifier.size(40.dp),
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = name.firstOrNull()?.uppercase() ?: "?",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
      )
    }
  }
}

@Composable
private fun <C: PairingCandidate> AvailableDevices(
  devices: List<DeviceViewModel.DiscoveredDevice<C>>,
  onPair: (C) -> Unit,
) {
  devices.forEachIndexed { index, device ->
    if (index > 0) HorizontalDivider()
    ListItem(
      modifier = Modifier.clickable(enabled = !device.isPairing) { onPair(device.candidate) },
      colors = ListItemDefaults.colors(containerColor = Color.Transparent),
      headlineContent = { Text(device.candidate.name) },
      leadingContent = { DeviceAvatar(device.candidate.name) },
      supportingContent = { Text(if (device.isPairing) "Pairing…" else "Tap to pair") },
      trailingContent = { if (device.isPairing) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp) },
    )
  }
}

@Composable
private fun <D: PairedDevice> PairedDevices(
  devices: List<DeviceViewModel.Device<D>>,
  onRemove: (ULong) -> Unit,
) {
  @Composable
  fun RemoveDevice(device: D) {
    IconButton(onClick = { onRemove(device.id) }) {
      Icon(Icons.Outlined.Delete, contentDescription = "Remove ${device.name}")
    }
  }

  @Composable
  fun ActiveStatus(remoteDevice: D) {
    val decider = koinInject<ConnectionInitiationDecider>()
    val isInitiatedByMe by produceState(initialValue = false, remoteDevice.id) { value = decider.shouldInitiateConnection(remoteDevice) }
    val color = if (isInitiatedByMe) MaterialTheme.colorScheme.primary else Color(0xFF4CAF50)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      Box(Modifier.size(8.dp).background(color, CircleShape))
      Text("Active", style = MaterialTheme.typography.bodySmall, color = color)
    }
  }

  devices.forEachIndexed { index, device ->
    if (index > 0) HorizontalDivider()
    ListItem(
      headlineContent = { Text(device.pairedDevice.name) },
      leadingContent = { DeviceAvatar(device.pairedDevice.name) },
      colors = ListItemDefaults.colors(containerColor = Color.Transparent),
      supportingContent = { if (device.connected) ActiveStatus(device.pairedDevice) },
      trailingContent = { RemoveDevice(device.pairedDevice) },
    )
  }
}

@Composable
fun DevicesScreen(
  snackBarHostState: SnackbarHostState,
  viewModel: BluetoothDeviceViewModel = koinViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.events.collect { snackBarHostState.showSnackbar(it.toMessage()) }
  }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(24.dp),
      modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
      contentPadding = PaddingValues(16.dp),
    ) {
      item {
        DeviceGroup(title = "Available") {
          if (state.discovered.isNotEmpty()) {
            AvailableDevices(devices = state.discovered, onPair = viewModel::pair)
          } else {
            EmptyGroup("No available devices")
          }
        }
      }

      item {
        DeviceGroup(title = "Paired") {
          if (state.devices.isNotEmpty()) {
            PairedDevices(devices = state.devices, onRemove = viewModel::unpair)
          } else {
            EmptyGroup("No paired devices")
          }
        }
      }
    }
  }
}
