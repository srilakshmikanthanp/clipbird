package com.srilakshmikanthanp.clipbird

import com.srilakshmikanthanp.clipbird.clipboard.replication.bluetooth.BluetoothClipboardReplicator
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairingChannelCollector
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import com.srilakshmikanthanp.clipbird.peer.client.bluetooth.BluetoothClipbirdProtocolClient
import com.srilakshmikanthanp.clipbird.peer.server.bluetooth.BluetoothClipbirdProtocolServer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppRuntime : KoinComponent {
  private val pairingChannelCollector: BluetoothPairingChannelCollector by inject()
  private val clipbirdProtocolServer: BluetoothClipbirdProtocolServer by inject()
  private val clipbirdProtocolClient: BluetoothClipbirdProtocolClient by inject()
  private val channelHub: BluetoothPeerHub by inject()
  private val clipboardSyncer: BluetoothClipboardReplicator by inject()

  @Synchronized
  fun start() {
    clipbirdProtocolServer.start()
    clipbirdProtocolClient.start()
    pairingChannelCollector.start()
    clipboardSyncer.start()
    channelHub.start()
  }

  @Synchronized
  fun stop() {
    clipbirdProtocolServer.stop()
    clipbirdProtocolClient.stop()
    pairingChannelCollector.stop()
    clipboardSyncer.stop()
    channelHub.stop()
  }
}
