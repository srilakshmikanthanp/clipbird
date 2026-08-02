package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import com.srilakshmikanthanp.clipbird.pairing.PairingChannelCollector
import com.srilakshmikanthanp.clipbird.pairing.PairingServer

class BluetoothPairingChannelCollector(
  pairingServer: PairingServer<BluetoothChannel>,
  service: BluetoothPairingService
): PairingChannelCollector<BluetoothPairingCandidate, BluetoothPairedDevice, BluetoothChannel>(
  pairingServer, service
)
