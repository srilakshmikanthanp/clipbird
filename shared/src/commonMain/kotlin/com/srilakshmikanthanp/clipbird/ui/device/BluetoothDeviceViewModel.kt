package com.srilakshmikanthanp.clipbird.ui.device

import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingCandidate
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.PairingCoordinator
import com.srilakshmikanthanp.clipbird.peer.ChannelHub

class BluetoothDeviceViewModel(
  pairedDeviceService: PairedDeviceService<BluetoothPairedDevice>,
  pairingCoordinator: PairingCoordinator<BluetoothPairingCandidate, BluetoothPairedDevice>,
  channelHub: ChannelHub<BluetoothPairedDevice>
): DeviceViewModel<BluetoothPairingCandidate, BluetoothPairedDevice>(
  pairedDeviceService,
  pairingCoordinator,
  channelHub
)
