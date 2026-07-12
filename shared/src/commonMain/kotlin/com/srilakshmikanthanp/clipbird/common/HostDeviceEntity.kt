package com.srilakshmikanthanp.clipbird.common

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "host_device")
class HostDeviceEntity(
  @PrimaryKey(autoGenerate = false) val singleton: Int = 0,
  val id: Long,
  val name: String,
  val publicKey: ByteArray,
  val privateKey: ByteArray,
)

fun HostDeviceEntity.toHostDevice(): HostDevice {
  return HostDevice(id, name, publicKey.toPublicKey(), privateKey.toPrivateKey())
}
