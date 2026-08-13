package com.srilakshmikanthanp.clipbird

import com.srilakshmikanthanp.clipbird.pairing.PairingDialogManager
import com.srilakshmikanthanp.clipbird.power.NativePowerHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClipbirdApplication(private val onExit: () -> Unit): KoinComponent {
  private val appRuntime by inject<AppRuntime>()
  private val pairingDialogManager = PairingDialogManager()
  private val powerHandler = NativePowerHandler(
    onSleep = { appRuntime.stop() },
    onWake  = { appRuntime.start() }
  )

  init {
    pairingDialogManager.start()
    appRuntime.start()
  }

  fun exit() {
    powerHandler.close()
    appRuntime.stop()
    pairingDialogManager.stop()
    onExit()
  }
}
