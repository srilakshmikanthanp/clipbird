package com.srilakshmikanthanp.clipbird.common

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface HostDeviceDao {
  @Upsert
  suspend fun upsert(entity: HostDeviceEntity)

  @Query("SELECT * FROM host_device WHERE singleton = 0")
  suspend fun get(): HostDeviceEntity?
}
