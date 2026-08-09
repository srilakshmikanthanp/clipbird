package com.srilakshmikanthanp.clipbird.pairing

import com.srilakshmikanthanp.clipbird.common.Device
import java.security.cert.X509Certificate

open class PairedDevice(
  override val id: Long,
  override val name: String,
  override val certificate: X509Certificate,
) : Device {
  override fun hashCode(): Int = id.hashCode()
  override fun equals(other: Any?): Boolean = other is PairedDevice && id == other.id
}
