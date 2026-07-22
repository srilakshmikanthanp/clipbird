package com.srilakshmikanthanp.clipbird.paring

import kotlinx.coroutines.flow.StateFlow

interface ActivePairedDeviceProvider<P : PairedDevice> {
  val devices: StateFlow<Collection<P>>
}