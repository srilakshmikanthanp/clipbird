package com.srilakshmikanthanp.clipbird.pairing

import kotlinx.coroutines.flow.Flow

interface PairedDeviceService<T : PairedDevice> {
  suspend fun findById(id: ULong): T?
  suspend fun getAllOneOff(): List<T>
  fun getAll(): Flow<List<T>>
  suspend fun delete(id: ULong)
  suspend fun upsert(device: T)
}
