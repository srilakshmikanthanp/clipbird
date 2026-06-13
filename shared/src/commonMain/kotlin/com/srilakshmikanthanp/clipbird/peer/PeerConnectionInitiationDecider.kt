package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.common.HostDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDevice

class PeerConnectionInitiationDecider(private val hostDevice: HostDevice) {
  fun shouldInitiateConnection(remoteDevice: PairedDevice): Boolean {
    return hostDevice.id < remoteDevice.id
  }
}
