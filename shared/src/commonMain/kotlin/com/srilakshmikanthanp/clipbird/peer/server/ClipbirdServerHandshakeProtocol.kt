package com.srilakshmikanthanp.clipbird.peer.server

import com.srilakshmikanthanp.clipbird.authentication.Authenticator
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.packet.ErrorPacket
import com.srilakshmikanthanp.clipbird.packet.ErrorPacket.ErrorCode
import com.srilakshmikanthanp.clipbird.packet.IdentityPacket
import com.srilakshmikanthanp.clipbird.packet.asPacket
import com.srilakshmikanthanp.clipbird.packet.nextPacket
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.peer.PeerException

class ClipbirdServerHandshakeProtocol<T: PairedDevice>(
  private val pairedDeviceService: PairedDeviceService<T>,
  private val authenticator: Authenticator,
) {
  suspend fun handshake(channel: Channel): T {
    val remoteDeviceId = channel.nextPacket().asPacket<IdentityPacket>().deviceId
    val remoteDevice = pairedDeviceService.findById(remoteDeviceId) ?: throw PeerException(ErrorCode.DEVICE_NOT_PAIRED, "Device with id $remoteDeviceId is not paired")
    authenticator.authenticate(channel, remoteDevice)
    return remoteDevice
  }
}
