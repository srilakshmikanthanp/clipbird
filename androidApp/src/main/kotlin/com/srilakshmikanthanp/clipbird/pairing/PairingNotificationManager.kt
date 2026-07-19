package com.srilakshmikanthanp.clipbird.pairing

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigTextStyle
import androidx.core.app.NotificationCompat.Builder
import com.srilakshmikanthanp.clipbird.paring.PairingDeferredVerifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import androidx.core.net.toUri
import com.srilakshmikanthanp.clipbird.paring.PairingDeferredVerifier.VerificationRequest

class PairingNotificationManager(private val context: Context) : KoinComponent {
  private val notificationManager = context.getSystemService(NotificationManager::class.java)
  private val verifier: PairingDeferredVerifier by inject()
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  private fun pendingBroadcast(action: String, deviceId: Long): PendingIntent {
    val intent = Intent(context, PairingVerificationReceiver::class.java).apply {
      this.action = action
      data = "clipbird://pairing/$action/$deviceId".toUri()
      putExtra(PairingVerificationReceiver.EXTRA_DEVICE_ID, deviceId)
    }
    return PendingIntent.getBroadcast(
      context, 0, intent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )
  }

  private fun show(request: VerificationRequest) {
    Builder(context, NOTIFICATION_CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_dialog_info)
      .setContentTitle("Pairing Request")
      .setContentText("${request.remoteDevice.name} wants to pair. Code: ${request.code}")
      .setStyle(BigTextStyle().bigText("${request.remoteDevice.name} wants to pair with this device.\n\nVerification code: ${request.code}"))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_CALL)
      .setOngoing(true)
      .addAction(0, "Confirm", pendingBroadcast(PairingVerificationReceiver.ACTION_CONFIRM, request.remoteDevice.id))
      .addAction(0, "Reject", pendingBroadcast(PairingVerificationReceiver.ACTION_REJECT, request.remoteDevice.id))
      .build().also {
        notificationManager.notify(NOTIFICATION_ID, it)
      }
  }

  private suspend fun process() = verifier.requests.collect { request ->
    notificationManager.cancel(NOTIFICATION_ID)
    if (request != null) show(request)
  }

  fun start() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return
    val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Pairing Requests", NotificationManager.IMPORTANCE_HIGH).apply { description = "Pairing verification requests from other devices" }
    notificationManager.createNotificationChannel(channel)
    scope.launch { process() }
  }

  fun stop() {
    notificationManager.cancel(NOTIFICATION_ID)
    scope.cancel()
  }

  companion object {
    const val NOTIFICATION_CHANNEL_ID = "pairing_requests"
    const val NOTIFICATION_ID = 1
  }
}
