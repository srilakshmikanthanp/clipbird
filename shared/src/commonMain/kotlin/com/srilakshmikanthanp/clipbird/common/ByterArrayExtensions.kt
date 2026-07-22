package com.srilakshmikanthanp.clipbird.common

import com.srilakshmikanthanp.clipbird.utility.KeyAlgorithm
import com.srilakshmikanthanp.clipbird.utility.Nonce
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

fun ByteArray.toPublicKey(): PublicKey {
  val spec = X509EncodedKeySpec(this)
  return KeyFactory
    .getInstance(KeyAlgorithm.KEY_ALGORITHM)
    .generatePublic(spec)
}

fun ByteArray.toPrivateKey(): PrivateKey {
  val spec = PKCS8EncodedKeySpec(this)
  return KeyFactory
    .getInstance(KeyAlgorithm.KEY_ALGORITHM)
    .generatePrivate(spec)
}