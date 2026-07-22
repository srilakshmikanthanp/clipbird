package com.srilakshmikanthanp.clipbird.paring

import kotlinx.coroutines.flow.Flow

interface PairedDeviceService<T : PairedDevice> {
  suspend fun findById(id: Long): T?
  fun getAll(): Flow<List<T>>
  suspend fun delete(id: Long)
  suspend fun upsert(device: T)
}
