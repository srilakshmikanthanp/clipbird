package com.srilakshmikanthanp.clipbird

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class AppModule {
  @Single
  fun provideAppRuntime(): AppRuntime {
    return AppRuntime()
  }
}
