package com.srilakshmikanthanp.clipbird

import android.app.Application
import com.srilakshmikanthanp.clipbird.notification.NotificationModule
import org.koin.core.context.loadKoinModules
import org.koin.ksp.generated.module

class ClipbirdApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    initKoin(this)
    loadKoinModules(NotificationModule().module)
  }
}
