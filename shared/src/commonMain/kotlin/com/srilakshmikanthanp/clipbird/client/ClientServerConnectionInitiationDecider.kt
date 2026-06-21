package com.srilakshmikanthanp.clipbird.client

import com.srilakshmikanthanp.clipbird.common.HostDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDevice

class ClientServerConnectionInitiationDecider(private val hostDevice: HostDevice) {
  fun shouldInitiateConnection(remoteDevice: PairedDevice): Boolean {
    return hostDevice.id < remoteDevice.id
  }
}
