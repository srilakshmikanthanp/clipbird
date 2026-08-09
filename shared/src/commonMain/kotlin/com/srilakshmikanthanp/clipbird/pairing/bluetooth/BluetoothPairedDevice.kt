package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import java.security.cert.X509Certificate

class BluetoothPairedDevice(
  id: Long,
  name: String,
  certificate: X509Certificate,
  val address: String
) : PairedDevice(
  id = id,
  name = name,
  certificate = certificate,
)
