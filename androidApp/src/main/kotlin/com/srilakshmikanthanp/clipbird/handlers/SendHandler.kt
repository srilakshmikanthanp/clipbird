package com.srilakshmikanthanp.clipbird.handlers

import android.os.Bundle
import com.srilakshmikanthanp.clipbird.clipboard.Clipboard
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardContent
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class SendHandler : ClipboardSendActivity() {
  private val applicationScope: CoroutineScope by inject()
  private val clipboard: Clipboard by inject()
  private val peerHub: BluetoothPeerHub by inject()

  override val notificationId = NOTIFICATION_ID

  private suspend fun sendClipboard(content: ClipboardContent) {
    try {
      peerHub.sendClipboard(content, progressUpdater("Sending to peers…"))
      val notification = buildNotification("Sent to peers", autoCancel = true)
      notificationManager.notify(NOTIFICATION_ID, notification)
    } catch (_: Exception) {
      val notification = buildNotification("Send failed", autoCancel = true)
      notificationManager.notify(NOTIFICATION_ID, notification)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val notification = buildNotification("Sending to peers…", ongoing = true, indeterminate = true)
    notificationManager.notify(NOTIFICATION_ID, notification)
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (!hasFocus) return
    val content = runBlocking { clipboard.get() }
    applicationScope.launch { sendClipboard(content) }
    finish()
  }

  companion object {
    private const val NOTIFICATION_ID = 3
  }
}
