package com.srilakshmikanthanp.clipbird.pairing

import kotlinx.coroutines.flow.Flow

interface ActivePairedDeviceProvider<P : PairedDevice> {
  val devices: Flow<Collection<P>>
}