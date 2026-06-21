package com.srilakshmikanthanp.clipbird.client

import com.srilakshmikanthanp.clipbird.authentication.Authenticator
import com.srilakshmikanthanp.clipbird.common.HostDevice
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.packet.IdentityPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket
import com.srilakshmikanthanp.clipbird.paring.PairedDevice

class ClientServerHandshakeProtocol(
  private val authenticator: Authenticator,
  private val hostDevice: HostDevice
) {
  suspend fun handshake(channel: Channel, remoteDevice: PairedDevice) {
    channel.sendPacket(IdentityPacket(hostDevice.id))
    authenticator.authenticate(channel, remoteDevice)
  }
}
