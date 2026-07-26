package com.srilakshmikanthanp.clipbird.paring.bluetooth

import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.Pairer
import com.srilakshmikanthanp.clipbird.paring.PairingCandidateProvider
import com.srilakshmikanthanp.clipbird.paring.PairingService
import com.srilakshmikanthanp.clipbird.paring.PairingResponder
import com.srilakshmikanthanp.clipbird.paring.PairingServer
import kotlinx.coroutines.CoroutineScope

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