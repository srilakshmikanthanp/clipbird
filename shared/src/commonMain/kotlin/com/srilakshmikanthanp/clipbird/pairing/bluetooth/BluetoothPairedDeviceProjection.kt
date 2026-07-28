package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import com.srilakshmikanthanp.clipbird.common.toPublicKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class BluetoothPairedDeviceProjection (
  val id: Long,
  val name: String,
  val publicKey: ByteArray,
  val address: String,
)

@OptIn(ExperimentalUuidApi::class)
fun BluetoothPairedDeviceProjection.toBluetoothPairedDevice(): BluetoothPairedDevice {
  return BluetoothPairedDevice(
    id = this.id,
    name = this.name,
    publicKey = this.publicKey.toPublicKey(),
    address = this.address,
  )
}
