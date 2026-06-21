package com.srilakshmikanthanp.clipbird.client

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.paring.PairedDevice

interface ClientServerConnector<T: PairedDevice> {
  suspend fun connect(pairedDevice: T) : Channel;
}
