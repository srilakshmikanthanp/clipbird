package com.srilakshmikanthanp.clipbird.common

import java.security.cert.X509Certificate

interface Device {
  val id: ULong
  val name: String
  val certificate: X509Certificate
}
