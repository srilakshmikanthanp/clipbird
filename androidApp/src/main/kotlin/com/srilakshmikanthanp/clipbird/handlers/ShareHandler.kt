package com.srilakshmikanthanp.clipbird.handlers

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
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

class ShareHandler : ComponentActivity() {
  private val applicationScope: CoroutineScope by inject()
  private val peerHub: BluetoothPeerHub by inject()

  private fun readImageUri(uri: Uri): ByteArray? {
    val bytes = try {
      contentResolver.openInputStream(uri)
    } catch (_: FileNotFoundException) {
      null
    } catch (_: SecurityException) {
      null
    }?.use { it.readBytes() } ?: return null
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
    val out = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    return out.toByteArray()
  }

  private fun getContent(intent: Intent): ClipboardContent? = when (intent.action) {
    Intent.ACTION_PROCESS_TEXT -> {
      val text = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: return null
      ClipboardContent(listOf(ClipboardItem(ClipboardMimeType.MIME_TEXT, text.toByteArray(Charsets.UTF_8))))
    }
    Intent.ACTION_SEND -> when {
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
    else -> null
  }

  private suspend fun sendClipboard() {
    val content = withContext(Dispatchers.IO) { getContent(intent) } ?: return
    peerHub.sendClipboard(content)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    applicationScope.launch { sendClipboard() }
    finish()
  }
}
