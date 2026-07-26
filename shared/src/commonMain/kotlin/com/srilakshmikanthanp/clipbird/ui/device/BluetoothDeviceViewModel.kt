package com.srilakshmikanthanp.clipbird.ui.device

import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingCandidate
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.PairingService
import com.srilakshmikanthanp.clipbird.peer.PeerHub

class BluetoothDeviceViewModel(
  pairedDeviceService: PairedDeviceService<BluetoothPairedDevice>,
  pairingService: PairingService<BluetoothPairingCandidate, BluetoothPairedDevice, BluetoothChannel>,
  peerHub: PeerHub<BluetoothPairedDevice>
): DeviceViewModel<BluetoothPairingCandidate, BluetoothPairedDevice, BluetoothChannel>(
  pairedDeviceService,
  pairingService,
  peerHub
)
