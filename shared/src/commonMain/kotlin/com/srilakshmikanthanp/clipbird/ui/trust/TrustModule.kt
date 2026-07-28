package com.srilakshmikanthanp.clipbird.ui.trust

import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDeviceService
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class TrustModule {
  @KoinViewModel
  fun trustedViewModel(
    pairedDeviceService: BluetoothPairedDeviceService,
  ): TrustViewModel = TrustViewModel(pairedDeviceService)
}
