package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import com.srilakshmikanthanp.clipbird.common.toCertificate
import kotlin.uuid.ExperimentalUuidApi

class BluetoothPairedDeviceProjection (
  val id: Long,
  val name: String,
  val certificate: ByteArray,
  val address: String,
)

fun BluetoothPairedDeviceProjection.toBluetoothPairedDevice(): BluetoothPairedDevice {
  return BluetoothPairedDevice(
    id = this.id.toULong(),
    name = this.name,
    certificate = this.certificate.toCertificate(),
    address = this.address,
  )
}
