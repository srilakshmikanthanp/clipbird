package com.srilakshmikanthanp.clipbird.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceEntity
import com.srilakshmikanthanp.clipbird.paring.bluetooth.ble.BlePairedDeviceEntity

@Database(entities = [PairedDeviceEntity::class, BlePairedDeviceEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
}
