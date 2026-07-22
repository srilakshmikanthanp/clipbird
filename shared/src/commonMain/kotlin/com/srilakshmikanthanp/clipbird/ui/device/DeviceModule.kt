package com.srilakshmikanthanp.clipbird.ui.device

import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingCoordinator
import com.srilakshmikanthanp.clipbird.peer.BluetoothChannelHub
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class DeviceModule {
  @KoinViewModel
  fun pairingViewModel(
    coordinator: BluetoothPairingCoordinator,
    pairedDeviceService: BluetoothPairedDeviceService,
    channelHub: BluetoothChannelHub,
  ): BluetoothDeviceViewModel = BluetoothDeviceViewModel(
    pairedDeviceService,
    coordinator, channelHub
  )
}