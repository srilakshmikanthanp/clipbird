package com.srilakshmikanthanp.clipbird.ui.device

import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDeviceService
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairingService
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class DeviceModule {
  @KoinViewModel
  fun pairingViewModel(
    coordinator: BluetoothPairingService,
    pairedDeviceService: BluetoothPairedDeviceService,
    channelHub: BluetoothPeerHub,
  ): BluetoothDeviceViewModel = BluetoothDeviceViewModel(
    pairedDeviceService,
    coordinator, channelHub
  )
}