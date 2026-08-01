package com.srilakshmikanthanp.clipbird.extension

import android.content.Context
import android.content.Intent
import android.os.Build
import com.srilakshmikanthanp.clipbird.ClipbirdService

fun Context.startClipbirdService() {
  val intent = Intent(this, ClipbirdService::class.java)
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    startForegroundService(intent)
  } else {
    startService(intent)
  }
}