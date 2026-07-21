package com.srilakshmikanthanp.clipbird

import com.srilakshmikanthanp.clipbird.pairing.PairingDialogManager
import com.srilakshmikanthanp.clipbird.paring.PairingCoordinator
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServerCoordinator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClipbirdApplication(private val onExit: () -> Unit): KoinComponent {
  private val pairingCoordinator: PairingCoordinator by inject()
  private val serverCoordinator: ClipbirdServerCoordinator by inject()
  val pairingDialogManager = PairingDialogManager()

  init {
    pairingDialogManager.start()
    pairingCoordinator.start()
    serverCoordinator.start()
  }

  fun exit() {
    serverCoordinator.stop()
    pairingDialogManager.stop()
    pairingCoordinator.stop()
    onExit()
  }
}
