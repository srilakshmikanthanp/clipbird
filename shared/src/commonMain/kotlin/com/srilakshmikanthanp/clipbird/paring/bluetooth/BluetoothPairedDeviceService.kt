package com.srilakshmikanthanp.clipbird.paring.bluetooth

import androidx.room.Transaction
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceEntityDao
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceEntity
import kotlinx.coroutines.flow.Flow

class BluetoothPairedDeviceService(
  private val pairedDao: PairedDeviceEntityDao,
  private val bleDao: BluetoothPairedDeviceDao,
) {
  fun getAll(): Flow<List<BluetoothPairedDeviceProjection>> {
    return bleDao.getAll()
  }

  @Transaction
  suspend fun upsert(pairedDevice: PairedDeviceEntity, blePairedDevice: BluetoothPairedDeviceEntity) {
    pairedDao.upsert(pairedDevice)
    bleDao.upsert(blePairedDevice)
  }

  @Transaction
  suspend fun delete(id: Long) {
    pairedDao.deleteById(id)
  }
}
