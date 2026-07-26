package com.srilakshmikanthanp.clipbird

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.srilakshmikanthanp.clipbird.handlers.SendHandler
import com.srilakshmikanthanp.clipbird.notification.TransferNotificationManager
import com.srilakshmikanthanp.clipbird.pairing.PairingNotificationManager
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class ClipbirdService : Service(), KoinComponent {
  private val pairingNotificationManager by lazy { PairingNotificationManager(this) }
  private val appRuntime by inject<AppRuntime>()
  private val transferNotificationManager by inject<TransferNotificationManager>()

  private fun startForeground() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(CHANNEL_ID, "Clipbird", NotificationManager.IMPORTANCE_LOW)
      getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    val sendPendingIntent = PendingIntent.getActivity(
      this, 0,
      Intent(this, SendHandler::class.java),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    NotificationCompat.Builder(this, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
      .setContentTitle(getString(R.string.app_name))
      .setContentText("Running in background")
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .addAction(R.drawable.ic_launcher_foreground, "Send", sendPendingIntent)
      .build().also { startForeground(NOTIFICATION_ID, it) }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    startForeground()
    pairingNotificationManager.start()
    transferNotificationManager.start()
    appRuntime.start()
    return START_STICKY
  }

  override fun onDestroy() {
    appRuntime.stop()
    pairingNotificationManager.stop()
    transferNotificationManager.stop()
    super.onDestroy()
  }

  override fun onBind(intent: Intent?) = null

  companion object {
    const val CHANNEL_ID = "clipbird_service"
    const val NOTIFICATION_ID = 2
  }
}