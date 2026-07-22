package com.srilakshmikanthanp.clipbird.utility

import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature

object Nonce {
  private val signature = Signature.getInstance(KeyAlgorithm.KEY_ALGORITHM)

  fun generateNonce(size: Int = 32): ByteArray {
    val nonce = ByteArray(size)
    SecureRandom().nextBytes(nonce)
    return nonce
  }

  fun signNonce(privateKey: PrivateKey, nonce: ByteArray): ByteArray {
    signature.initSign(privateKey)
    signature.update(nonce)
    return signature.sign()
  }

  fun verifyNonce(publicKey: PublicKey, nonce: ByteArray, bytes: ByteArray): Boolean {
    signature.initVerify(publicKey)
    signature.update(nonce)
    return signature.verify(bytes)
  }
}
