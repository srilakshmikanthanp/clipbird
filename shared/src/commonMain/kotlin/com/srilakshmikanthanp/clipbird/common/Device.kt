package com.srilakshmikanthanp.clipbird.common

import java.security.PublicKey

interface Device {
  val id: Long
  val name: String
  val publicKey: PublicKey
}
