package com.srilakshmikanthanp.clipbird.paring.bluetooth

import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.paring.PairingCandidateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BluetoothPairingCandidateProvider(
  bluetoothPairedDeviceService: BluetoothPairedDeviceService,
  scope: CoroutineScope,
  bluetoothManager: BluetoothManager,
) : PairingCandidateProvider<BluetoothPairingCandidate> {
  override val devices: StateFlow<Collection<BluetoothPairingCandidate>> = bluetoothManager.boundedDevices.combine(
    bluetoothPairedDeviceService.getAll()
  ) { boundedDevices, pairedDevices ->
    val pairedAddresses = pairedDevices.map { it.address }.toSet()
    boundedDevices.mapNotNull {
      val name = it.name ?: return@mapNotNull null
      val address = it.address
      if (address in pairedAddresses) return@mapNotNull null
      BluetoothPairingCandidate(name, address)
    }
  }.stateIn(
    scope,
    started = SharingStarted.WhileSubscribed(),
    emptyList()
  )
}
