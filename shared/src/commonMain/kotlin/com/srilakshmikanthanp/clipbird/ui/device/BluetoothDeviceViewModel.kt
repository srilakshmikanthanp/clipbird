package com.srilakshmikanthanp.clipbird.ui.device

import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairingCandidate
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceService
import com.srilakshmikanthanp.clipbird.pairing.PairingService
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
