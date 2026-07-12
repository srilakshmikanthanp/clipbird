package com.srilakshmikanthanp.clipbird

import android.app.Application

class ClipbirdApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    initKoin(this)
  }
}
