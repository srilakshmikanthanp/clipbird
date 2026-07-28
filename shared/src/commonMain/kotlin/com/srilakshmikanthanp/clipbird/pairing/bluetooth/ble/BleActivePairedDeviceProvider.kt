package com.srilakshmikanthanp.clipbird.pairing.bluetooth.ble

import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Found
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Lost
import com.srilakshmikanthanp.clipbird.hub.Discoverer
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleHubDevice
import com.srilakshmikanthanp.clipbird.pairing.ActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDeviceService
import kotlinx.coroutines.flow.*

class BleActivePairedDeviceProvider(
  discoverer: Discoverer<BleHubDevice>,
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
