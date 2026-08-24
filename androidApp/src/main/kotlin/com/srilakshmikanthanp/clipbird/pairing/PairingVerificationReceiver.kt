package com.srilakshmikanthanp.clipbird.pairing

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.srilakshmikanthanp.clipbird.pairing.PairingDeferredVerifier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PairingVerificationReceiver : BroadcastReceiver(), KoinComponent {
  private val verifier: PairingDeferredVerifier by inject()

  override fun onReceive(context: Context, intent: Intent) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val rawId = intent.getLongExtra(EXTRA_DEVICE_ID, -1L)
    if (rawId == -1L) return
    val deviceId = rawId.toULong()

    when (intent.action) {
      ACTION_CONFIRM -> verifier.confirmById(deviceId)
      ACTION_REJECT -> verifier.rejectById(deviceId)
    }

    notificationManager.cancel(PairingNotificationManager.NOTIFICATION_ID)
  }

  companion object {
    const val EXTRA_DEVICE_ID = "device_id"
    const val ACTION_CONFIRM = "com.srilakshmikanthanp.clipbird.PAIRING_CONFIRM"
    const val ACTION_REJECT = "com.srilakshmikanthanp.clipbird.PAIRING_REJECT"
  }
}
