package com.srilakshmikanthanp.clipbird.peer.client

import com.srilakshmikanthanp.clipbird.peer.authentication.AuthenticationException
import com.srilakshmikanthanp.clipbird.peer.authentication.Authenticator
import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.EncryptedChannel
import com.srilakshmikanthanp.clipbird.packet.ErrorPacket
import com.srilakshmikanthanp.clipbird.packet.IdentityPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.HandshakeProtocol
import com.srilakshmikanthanp.clipbird.peer.PeerException

class ClientServerHandshakeProtocol(
  private val authenticator: Authenticator,
  private val hostDeviceProvider: HostDeviceProvider
) : HandshakeProtocol() {
  suspend fun handshake(channel: Channel, remoteDevice: PairedDevice): Channel {
    try {
      channel.sendPacket(IdentityPacket(hostDeviceProvider.get().id))
      val exchangedNonce = authenticator.authenticate(channel, remoteDevice)
      val symmetricKey = super.exchangeKeys(channel, exchangedNonce)
      return EncryptedChannel(channel, symmetricKey)
    } catch (e: PeerException) {
      channel.sendPacket(e.toErrorPacket())
      throw e
    }
  }
}