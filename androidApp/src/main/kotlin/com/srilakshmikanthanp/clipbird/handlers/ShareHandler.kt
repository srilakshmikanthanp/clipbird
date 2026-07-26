package com.srilakshmikanthanp.clipbird.handlers

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardContent
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardItem
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardMimeType
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class ShareHandler : ClipboardSendActivity() {
  private val applicationScope: CoroutineScope by inject()
  private val peerHub: BluetoothPeerHub by inject()

  override val notificationId = NOTIFICATION_ID

  private fun processTextIntentContent(intent: Intent): ClipboardContent? {
    val text = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: return null
    return ClipboardContent(listOf(ClipboardItem(ClipboardMimeType.MIME_TEXT, text.toByteArray(Charsets.UTF_8))))
  }

  private fun readImageUri(uri: Uri): ByteArray? {
    val bytes = try {
      contentResolver.openInputStream(uri)
    } catch (_: FileNotFoundException) {
      null
    } catch (_: SecurityException) {
      null
    }?.use {
      it.readBytes()
    } ?: return null

    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
    val out = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    return out.toByteArray()
  }

  private fun sendIntentContent(intent: Intent): ClipboardContent? = when {
    intent.type?.startsWith("image/") == true -> {
      val uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri ?: return null
      val png = readImageUri(uri) ?: return null
      ClipboardContent(listOf(ClipboardItem(ClipboardMimeType.MIME_PNG, png)))
    }

    intent.type?.startsWith("text/") == true -> {
      val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return null
      ClipboardContent(listOf(ClipboardItem(ClipboardMimeType.MIME_TEXT, text.toByteArray(Charsets.UTF_8))))
    }

    else -> null
  }

  private fun getContent(): ClipboardContent? = when (intent.action) {
    Intent.ACTION_PROCESS_TEXT -> this.processTextIntentContent(intent)
    Intent.ACTION_SEND -> this.sendIntentContent(intent)
    else -> null
  }

  private suspend fun sendClipboard() {
    try {
      val content = withContext(Dispatchers.IO) { getContent() } ?: return
      peerHub.sendClipboard(content, progressUpdater("Sharing to peers…"))
      val notification = buildNotification(title = "Shared to peers", autoCancel = true)
      notificationManager.notify(NOTIFICATION_ID, notification)
    } catch (e: Exception) {
      val notification = buildNotification(title = "Share failed", autoCancel = true)
      notificationManager.notify(NOTIFICATION_ID, notification)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val notification = buildNotification(title = "Sharing to peers…", ongoing = true, indeterminate = true)
    notificationManager.notify(NOTIFICATION_ID, notification)
    applicationScope.launch { sendClipboard() }
    finish()
  }

  companion object {
    const val NOTIFICATION_ID = 4
  }
}
