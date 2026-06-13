package com.srilakshmikanthanp.clipbird.common

import java.security.PrivateKey
import java.security.PublicKey

class HostDevice (
  override val id: Long,
  override val name: String,
  override val publicKey: PublicKey,
  val privateKey: PrivateKey,
) : Device
