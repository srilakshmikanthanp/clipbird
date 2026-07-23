package com.srilakshmikanthanp.clipbird.paring.bluetooth.ble

import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Found
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Lost
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleDiscoverer
import com.srilakshmikanthanp.clipbird.paring.ActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceService
import kotlinx.coroutines.flow.*

class BleActivePairedDeviceProvider(
  discoverer: BleDiscoverer,
  service: BluetoothPairedDeviceService,
) : ActivePairedDeviceProvider<BluetoothPairedDevice> {
  private val activeDeviceIds: Flow<Set<Long>> = discoverer.events.scan(emptySet()) { activeDevicesIds, event ->
    when (event) {
      is Found -> activeDevicesIds + event.device.id
      is Lost -> activeDevicesIds - event.device.id
    }
  }

  override val devices: Flow<Collection<BluetoothPairedDevice>> = combine(
    service.getAll(),
    activeDeviceIds
  ) { pairedDevices, activeDevicesIds ->
    pairedDevices.filter { it.id in activeDevicesIds }
  }
}
