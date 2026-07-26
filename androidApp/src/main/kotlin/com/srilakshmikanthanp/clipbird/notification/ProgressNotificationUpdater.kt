package com.srilakshmikanthanp.clipbird.notification

import android.app.NotificationManager
import android.content.Context
import android.os.SystemClock
import androidx.core.app.NotificationCompat.Builder
import com.srilakshmikanthanp.clipbird.ClipbirdService
import com.srilakshmikanthanp.clipbird.R
import com.srilakshmikanthanp.clipbird.io.ProgressListener

class ProgressNotificationUpdater(
  private val context: Context,
  private val notificationId: Int,
  private val title: String,
  private val intervalMs: Long = 200,
) : ProgressListener {
  private val notificationManager = context.getSystemService(NotificationManager::class.java)
  private var lastPostedMs = 0L

  override fun onProgress(progress: Int, total: Int) {
    val now = SystemClock.elapsedRealtime()
    if (now - lastPostedMs < intervalMs) return
    lastPostedMs = now

    val builder = Builder(context, ClipbirdService.CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle(title)
      .setOngoing(true)

    if (total > 0) {
      val percent = (progress * 100L / total).toInt()
      builder.setProgress(100, percent, false)
      builder.setContentText("$percent%")
    } else {
      builder.setProgress(0, 0, true)
    }

    notificationManager.notify(
      notificationId,
      builder.build()
    )
  }
}
