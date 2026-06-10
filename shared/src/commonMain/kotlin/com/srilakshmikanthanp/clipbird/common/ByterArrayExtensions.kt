package com.srilakshmikanthanp.clipbird.common

import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec

fun ByteArray.toPublicKey(): PublicKey {
  val spec = X509EncodedKeySpec(this)
  return KeyFactory
    .getInstance("RSA")
    .generatePublic(spec)
}