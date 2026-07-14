package com.srilakshmikanthanp.clipbird.ui.device

import com.srilakshmikanthanp.clipbird.paring.BlockingPairingVerifier
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.PairingCoordinator
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class DeviceModule {
  @KoinViewModel
  fun pairingVerificationViewModel(
    verifier: BlockingPairingVerifier
  ): PairingVerificationViewModel = PairingVerificationViewModel(verifier)

  @KoinViewModel
  fun pairingViewModel(
    coordinator: PairingCoordinator,
    pairedDeviceService: PairedDeviceService<out PairedDevice>,
  ): DeviceViewModel = DeviceViewModel(coordinator, pairedDeviceService)
}
