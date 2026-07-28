package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import com.srilakshmikanthanp.clipbird.pairing.PairingChannelCollector

class BluetoothPairingChannelCollector(
  pairingServer: BluetoothPairingServer,
  service: BluetoothPairingService
): PairingChannelCollector<BluetoothPairingCandidate, BluetoothPairedDevice, BluetoothChannel>(
  pairingServer, service
)
