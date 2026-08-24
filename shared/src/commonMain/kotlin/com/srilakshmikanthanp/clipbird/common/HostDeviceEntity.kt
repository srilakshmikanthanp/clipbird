package com.srilakshmikanthanp.clipbird.common

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "host_device")
class HostDeviceEntity(
  @PrimaryKey(autoGenerate = false) val singleton: Int = 0,
  val id: Long,
  val name: String,
  val privateKey: ByteArray,
  val certificate: ByteArray,
)

fun HostDeviceEntity.toHostDevice(): HostDevice {
  return HostDevice(id.toULong(), name, certificate.toCertificate(), privateKey.toPrivateKey())
}
