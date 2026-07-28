package com.srilakshmikanthanp.clipbird.peer.handshake.authentication

import com.srilakshmikanthanp.clipbird.common.Device
import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.packet.*
import com.srilakshmikanthanp.clipbird.utility.Nonce

class Authenticator(private val hostDeviceProvider: HostDeviceProvider) {
  suspend fun authenticate(channel: Channel, remoteDevice: Device): ExchangedNonce {
    val hostDevice = hostDeviceProvider.get()

    val localNonce = NoncePacket(Nonce.generateNonce())
    channel.sendPacket(localNonce)

    val remoteNonce = channel.nextPacket().asPacket<NoncePacket>()
    val localSign = SignaturePacket(Nonce.signNonce(hostDevice.privateKey, remoteNonce.nonce))
    channel.sendPacket(localSign)

    val remoteSign = channel.nextPacket().asPacket<SignaturePacket>()
    if (!Nonce.verifyNonce(remoteDevice.publicKey, localNonce.nonce, remoteSign.signature)) {
      throw AuthenticationException("Failed to verify remote signature for device ${remoteDevice.id}")
    }

    return ExchangedNonce(localNonce.nonce, remoteNonce.nonce)
  }
}
