package com.srilakshmikanthanp.clipbird.paring

import com.srilakshmikanthanp.clipbird.common.Device
import java.security.PublicKey

open class PairedDevice(
  override val id: Long,
  override val name: String,
  override val publicKey: PublicKey,
) : Device
