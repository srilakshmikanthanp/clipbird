package com.srilakshmikanthanp.clipbird.pairing

import kotlinx.coroutines.flow.Flow

interface PairedDeviceService<T : PairedDevice> {
  suspend fun findById(id: Long): T?
  suspend fun getAllOneOff(): List<T>
  fun getAll(): Flow<List<T>>
  suspend fun delete(id: Long)
  suspend fun upsert(device: T)
}
