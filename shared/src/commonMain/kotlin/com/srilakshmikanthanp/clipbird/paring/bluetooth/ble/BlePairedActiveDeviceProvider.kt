package com.srilakshmikanthanp.clipbird.paring.bluetooth.ble

import com.srilakshmikanthanp.clipbird.common.toPublicKey
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleDiscoverer
import com.srilakshmikanthanp.clipbird.paring.PairedActiveDeviceProvider
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.emptySet
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class BlePairedActiveDeviceProvider(
  discoverer: BleDiscoverer,
  service: BluetoothPairedDeviceService,
  private val scope: CoroutineScope
) : PairedActiveDeviceProvider<BluetoothPairedDevice> {
  private val activeDeviceIds = discoverer.events.scan(emptySet<Long>()) { activeDevices, event ->
    when (event) {
      is DiscoveryEvent.Found -> activeDevices + event.device.id
      is DiscoveryEvent.Lost -> activeDevices - event.device.id
    }
  }.stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = emptySet()
  )

  @OptIn(ExperimentalUuidApi::class)
  override val devices: StateFlow<Set<BluetoothPairedDevice>> = combine(
    service.getAll(),
    activeDeviceIds
  ) { pairedDevices, activeDevices ->
    pairedDevices.filter {
      it.id in activeDevices
    }.map {
      BluetoothPairedDevice(it.id, it.name, it.publicKey.toPublicKey(), Uuid.parse(it.address))
    }.toSet()
  }.stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = emptySet()
  )
}
