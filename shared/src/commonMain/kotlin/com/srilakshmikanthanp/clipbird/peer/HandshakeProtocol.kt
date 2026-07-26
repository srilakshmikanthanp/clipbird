package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.packet.EphemeralKeyPacket
import com.srilakshmikanthanp.clipbird.packet.asPacket
import com.srilakshmikanthanp.clipbird.packet.nextPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket
import com.srilakshmikanthanp.clipbird.peer.authentication.ExchangedNonce
import com.srilakshmikanthanp.clipbird.utility.ByteArrayComparator
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

abstract class HandshakeProtocol {
  protected suspend fun exchangeKeys(channel: Channel, exchangedNonce: ExchangedNonce): ByteArray {
    val keyPair = KeyPairGenerator.getInstance("X25519").generateKeyPair()
    channel.sendPacket(EphemeralKeyPacket(keyPair.public.encoded))

    val ephemeralKeyPacket = channel.nextPacket().asPacket<EphemeralKeyPacket>()
    val remotePublicKey = KeyFactory.getInstance("X25519").generatePublic(X509EncodedKeySpec(ephemeralKeyPacket.publicKey))

    val agreement = KeyAgreement.getInstance("X25519")
    agreement.init(keyPair.private)
    agreement.doPhase(remotePublicKey, true)
    val sharedSecret = agreement.generateSecret()

    val (lo, hi) = if (ByteArrayComparator.compare(exchangedNonce.local, exchangedNonce.remote) < 0) {
      exchangedNonce.local to exchangedNonce.remote
    } else {
      exchangedNonce.remote to exchangedNonce.local
    }

    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(sharedSecret, "HmacSHA256"))
    mac.update(lo)
    mac.update(hi)
    return mac.doFinal()
  }
}