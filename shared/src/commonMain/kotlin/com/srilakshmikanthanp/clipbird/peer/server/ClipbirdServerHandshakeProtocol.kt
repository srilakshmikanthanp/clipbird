package com.srilakshmikanthanp.clipbird.peer.server

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.TlsChannel
import com.srilakshmikanthanp.clipbird.packet.ErrorPacket.ErrorCode
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceService
import com.srilakshmikanthanp.clipbird.peer.PeerConnection
import com.srilakshmikanthanp.clipbird.peer.PeerException

class ClipbirdServerHandshakeProtocol<T : PairedDevice>(
  private val pairedDeviceService: PairedDeviceService<T>,
  private val hostDeviceProvider: HostDeviceProvider,
) {
  suspend fun handshake(channel: Channel): PeerConnection {
    val paired = pairedDeviceService.getAllOneOff()
    val host = hostDeviceProvider.get()

    if (paired.isEmpty()) {
      throw PeerException(ErrorCode.DEVICE_NOT_PAIRED, "Device is not paired")
    }

    val secured = TlsChannel.server(channel, host, paired.map { it.certificate })
    val device = paired.first { it.certificate.encoded.contentEquals(secured.peerCertificate.encoded) }
    return PeerConnection(device, secured)
  }
}
