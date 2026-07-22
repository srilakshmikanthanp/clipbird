package com.srilakshmikanthanp.clipbird.paring.bluetooth

import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.Pairer
import com.srilakshmikanthanp.clipbird.paring.PairingCandidateProvider
import com.srilakshmikanthanp.clipbird.paring.PairingCoordinator
import com.srilakshmikanthanp.clipbird.paring.PairingResponder
import com.srilakshmikanthanp.clipbird.paring.PairingServer
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingCandidate
import kotlinx.coroutines.CoroutineScope

class BluetoothPairingCoordinator(
  bluetoothProvider: PairingCandidateProvider<BluetoothPairingCandidate>,
  bluetoothPairer: Pairer<BluetoothPairingCandidate, BluetoothPairedDevice>,
  pairingServer: PairingServer<BluetoothChannel>,
  service: PairedDeviceService<BluetoothPairedDevice>,
  scope: CoroutineScope,
  responder: PairingResponder<BluetoothChannel, BluetoothPairedDevice>
) : PairingCoordinator<BluetoothPairingCandidate, BluetoothPairedDevice>(
  bluetoothProvider,
  bluetoothPairer,
  pairingServer,
  service,
  scope,
  responder
) {
}