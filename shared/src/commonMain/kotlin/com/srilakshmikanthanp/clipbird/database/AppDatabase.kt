package com.srilakshmikanthanp.clipbird.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceEntity
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceEntity

@Database(entities = [PairedDeviceEntity::class, BluetoothPairedDeviceEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
}
