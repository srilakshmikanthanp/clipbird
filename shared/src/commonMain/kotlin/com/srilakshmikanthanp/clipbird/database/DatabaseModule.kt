package com.srilakshmikanthanp.clipbird.database

import com.srilakshmikanthanp.clipbird.common.HostDeviceDao
import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceEntityDao
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDeviceDao
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/** DAOs exposed from the [AppDatabase]. The database itself is provided by the platform module. */
@Module
class DatabaseModule {
  @Single
  fun hostDeviceDao(database: AppDatabase): HostDeviceDao = database.hostDeviceDao()

  @Single
  fun pairedDeviceDao(database: AppDatabase): PairedDeviceEntityDao = database.pairedDeviceDao()

  @Single
  fun bluetoothPairedDeviceDao(database: AppDatabase): BluetoothPairedDeviceDao =
    database.bluetoothPairedDeviceDao()
}
