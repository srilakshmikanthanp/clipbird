package com.srilakshmikanthanp.clipbird.crypto

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.KeyUsage
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.security.KeyPair
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Date
import java.util.concurrent.TimeUnit

object SelfSignedCertificates {
  private const val SERIAL_BITS = 128
  private val backDateMillis = TimeUnit.DAYS.toMillis(365)
  private val lifetimeMillis = TimeUnit.DAYS.toMillis(10L * 365)
  private val provider = BouncyCastleProvider()
  private val random = SecureRandom()

  fun create(keyPair: KeyPair, subject: String): X509Certificate {
    val name = X500Name("CN=$subject")
    val now = System.currentTimeMillis()
    val notBefore = Date(now - backDateMillis)
    val notAfter = Date(now + lifetimeMillis)

    val builder = JcaX509v3CertificateBuilder(
      name,
      BigInteger(SERIAL_BITS, random),
      notBefore,
      notAfter,
      name,
      keyPair.public,
    )

    builder.addExtension(
      Extension.basicConstraints,
      true,
      BasicConstraints(0)
    )

    builder.addExtension(
      Extension.keyUsage,
      true,
      KeyUsage(KeyUsage.digitalSignature or KeyUsage.keyCertSign),
    )

    val signer = JcaContentSignerBuilder(KeyPairs.SIGNATURE_ALGORITHM)
      .setProvider(provider)
      .build(keyPair.private)

    return JcaX509CertificateConverter()
      .getCertificate(builder.build(signer))
  }
}
