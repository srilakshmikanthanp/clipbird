package com.srilakshmikanthanp.clipbird.crypto

import java.net.Socket
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLEngine
import javax.net.ssl.X509ExtendedTrustManager

@Suppress("CustomX509TrustManager")
class PeerTrustManager(
  private val delegate: X509ExtendedTrustManager,
) : X509ExtendedTrustManager() {
  private fun validate(chain: Array<out X509Certificate>?): Array<X509Certificate> {
    val certificates = chain?.takeIf { it.isNotEmpty() } ?: throw CertificateException("Peer presented no certificate")
    certificates.forEach(X509Certificate::checkValidity)
    @Suppress("UNCHECKED_CAST")
    return certificates as Array<X509Certificate>
  }

  override fun checkClientTrusted(
    chain: Array<out X509Certificate>?,
    authType: String?,
  ) = delegate.checkClientTrusted(validate(chain), authType)

  override fun checkClientTrusted(
    chain: Array<out X509Certificate>?,
    authType: String?,
    socket: Socket?,
  ) = delegate.checkClientTrusted(validate(chain), authType, socket)

  override fun checkClientTrusted(
    chain: Array<out X509Certificate>?,
    authType: String?,
    engine: SSLEngine?,
  ) = delegate.checkClientTrusted(validate(chain), authType, engine)

  override fun checkServerTrusted(
    chain: Array<out X509Certificate>?,
    authType: String?,
  ) = delegate.checkServerTrusted(validate(chain), authType)

  override fun checkServerTrusted(
    chain: Array<out X509Certificate>?,
    authType: String?,
    socket: Socket?,
  ) = delegate.checkServerTrusted(validate(chain), authType, socket)

  override fun checkServerTrusted(
    chain: Array<out X509Certificate>?,
    authType: String?,
    engine: SSLEngine?,
  ) = delegate.checkServerTrusted(validate(chain), authType, engine)

  override fun getAcceptedIssuers(): Array<X509Certificate> = delegate.acceptedIssuers
}