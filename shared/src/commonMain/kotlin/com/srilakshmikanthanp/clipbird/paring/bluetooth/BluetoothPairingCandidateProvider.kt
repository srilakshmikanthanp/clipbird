package com.srilakshmikanthanp.clipbird.paring.bluetooth

import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.paring.PairingCandidateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BluetoothPairingCandidateProvider(
  bluetoothManager: BluetoothManager,
  private val serviceUuid: Uuid,
  private val scope: CoroutineScope
) : PairingCandidateProvider<BluetoothPairingCandidate> {
  override val devices: StateFlow<Set<BluetoothPairingCandidate>> = bluetoothManager.boundedDevices.map { devices ->
    devices.filter { device ->
      device.serviceUuids.contains(serviceUuid)
    }.mapTo(mutableSetOf()) { device ->
      BluetoothPairingCandidate(device.address, serviceUuid)
    }
  }.stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = emptySet()
  )
}
