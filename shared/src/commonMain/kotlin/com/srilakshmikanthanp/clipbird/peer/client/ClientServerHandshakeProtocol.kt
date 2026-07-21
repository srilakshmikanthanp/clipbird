package com.srilakshmikanthanp.clipbird.peer.client

import com.srilakshmikanthanp.clipbird.authentication.Authenticator
import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.packet.IdentityPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket
import com.srilakshmikanthanp.clipbird.paring.PairedDevice

class ClientServerHandshakeProtocol(
  private val authenticator: Authenticator,
  private val hostDeviceProvider: HostDeviceProvider
) {
  suspend fun handshake(channel: Channel, remoteDevice: PairedDevice) {
    channel.sendPacket(IdentityPacket(hostDeviceProvider.get().id))
    authenticator.authenticate(channel, remoteDevice)
  }
}
