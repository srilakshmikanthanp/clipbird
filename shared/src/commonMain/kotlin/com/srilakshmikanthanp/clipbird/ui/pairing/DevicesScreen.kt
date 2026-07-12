package com.srilakshmikanthanp.clipbird.ui.pairing

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srilakshmikanthanp.clipbird.ui.pairing.components.DeviceRow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DevicesScreen(viewModel: PairingViewModel = koinViewModel()) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val snackBarHostState = remember { SnackbarHostState() }

  LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
      snackBarHostState.showSnackbar(event.toMessage())
    }
  }

  Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) { padding ->
    LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
      if (state.discovered.isNotEmpty()) {
        item { SectionHeader("Available") }
        items(state.discovered, key = { it.candidate.name }) { device ->
          DeviceRow(
            name = device.candidate.name,
            onClick = { viewModel.pair(device.candidate) },
            trailing = {
              if (device.isPairing) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
              }
            },
          )
        }
      }

      if (state.paired.isNotEmpty()) {
        item { SectionHeader("Paired") }
        items(state.paired, key = { it.id }) { device ->
          DeviceRow(
            name = device.name,
            trailing = {
              TextButton(onClick = { viewModel.unpair(device.id) }) { Text("Unpair") }
            },
          )
        }
      }

      if (state.discovered.isEmpty() && state.paired.isEmpty()) {
        item {
          Text(
            text = "No devices found",
            modifier = Modifier.fillParentMaxSize().padding(32.dp),
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }
    }
  }
}

@Composable
private fun SectionHeader(text: String) {
  Text(
    text = text,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    style = MaterialTheme.typography.titleSmall,
    color = MaterialTheme.colorScheme.primary,
  )
}

private fun PairingViewModel.PairingEvent.toMessage(): String = when (this) {
  is PairingViewModel.PairingEvent.AlreadyPairing -> "Already pairing with $deviceName"
  is PairingViewModel.PairingEvent.Failed -> "Pairing failed with $deviceName"
  is PairingViewModel.PairingEvent.Unsupported -> "$deviceName is not supported"
  is PairingViewModel.PairingEvent.Error -> "Could not pair with $deviceName"
}
