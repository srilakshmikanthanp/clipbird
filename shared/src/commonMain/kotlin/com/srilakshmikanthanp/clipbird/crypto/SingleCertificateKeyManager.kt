package com.srilakshmikanthanp.clipbird.crypto

import java.net.Socket
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate
import javax.net.ssl.SSLEngine
import javax.net.ssl.X509ExtendedKeyManager

class SingleCertificateKeyManager(
  private val privateKey: PrivateKey,
  private val certificate: X509Certificate,
) : X509ExtendedKeyManager() {
  private val chain = arrayOf(certificate)

  override fun chooseClientAlias(keyType: Array<out String>?, issuers: Array<out Principal>?, socket: Socket?): String = ALIAS
  override fun getClientAliases(keyType: String?, issuers: Array<out Principal>?): Array<String> = arrayOf(ALIAS)
  override fun chooseEngineClientAlias(keyType: Array<out String>?, issuers: Array<out Principal>?, engine: SSLEngine?): String = ALIAS
  override fun getServerAliases(keyType: String?, issuers: Array<out Principal>?): Array<String> = arrayOf(ALIAS)
  override fun chooseServerAlias(keyType: String?, issuers: Array<out Principal>?, socket: Socket?): String = ALIAS
  override fun chooseEngineServerAlias(keyType: String?, issuers: Array<out Principal>?, engine: SSLEngine?): String = ALIAS
  override fun getCertificateChain(alias: String?): Array<X509Certificate> = chain
  override fun getPrivateKey(alias: String?): PrivateKey = privateKey

  private companion object {
    const val ALIAS = "clipbird"
  }
}
