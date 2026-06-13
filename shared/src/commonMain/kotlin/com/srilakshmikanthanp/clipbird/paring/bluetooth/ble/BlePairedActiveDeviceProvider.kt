package com.srilakshmikanthanp.clipbird.paring.bluetooth.ble

import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Found
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Lost
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleDiscoverer
import com.srilakshmikanthanp.clipbird.paring.PairedActiveDeviceProvider
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlin.uuid.ExperimentalUuidApi

class BlePairedActiveDeviceProvider(
  discoverer: BleDiscoverer,
  service: BluetoothPairedDeviceService,
  private val scope: CoroutineScope
) : PairedActiveDeviceProvider<BluetoothPairedDevice> {
  private val activeDeviceIds = discoverer.events.scan(emptySet<Long>()) { activeDevices, event ->
    when (event) {
      is Found -> activeDevices + event.device.id
      is Lost -> activeDevices - event.device.id
    }
  }.stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = emptySet()
  )

  @OptIn(ExperimentalUuidApi::class)
  override val devices: StateFlow<Collection<BluetoothPairedDevice>> = combine(
    service.getAll(),
    activeDeviceIds
  ) { pairedDevices, activeDevices ->
    pairedDevices.filter { it.id in activeDevices }
  }.stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = emptySet()
  )
}
