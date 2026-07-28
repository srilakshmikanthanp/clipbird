package com.srilakshmikanthanp.clipbird.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.srilakshmikanthanp.clipbird.common.HostDeviceDao
import com.srilakshmikanthanp.clipbird.common.HostDeviceEntity
import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceEntity
import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceEntityDao
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDeviceDao
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDeviceEntity

@Database(entities = [PairedDeviceEntity::class, BluetoothPairedDeviceEntity::class, HostDeviceEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun hostDeviceDao(): HostDeviceDao
  abstract fun pairedDeviceDao(): PairedDeviceEntityDao
  abstract fun bluetoothPairedDeviceDao(): BluetoothPairedDeviceDao
}
