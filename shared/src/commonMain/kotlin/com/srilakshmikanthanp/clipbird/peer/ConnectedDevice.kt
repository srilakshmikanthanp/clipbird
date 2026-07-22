package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.paring.PairedDevice

class ConnectedDevice(
  val device: PairedDevice,
  val channel: Channel
)
