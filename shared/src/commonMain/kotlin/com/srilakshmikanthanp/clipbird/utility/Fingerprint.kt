package com.srilakshmikanthanp.clipbird.utility

import java.security.MessageDigest
import java.security.PublicKey

fun publicKeyFingerprint(publicKey: PublicKey): String {
  val digest = MessageDigest.getInstance("SHA-256").digest(publicKey.encoded)
  return digest.joinToString(":") { "%02X".format(it) }
}