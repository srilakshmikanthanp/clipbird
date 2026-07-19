package com.srilakshmikanthanp.clipbird.paring

import com.srilakshmikanthanp.clipbird.io.Channel

fun interface PairingResponder<T: Channel, D : PairedDevice> {
  suspend fun respond(channel: T): D
}
