package com.srilakshmikanthanp.clipbird

import com.srilakshmikanthanp.clipbird.pairing.PairingDialogManager
import com.srilakshmikanthanp.clipbird.paring.PairingCoordinator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClipbirdApplication(private val onExit: () -> Unit): KoinComponent {
  private val pairingCoordinator: PairingCoordinator by inject()
  val pairingDialogManager = PairingDialogManager()

  init {
    pairingDialogManager.start()
    pairingCoordinator.start()
  }

  fun exit() {
    pairingDialogManager.stop()
    pairingCoordinator.stop()
    onExit()
  }
}
