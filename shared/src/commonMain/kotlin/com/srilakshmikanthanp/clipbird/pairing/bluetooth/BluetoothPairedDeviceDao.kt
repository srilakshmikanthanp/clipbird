package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BluetoothPairedDeviceDao {
  @Query(
    """
        SELECT
            p.id, p.name, p.publicKey, b.address
        FROM paired_device p
        JOIN ble_paired_device b
            ON p.id = b.id
    """
  )
  fun getAll(): Flow<List<BluetoothPairedDeviceProjection>>

  @Query(
    """
        SELECT
            p.id, p.name, p.publicKey, b.address
        FROM paired_device p
        JOIN ble_paired_device b
            ON p.id = b.id
        WHERE p.id = :id
    """
  )
  suspend fun findById(id: Long): BluetoothPairedDeviceProjection?

  @Upsert
  suspend fun upsert(device: BluetoothPairedDeviceEntity)

  @Delete
  suspend fun delete(device: BluetoothPairedDeviceEntity)
}
