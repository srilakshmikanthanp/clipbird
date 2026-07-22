package com.srilakshmikanthanp.clipbird

import com.srilakshmikanthanp.clipbird.paring.PairingCoordinator
import com.srilakshmikanthanp.clipbird.peer.ChannelCollector
import com.srilakshmikanthanp.clipbird.peer.ChannelHub
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppRuntime : KoinComponent {
  private val pairingCoordinator: PairingCoordinator by inject()
  private val channelCollector: ChannelCollector by inject()
  private val channelHub: ChannelHub by inject()

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