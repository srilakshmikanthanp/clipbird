package com.srilakshmikanthanp.clipbird.pairing

import com.srilakshmikanthanp.clipbird.io.Channel

fun interface PairingResponder<T: Channel, D : PairedDevice> {
  suspend fun respond(channel: T): D
}
