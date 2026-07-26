package com.srilakshmikanthanp.clipbird

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class AppModule {
  @Single
  fun provideAppRuntime(): AppRuntime {
    return AppRuntime()
  }

  @Single
  fun provideApplicationScope(): CoroutineScope {
    return CoroutineScope(Dispatchers.IO + SupervisorJob())
  }
}
