package com.srilakshmikanthanp.clipbird.peer.client

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.paring.PairedDevice

class ConnectionInitiationDecider(private val hostDeviceProvider: HostDeviceProvider) {
  suspend fun shouldInitiateConnection(remoteDevice: PairedDevice): Boolean {
    return hostDeviceProvider.get().id > remoteDevice.id
  }
}
