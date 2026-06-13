package com.srilakshmikanthanp.clipbird.paring

import kotlinx.coroutines.flow.StateFlow

interface PairedActiveDeviceProvider<P : PairedDevice> {
  val devices: StateFlow<Collection<P>>
}