package com.srilakshmikanthanp.clipbird.peer.client

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.EncryptedChannel
import com.srilakshmikanthanp.clipbird.packet.IdentityPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.PeerConnection
import com.srilakshmikanthanp.clipbird.peer.PeerException
import com.srilakshmikanthanp.clipbird.peer.handshake.HandshakeProtocol
import com.srilakshmikanthanp.clipbird.peer.handshake.authentication.Authenticator

class ClipbirdClientHandshakeProtocol(
  private val authenticator: Authenticator,
  private val hostDeviceProvider: HostDeviceProvider
) : HandshakeProtocol() {
  suspend fun handshake(channel: Channel, remoteDevice: PairedDevice): PeerConnection {
    try {
      channel.sendPacket(IdentityPacket(hostDeviceProvider.get().id))
      val exchangedNonce = authenticator.authenticate(channel, remoteDevice)
      val symmetricKey = super.exchangeKeys(channel, exchangedNonce)
      return PeerConnection(remoteDevice, EncryptedChannel(channel, symmetricKey))
    } catch (e: PeerException) {
      channel.sendPacket(e.toErrorPacket())
      throw e
    }
  }
}
