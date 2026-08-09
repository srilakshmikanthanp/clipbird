package com.srilakshmikanthanp.clipbird.crypto

import java.security.PrivateKey
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.TrustManager

internal object SslEngines {
  const val PROTOCOL = "TLSv1.3"

  fun create(
    privateKey: PrivateKey,
    certificate: X509Certificate,
    trustManager: TrustManager,
    client: Boolean
  ): SSLEngine {
    val context = SSLContext.getInstance(PROTOCOL, tlsProvider())

    context.init(
      arrayOf(SingleCertificateKeyManager(privateKey, certificate)),
      arrayOf(trustManager),
      SecureRandom(),
    )

    return context.createSSLEngine().apply {
      enabledProtocols = arrayOf(PROTOCOL)
      useClientMode = client
      needClientAuth = !client
    }
  }
}
