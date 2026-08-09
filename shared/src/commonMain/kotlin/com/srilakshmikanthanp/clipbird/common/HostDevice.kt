package com.srilakshmikanthanp.clipbird.common

import java.security.PrivateKey
import java.security.cert.X509Certificate

class HostDevice (
  override val id: Long,
  override val name: String,
  override val certificate: X509Certificate,
  val privateKey: PrivateKey,
) : Device
