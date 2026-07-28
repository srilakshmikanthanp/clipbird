package com.srilakshmikanthanp.clipbird.pairing

import kotlinx.coroutines.flow.StateFlow

interface PairedDeviceProvider<D: PairedDevice> {
  val devices: StateFlow<Collection<D>>
}