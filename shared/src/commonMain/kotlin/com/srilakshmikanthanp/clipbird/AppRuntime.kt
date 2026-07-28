package com.srilakshmikanthanp.clipbird

import com.srilakshmikanthanp.clipbird.clipboard.replication.bluetooth.BluetoothClipboardReplicator
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairingChannelCollector
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import com.srilakshmikanthanp.clipbird.peer.client.bluetooth.BluetoothClipbirdProtocolClient
import com.srilakshmikanthanp.clipbird.peer.server.bluetooth.BluetoothClipbirdProtocolServer
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppRuntime : KoinComponent {
  private val pairingChannelCollector: BluetoothPairingChannelCollector by inject()
  private val clipbirdProtocolServer: BluetoothClipbirdProtocolServer by inject()
  private val clipbirdProtocolClient: BluetoothClipbirdProtocolClient by inject()
  private val channelHub: BluetoothPeerHub by inject()
  private val clipboardSyncer: BluetoothClipboardReplicator by inject()

  private val scope = CoroutineScope(SupervisorJob())
  private var job: Job? = null

  @Synchronized
  fun start() {
    if (job?.isActive == true) return

    job = scope.launch {
      coroutineScope {
        launch { pairingChannelCollector.run() }
        launch { clipbirdProtocolServer.run() }
        launch { clipbirdProtocolClient.run() }
        launch { clipboardSyncer.run() }
      }
    }
  }

  @Synchronized
  fun stop() {
    channelHub.close()
    job?.cancel()
    job = null
  }
}
