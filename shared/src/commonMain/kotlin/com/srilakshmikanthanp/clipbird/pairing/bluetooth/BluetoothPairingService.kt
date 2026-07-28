package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceService
import com.srilakshmikanthanp.clipbird.pairing.Pairer
import com.srilakshmikanthanp.clipbird.pairing.PairingCandidateProvider
import com.srilakshmikanthanp.clipbird.pairing.PairingService
import com.srilakshmikanthanp.clipbird.pairing.PairingResponder

class BluetoothPairingService(
  bluetoothProvider: PairingCandidateProvider<BluetoothPairingCandidate>,
  bluetoothPairer: Pairer<BluetoothPairingCandidate, BluetoothPairedDevice>,
  service: PairedDeviceService<BluetoothPairedDevice>,
  responder: PairingResponder<BluetoothChannel, BluetoothPairedDevice>
) : PairingService<BluetoothPairingCandidate, BluetoothPairedDevice, BluetoothChannel>(
  bluetoothProvider,
  bluetoothPairer,
  service,
  responder
) {
}