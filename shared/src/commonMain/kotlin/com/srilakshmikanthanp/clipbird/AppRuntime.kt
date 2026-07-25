package com.srilakshmikanthanp.clipbird

import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingCoordinator
import com.srilakshmikanthanp.clipbird.peer.BluetoothChannelCollector
import com.srilakshmikanthanp.clipbird.peer.BluetoothChannelHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppRuntime : KoinComponent {
  private val pairingCoordinator: BluetoothPairingCoordinator by inject()
  private val channelCollector: BluetoothChannelCollector by inject()
  private val channelHub: BluetoothChannelHub by inject()

  private val scope = CoroutineScope(SupervisorJob())
  private var job: Job? = null

  fun start() {
    job = scope.launch {
      coroutineScope {
        launch { pairingCoordinator.run() }
        launch { channelCollector.run() }
        launch { channelHub.run() }
      }
    }
  }

  fun stop() {
    job?.cancel()
    job = null
  }
}
