package com.srilakshmikanthanp.clipbird.paring.bluetooth

import androidx.room.Transaction
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceEntity
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceEntityDao
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi

class BluetoothPairedDeviceService(
  private val pairedDao: PairedDeviceEntityDao,
  private val bleDao: BluetoothPairedDeviceDao,
) : PairedDeviceService<BluetoothPairedDevice> {
  override suspend fun findById(id: Long): BluetoothPairedDevice? {
    return bleDao.findById(id)?.toBluetoothPairedDevice()
  }

  @OptIn(ExperimentalUuidApi::class)
  override fun getAll(): Flow<List<BluetoothPairedDevice>> {
    return bleDao.getAll().map { bleDevices -> bleDevices.map { it.toBluetoothPairedDevice() } }
  }

  @Transaction
  suspend fun upsert(pairedDevice: PairedDeviceEntity, blePairedDevice: BluetoothPairedDeviceEntity) {
    pairedDao.upsert(pairedDevice)
    bleDao.upsert(blePairedDevice)
  }

  @Transaction
  override suspend fun delete(id: Long) {
    pairedDao.deleteById(id)
  }
}
