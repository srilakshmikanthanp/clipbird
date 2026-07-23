package com.srilakshmikanthanp.clipbird.paring

import kotlinx.coroutines.flow.Flow

interface ActivePairedDeviceProvider<P : PairedDevice> {
  val devices: Flow<Collection<P>>
}