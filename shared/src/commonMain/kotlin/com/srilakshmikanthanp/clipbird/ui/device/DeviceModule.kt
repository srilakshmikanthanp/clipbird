package com.srilakshmikanthanp.clipbird.ui.device

import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.PairingCoordinator
import com.srilakshmikanthanp.clipbird.peer.ChannelHub
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class DeviceModule {
  @KoinViewModel
  fun pairingViewModel(
    coordinator: PairingCoordinator,
    pairedDeviceService: PairedDeviceService<out PairedDevice>,
    channelHub: ChannelHub,
  ): DeviceViewModel = DeviceViewModel(pairedDeviceService, coordinator, channelHub)
}
