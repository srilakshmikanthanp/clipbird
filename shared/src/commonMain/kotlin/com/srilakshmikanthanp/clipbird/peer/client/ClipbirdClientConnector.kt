package com.srilakshmikanthanp.clipbird.peer.client

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice

interface ClipbirdClientConnector<T : PairedDevice> {
  suspend fun connect(pairedDevice: T): Channel
}
