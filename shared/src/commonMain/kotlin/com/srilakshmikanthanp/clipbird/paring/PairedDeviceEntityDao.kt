package com.srilakshmikanthanp.clipbird.paring

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface PairedDeviceEntityDao {
  @Upsert
  suspend fun upsert(device: PairedDeviceEntity)
  @Delete
  suspend fun delete(device: PairedDeviceEntity)
  @Query("DELETE FROM paired_device WHERE id = :id")
  suspend fun deleteById(id: Long)
}
