package com.srilakshmikanthanp.clipbird.ui.pairing

import com.srilakshmikanthanp.clipbird.paring.BlockingPairingVerifier
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.PairingCoordinator
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class PairingViewModelModule {
  @KoinViewModel
  fun pairingViewModel(
    coordinator: PairingCoordinator,
    pairedDeviceService: PairedDeviceService<out PairedDevice>,
  ): PairingViewModel = PairingViewModel(coordinator, pairedDeviceService)

  @KoinViewModel
  fun pairingVerificationViewModel(verifier: BlockingPairingVerifier): PairingVerificationViewModel =
    PairingVerificationViewModel(verifier)
}
