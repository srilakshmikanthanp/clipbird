package com.srilakshmikanthanp.clipbird.database

import android.content.Context
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DatabasePlatformModule {
  @Single
  fun appDatabase(context: Context): AppDatabase = getRoomDatabase(getDatabaseBuilder(context))
}
