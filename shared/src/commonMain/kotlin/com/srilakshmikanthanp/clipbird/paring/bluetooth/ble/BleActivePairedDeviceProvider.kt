package com.srilakshmikanthanp.clipbird.paring.bluetooth.ble

import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Found
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Lost
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleDiscoverer
import com.srilakshmikanthanp.clipbird.paring.ActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class BleActivePairedDeviceProvider(
  discoverer: BleDiscoverer,
  service: BluetoothPairedDeviceService,
  private val scope: CoroutineScope
) : ActivePairedDeviceProvider<BluetoothPairedDevice> {
  private val activeDeviceIds = discoverer.events.scan(emptySet<Long>()) { activeDevicesIds, event ->
    when (event) {
      is Found -> activeDevicesIds + event.device.id
      is Lost -> activeDevicesIds - event.device.id
    }
  }.stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = emptySet()
  )

  override val devices: StateFlow<Collection<BluetoothPairedDevice>> = combine(
    service.getAll(),
    activeDeviceIds
  ) { pairedDevices, activeDevicesIds ->
    pairedDevices.filter { it.id in activeDevicesIds }
  }.stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = emptySet()
  )
}
