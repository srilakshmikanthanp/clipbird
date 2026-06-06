package com.srilakshmikanthanp.clipbird.hub

import kotlinx.coroutines.flow.StateFlow

interface Advertiser<T: HubDevice> {
  val advertisedDevice: StateFlow<T?>
  suspend fun startAdvertising()
  suspend fun stopAdvertising()
}
