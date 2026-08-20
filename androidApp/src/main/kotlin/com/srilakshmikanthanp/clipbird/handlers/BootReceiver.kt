package com.srilakshmikanthanp.clipbird.handlers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.srilakshmikanthanp.clipbird.extension.hasRequiredPermissions
import com.srilakshmikanthanp.clipbird.extension.isBatteryOptimizationDisabled
import com.srilakshmikanthanp.clipbird.extension.startClipbirdService

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
    if (!context.hasRequiredPermissions() || !context.isBatteryOptimizationDisabled()) return
    context.startClipbirdService()
  }
}
