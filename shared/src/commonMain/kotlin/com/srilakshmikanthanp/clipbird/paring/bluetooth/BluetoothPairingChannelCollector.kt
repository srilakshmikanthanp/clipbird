package com.srilakshmikanthanp.clipbird.paring.bluetooth

import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import com.srilakshmikanthanp.clipbird.paring.PairingChannelCollector

class BluetoothPairingChannelCollector(
  pairingServer: BluetoothPairingServer,
  service: BluetoothPairingService
): PairingChannelCollector<BluetoothPairingCandidate, BluetoothPairedDevice, BluetoothChannel>(
  pairingServer, service
)
