package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.paring.PairedDevice

interface PeerConnector<T: PairedDevice> {
  suspend fun connect(pairedDevice: T) : Channel;
}
