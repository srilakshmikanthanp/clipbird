package com.srilakshmikanthanp.clipbird.paring

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paired_device")
class PairedDeviceEntity (
  @PrimaryKey(autoGenerate = false) val id : Long,
  val name : String,
  val publicKey: ByteArray,
)
