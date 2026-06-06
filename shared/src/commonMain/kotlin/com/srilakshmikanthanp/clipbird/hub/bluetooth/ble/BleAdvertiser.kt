package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.hub.Advertiser
import kotlinx.coroutines.flow.StateFlow

expect class BleAdvertiser : Advertiser<BleHubDevice> {
  override val advertisedDevice: StateFlow<BleHubDevice?>
  override suspend fun startAdvertising()
  override suspend fun stopAdvertising()
}
