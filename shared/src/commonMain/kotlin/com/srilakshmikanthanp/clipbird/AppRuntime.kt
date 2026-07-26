package com.srilakshmikanthanp.clipbird

import com.srilakshmikanthanp.clipbird.clipboard.replication.bluetooth.BluetoothClipboardReplicator
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingChannelCollector
import com.srilakshmikanthanp.clipbird.peer.BluetoothChannelCollector
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppRuntime : KoinComponent {
  private val pairingChannelCollector: BluetoothPairingChannelCollector by inject()
  private val channelCollector: BluetoothChannelCollector by inject()
  private val channelHub: BluetoothPeerHub by inject()
  private val clipboardSyncer: BluetoothClipboardReplicator by inject()

  private val scope = CoroutineScope(SupervisorJob())
  private var job: Job? = null

  fun start() {
    job = scope.launch {
      coroutineScope {
        launch { pairingChannelCollector.run() }
        launch { channelCollector.run() }
        launch { clipboardSyncer.run() }
      }
    }
  }

  fun stop() {
    channelHub.close()
    job?.cancel()
    job = null
  }
}
