package com.srilakshmikanthanp.clipbird.paring.bluetooth

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceEntity

@Entity(
  tableName = "ble_paired_device",
  foreignKeys = [
    ForeignKey(
      entity = PairedDeviceEntity::class,
      parentColumns = ["id"],
      childColumns = ["id"],
      onDelete = ForeignKey.CASCADE
    )
  ]
)
class BluetoothPairedDeviceEntity(
  @PrimaryKey(autoGenerate = false) val id: Long,
  val address: String,
)
