package com.srilakshmikanthanp.clipbird.authentication

import com.srilakshmikanthanp.clipbird.common.Device
import com.srilakshmikanthanp.clipbird.common.HostDevice
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.packet.NoncePacket
import com.srilakshmikanthanp.clipbird.packet.SignaturePacket
import com.srilakshmikanthanp.clipbird.packet.asNoncePacket
import com.srilakshmikanthanp.clipbird.packet.asSignaturePacket
import com.srilakshmikanthanp.clipbird.packet.nextPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket
import com.srilakshmikanthanp.clipbird.utility.Nonce

class Authenticator(private val hostDevice: HostDevice) {
  suspend fun authenticate(channel: Channel, remoteDevice: Device) {
    // Generate local nonce and send it to the remote device
    val localNonce = NoncePacket(Nonce.generateNonce()).also { channel.sendPacket(it) }

    // Receive remote nonce, sign it with the host device's
    // private key and send the signature back to the remote
    // device
    val remoteNonce = channel.nextPacket().asNoncePacket()
    val localSign = SignaturePacket(Nonce.signNonce(hostDevice.privateKey, remoteNonce.nonce))
    channel.sendPacket(localSign)

    // Receive the signature of the remote nonce and verify
    // it using the remote device's public key
    val remoteSign = channel.nextPacket().asSignaturePacket()

    if (!Nonce.verifyNonce(remoteDevice.publicKey, localNonce.nonce, remoteSign.signature)) {
      throw AuthenticationException("Failed to verify remote signature for device ${remoteDevice.id}");
    }
  }
}
