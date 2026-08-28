package com.srilakshmikanthanp.clipbird.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.srilakshmikanthanp.clipbird.AppDirs

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
  val dbFile = AppDirs.home.resolve("clipbird.db").toFile()
  return Room.databaseBuilder<AppDatabase>(
    name = dbFile.absolutePath,
  )
}
