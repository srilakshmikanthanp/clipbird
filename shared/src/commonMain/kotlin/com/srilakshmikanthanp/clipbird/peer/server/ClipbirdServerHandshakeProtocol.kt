package com.srilakshmikanthanp.clipbird.peer.server

import com.srilakshmikanthanp.clipbird.peer.authentication.AuthenticationException
import com.srilakshmikanthanp.clipbird.peer.authentication.Authenticator
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.EncryptedChannel
import com.srilakshmikanthanp.clipbird.packet.ErrorPacket
import com.srilakshmikanthanp.clipbird.packet.ErrorPacket.ErrorCode
import com.srilakshmikanthanp.clipbird.packet.IdentityPacket
import com.srilakshmikanthanp.clipbird.packet.asPacket
import com.srilakshmikanthanp.clipbird.packet.nextPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.peer.HandshakeProtocol
import com.srilakshmikanthanp.clipbird.peer.PeerException

class ClipbirdServerHandshakeProtocol<T: PairedDevice>(
  private val pairedDeviceService: PairedDeviceService<T>,
  private val authenticator: Authenticator,
) : HandshakeProtocol() {
  suspend fun handshake(channel: Channel): Pair<T, Channel> {
    try {
      val remoteDeviceId = channel.nextPacket().asPacket<IdentityPacket>().deviceId
      val remoteDevice = pairedDeviceService.findById(remoteDeviceId) ?: throw PeerException(ErrorCode.DEVICE_NOT_PAIRED, "Device with id $remoteDeviceId is not paired")
      val exchangedNonce = authenticator.authenticate(channel, remoteDevice)
      val symmetricKey = super.exchangeKeys(channel, exchangedNonce)
      return remoteDevice to EncryptedChannel(channel, symmetricKey)
    } catch (e: PeerException) {
      channel.sendPacket(e.toErrorPacket())
      throw e
    }
  }
}
