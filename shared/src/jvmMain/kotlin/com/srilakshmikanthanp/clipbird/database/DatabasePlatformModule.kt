package com.srilakshmikanthanp.clipbird.database

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DatabasePlatformModule {
  @Single
  fun appDatabase(): AppDatabase = getRoomDatabase(getDatabaseBuilder())
}
