package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.io.Channel

interface BluetoothChannel : Channel {
  val remoteAddress: String
}
