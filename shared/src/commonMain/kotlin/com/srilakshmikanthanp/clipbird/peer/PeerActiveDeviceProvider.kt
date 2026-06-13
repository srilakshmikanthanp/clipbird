package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import kotlinx.coroutines.flow.StateFlow

interface PeerActiveDeviceProvider<P : PairedDevice> {
  val devices: StateFlow<Collection<P>>
}
