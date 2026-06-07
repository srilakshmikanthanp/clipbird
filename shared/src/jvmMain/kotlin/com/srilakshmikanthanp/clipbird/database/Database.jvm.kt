package com.srilakshmikanthanp.clipbird.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.nio.file.Paths

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
  val dbFile = Paths.get(System.getProperty("user.home"), ".clipbird", "clipbird.db").toFile()
  return Room.databaseBuilder<AppDatabase>(
    name = dbFile.absolutePath,
  )
}
