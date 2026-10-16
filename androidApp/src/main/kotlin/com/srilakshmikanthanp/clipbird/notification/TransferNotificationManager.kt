package com.srilakshmikanthanp.clipbird.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.srilakshmikanthanp.clipbird.ClipbirdService
import com.srilakshmikanthanp.clipbird.MainActivity
import com.srilakshmikanthanp.clipbird.R
import com.srilakshmikanthanp.clipbird.handlers.SendHandler
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TransferNotificationManager(
  private val context: Context,
  private val peerHub: BluetoothPeerHub,
  private val scope: CoroutineScope,
) {
  private val notificationManager = context.getSystemService(NotificationManager::class.java)

  private var job: Job? = null

  private fun historyPendingIntent(): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
      putExtra(MainActivity.EXTRA_INITIAL_ROUTE, MainActivity.ROUTE_HISTORY)
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    return PendingIntent.getActivity(
      context,
      1,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
  }

  private fun sendPendingIntent(): PendingIntent {
    return PendingIntent.getActivity(
      context,
      0,
      Intent(context, SendHandler::class.java),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
  }

  private fun notify(
    contentIntent: PendingIntent,
    block: NotificationCompat.Builder.() -> Unit
  ) {
    val notification = NotificationCompat.Builder(context, ClipbirdService.CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle(context.getString(R.string.app_name))
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setOngoing(true)
      .setContentIntent(contentIntent)
      .apply(block)
      .build()

    notificationManager.notify(
      ClipbirdService.NOTIFICATION_ID,
      notification
    )
  }

  private fun showSending() {
    notify(contentIntent = historyPendingIntent()) {
      setProgress(0, 0, true)
      setContentText("Sending clipboard…")
    }
  }

  private fun showDone() {
    notify(contentIntent = sendPendingIntent()) {
      setContentText("Tap to send clipboard")
    }
  }

  fun start() {
    job = scope.launch {
      peerHub.isSending.collect { isSending ->
        if (isSending) showSending() else showDone()
      }
    }
  }

  fun stop() {
    job?.cancel()
  }
}
