package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.hub.Discoverer
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent
import kotlinx.coroutines.flow.Flow

expect class BleDiscoverer : Discoverer<BleHubDevice> {
  override val events: Flow<DiscoveryEvent<BleHubDevice>>
}
