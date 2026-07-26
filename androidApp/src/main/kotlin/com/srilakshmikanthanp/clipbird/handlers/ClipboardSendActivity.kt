package com.srilakshmikanthanp.clipbird.handlers

import android.app.NotificationManager
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
import com.srilakshmikanthanp.clipbird.ClipbirdService
import com.srilakshmikanthanp.clipbird.R
import com.srilakshmikanthanp.clipbird.notification.ProgressNotificationUpdater

abstract class ClipboardSendActivity : ComponentActivity() {
  protected abstract val notificationId: Int

  protected val notificationManager: NotificationManager by lazy {
    applicationContext.getSystemService(NotificationManager::class.java)
  }

  protected fun buildNotification(
    title: String,
    text: String? = null,
    ongoing: Boolean = false,
    autoCancel: Boolean = false,
    indeterminate: Boolean = false,
  ) = NotificationCompat.Builder(applicationContext, ClipbirdService.CHANNEL_ID)
    .setSmallIcon(R.drawable.ic_launcher_foreground)
    .setContentTitle(title)
    .setContentText(text)
    .setOngoing(ongoing)
    .setAutoCancel(autoCancel)
    .apply { if (indeterminate) setProgress(0, 0, true) }
    .build()

  protected fun progressUpdater(
    title: String
  ) = ProgressNotificationUpdater(
    applicationContext,
    notificationId,
    title
  )
}
