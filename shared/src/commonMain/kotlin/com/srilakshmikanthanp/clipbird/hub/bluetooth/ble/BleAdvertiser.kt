package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.hub.Advertiser

expect class BleAdvertiser : Advertiser {
  override suspend fun advertise()
}
