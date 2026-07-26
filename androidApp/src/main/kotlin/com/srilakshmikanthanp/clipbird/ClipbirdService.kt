package com.srilakshmikanthanp.clipbird

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.srilakshmikanthanp.clipbird.pairing.PairingNotificationManager
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class ClipbirdService : Service(), KoinComponent {
  private val pairingNotificationManager by lazy { PairingNotificationManager(this) }
  private val appRuntime by inject<AppRuntime>()

  private fun startForeground() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(CHANNEL_ID, "Clipbird", NotificationManager.IMPORTANCE_LOW)
      getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
    NotificationCompat.Builder(this, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle("Clipbird")
      .setContentText("Running in background")
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .build().also {
        startForeground(NOTIFICATION_ID, it)
      }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    startForeground()
    pairingNotificationManager.start()
    appRuntime.start()
    return START_STICKY
  }

  override fun onDestroy() {
    appRuntime.stop()
    pairingNotificationManager.stop()
    super.onDestroy()
  }

  override fun onBind(intent: Intent?) = null

  companion object {
    const val CHANNEL_ID = "clipbird_service"
    const val NOTIFICATION_ID = 2
  }
}