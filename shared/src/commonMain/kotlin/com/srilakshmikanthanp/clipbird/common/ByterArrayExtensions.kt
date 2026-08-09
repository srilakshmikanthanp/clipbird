package com.srilakshmikanthanp.clipbird.common

import com.srilakshmikanthanp.clipbird.crypto.KeyPairs
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec

fun ByteArray.toPrivateKey(): PrivateKey {
  val spec = PKCS8EncodedKeySpec(this)
  return KeyFactory
    .getInstance(KeyPairs.KEY_ALGORITHM)
    .generatePrivate(spec)
}

fun ByteArray.toCertificate(): X509Certificate {
  return CertificateFactory
    .getInstance("X.509")
    .generateCertificate(inputStream()) as X509Certificate
}
