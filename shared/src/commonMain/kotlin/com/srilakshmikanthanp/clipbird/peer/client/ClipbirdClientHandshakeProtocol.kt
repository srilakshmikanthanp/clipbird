package com.srilakshmikanthanp.clipbird.peer.client

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.TlsChannel
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.PeerConnection

class ClipbirdClientHandshakeProtocol(
  private val hostDeviceProvider: HostDeviceProvider,
) {
  suspend fun handshake(channel: Channel, remoteDevice: PairedDevice): PeerConnection {
    val secured = TlsChannel.client(channel, hostDeviceProvider.get(), listOf(remoteDevice.certificate))
    return PeerConnection(remoteDevice, secured)
  }
}
