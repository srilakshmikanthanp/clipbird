package com.srilakshmikanthanp.clipbird

import com.srilakshmikanthanp.clipbird.pairing.PairingDialogManager
import com.srilakshmikanthanp.clipbird.paring.PairingCoordinator
import com.srilakshmikanthanp.clipbird.peer.ChannelCollector
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClipbirdApplication(private val onExit: () -> Unit): KoinComponent {
  private val appRuntime by inject<AppRuntime>()
  private val pairingDialogManager = PairingDialogManager()

  init {
    pairingDialogManager.start()
    appRuntime.start()
  }

  fun exit() {
    appRuntime.stop()
    pairingDialogManager.stop()
    onExit()
  }
}
