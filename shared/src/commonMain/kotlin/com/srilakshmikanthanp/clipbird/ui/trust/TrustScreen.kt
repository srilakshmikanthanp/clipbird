package com.srilakshmikanthanp.clipbird.ui.trust

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import com.srilakshmikanthanp.clipbird.utility.publicKeyFingerprint
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun EmptyState() {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(
      imageVector = Icons.Outlined.Devices,
      contentDescription = null,
      modifier = Modifier.size(80.dp),
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = "No paired devices",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(top = 12.dp),
    )
  }
}

@Composable
private fun DeviceItem(
  device: PairedDevice,
  onRemove: () -> Unit,
) {
  val fingerprint = remember(device.publicKey) { publicKeyFingerprint(device.publicKey) }
  var showDialog by remember { mutableStateOf(false) }

  if (showDialog) {
    AlertDialog(
      onDismissRequest = { showDialog = false },
      confirmButton = {
        TextButton(onClick = { showDialog = false }) { Text("OK") }
      },
      title = { Text("Fingerprint") },
      text = {
        Text(
          text = fingerprint,
          style = MaterialTheme.typography.bodyMedium,
        )
      },
    )
  }

  ListItem(
    headlineContent = {
      Text(
        text = device.name,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
      )
    },
    supportingContent = {
      Text(
        text = fingerprint,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    },
    trailingContent = {
      IconButton(onClick = onRemove) {
        Icon(
          imageVector = Icons.Outlined.Delete,
          contentDescription = "Remove",
          tint = MaterialTheme.colorScheme.error,
        )
      }
    },
    modifier = Modifier
      .fillMaxWidth()
      .clickable { showDialog = true },
  )
}

@Composable
fun TrustedScreen(viewModel: TrustViewModel = koinViewModel()) {
  val devices by viewModel.devices.collectAsStateWithLifecycle()

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    if (devices.isEmpty()) {
      EmptyState()
    } else {
      LazyColumn(
        modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
      ) {
        items(devices, key = { it.id }) { device ->
          DeviceItem(
            device = device,
            onRemove = { viewModel.remove(device.id) },
          )
        }
      }
    }
  }
}