package com.srilakshmikanthanp.clipbird.notification

import android.app.NotificationManager
import android.content.Context
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.srilakshmikanthanp.clipbird.ClipbirdService
import com.srilakshmikanthanp.clipbird.R
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import com.srilakshmikanthanp.clipbird.peer.TransferState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TransferNotificationManager(
  private val context: Context,
  private val peerHub: BluetoothPeerHub,
  private val scope: CoroutineScope,
) {
  private val notificationManager = context.getSystemService(NotificationManager::class.java)
  private var lastProgressMs = 0L
  private var job: Job? = null

  private fun showProgress(current: Int, total: Int) {
    val now = SystemClock.elapsedRealtime()

    if (now - lastProgressMs < THROTTLE_MS) {
      return
    } else {
      lastProgressMs = now
    }

    val builder = NotificationCompat.Builder(context, ClipbirdService.CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle("Sending to peers…")
      .setOngoing(true)

    if (total > 0) {
      val percent = (current * 100L / total).toInt()
      builder.setProgress(100, percent, false).setContentText("$percent%")
    } else {
      builder.setProgress(0, 0, true)
    }

    notificationManager.notify(
      NOTIFICATION_ID,
      builder.build()
    )
  }

  private fun showDone(success: Boolean) {
    val title = if (success) "Sent to peers" else "Send failed"
    val notification = NotificationCompat.Builder(context, ClipbirdService.CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle(title)
      .setAutoCancel(true)
      .build()

    notificationManager.notify(
      NOTIFICATION_ID,
      notification
    )
  }

  private suspend fun observeTransferState() {
    peerHub.transferState.collect { state ->
      when (state) {
        is TransferState.Progress -> showProgress(state.current, state.total)
        is TransferState.Success -> showDone(success = true)
        is TransferState.Failure -> showDone(success = false)
      }
    }
  }

  fun start() {
    job = scope.launch { observeTransferState() }
  }

  fun stop() {
    job?.cancel()
  }

  companion object {
    private const val NOTIFICATION_ID = 3
    private const val THROTTLE_MS = 200L
  }
}
