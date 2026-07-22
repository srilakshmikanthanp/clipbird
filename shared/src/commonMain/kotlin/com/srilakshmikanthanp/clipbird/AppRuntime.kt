package com.srilakshmikanthanp.clipbird

import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingCoordinator
import com.srilakshmikanthanp.clipbird.peer.BluetoothChannelCollector
import com.srilakshmikanthanp.clipbird.peer.BluetoothChannelHub
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppRuntime : KoinComponent {
  private val pairingCoordinator: BluetoothPairingCoordinator by inject()
  private val channelCollector: BluetoothChannelCollector by inject()
  private val channelHub: BluetoothChannelHub by inject()

  fun start() {
    pairingCoordinator.start()
    channelCollector.start()
    channelHub.start()
  }

  fun stop() {
    channelHub.stop()
    channelCollector.stop()
    pairingCoordinator.stop()
  }
}
