package com.srilakshmikanthanp.clipbird.utility

import java.security.MessageDigest
import java.security.cert.X509Certificate

fun certificateFingerprint(certificate: X509Certificate): String {
  val digest = MessageDigest.getInstance("SHA-256").digest(certificate.encoded)
  return digest.joinToString(":") { "%02X".format(it) }
}
