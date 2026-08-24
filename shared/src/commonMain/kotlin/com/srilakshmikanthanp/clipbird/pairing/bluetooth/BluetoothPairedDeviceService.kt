package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import androidx.room.Transaction
import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceEntity
import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceEntityDao
import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BluetoothPairedDeviceService(
  private val pairedDao: PairedDeviceEntityDao,
  private val bleDao: BluetoothPairedDeviceDao,
) : PairedDeviceService<BluetoothPairedDevice> {
  @Transaction
  private suspend fun upsert(pairedDevice: PairedDeviceEntity, blePairedDevice: BluetoothPairedDeviceEntity) {
    pairedDao.upsert(pairedDevice)
    bleDao.upsert(blePairedDevice)
  }

  override suspend fun findById(id: ULong): BluetoothPairedDevice? {
    return bleDao.findById(id.toLong())?.toBluetoothPairedDevice()
  }

  override suspend fun getAllOneOff(): List<BluetoothPairedDevice> {
    return bleDao.getAllOneOff().map { it.toBluetoothPairedDevice() }
  }

  override fun getAll(): Flow<List<BluetoothPairedDevice>> {
    return bleDao.getAll().map { bleDevices -> bleDevices.map { it.toBluetoothPairedDevice() } }
  }

  override suspend fun upsert(device: BluetoothPairedDevice) {
    val pairedDeviceEntity = PairedDeviceEntity(id = device.id.toLong(), name = device.name, certificate = device.certificate.encoded)
    val blePairedDeviceEntity = BluetoothPairedDeviceEntity(id = device.id.toLong(), address = device.address)
    upsert(pairedDeviceEntity, blePairedDeviceEntity)
  }

  override suspend fun delete(id: ULong) {
    pairedDao.deleteById(id.toLong())
  }
}
