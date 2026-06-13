package com.srilakshmikanthanp.clipbird.paring

import kotlinx.coroutines.flow.StateFlow

interface PairedDeviceProvider<D: PairedDevice> {
  val devices: StateFlow<Collection<D>>
}