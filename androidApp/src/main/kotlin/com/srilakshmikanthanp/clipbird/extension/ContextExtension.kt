package com.srilakshmikanthanp.clipbird.extension

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import com.srilakshmikanthanp.clipbird.ClipbirdService

val requiredPermissions: Array<String> get() = buildList {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) add(permission.POST_NOTIFICATIONS)
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    add(permission.BLUETOOTH_CONNECT)
    add(permission.BLUETOOTH_SCAN)
    add(permission.BLUETOOTH_ADVERTISE)
  }
}.toTypedArray()

fun Context.hasRequiredPermissions() = requiredPermissions.all {
  checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
}

fun Context.isBatteryOptimizationDisabled(): Boolean {
  val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
  return powerManager.isIgnoringBatteryOptimizations(packageName)
}

fun Context.startClipbirdService() {
  val intent = Intent(this, ClipbirdService::class.java)
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    startForegroundService(intent)
  } else {
    startService(intent)
  }
}
