package com.srilakshmikanthanp.clipbird.crypto

import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509ExtendedTrustManager

object PeerTrustManagers {
  fun forCertificates(certificates: Collection<X509Certificate>): TrustManager {
    require(certificates.isNotEmpty()) { "At least one paired certificate is needed to accept a peer" }

    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
      load(null, null)
      certificates.forEachIndexed { index, certificate -> setCertificateEntry("peer-$index", certificate) }
    }

    val factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
      init(keyStore)
    }

    val delegate = factory.trustManagers
      .filterIsInstance<X509ExtendedTrustManager>()
      .firstOrNull() ?: throw IllegalStateException("The platform supplied no X509ExtendedTrustManager")

    return PeerTrustManager(delegate)
  }
}
