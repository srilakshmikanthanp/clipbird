package com.srilakshmikanthanp.clipbird.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

class AndroidClipboard(private val context: Context, scope: CoroutineScope) : Clipboard {
  private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  private val authority = "${context.packageName}.fileprovider"
  private val maxClipboardSize = 800 * 1024

  override val data: Flow<ClipboardContent> = callbackFlow {
    val listener = ClipboardManager.OnPrimaryClipChangedListener {
      if (clipboardManager.primaryClipDescription?.label == LABEL) {
        return@OnPrimaryClipChangedListener
      }
      launch {
        send(get())
      }
    }

    clipboardManager.addPrimaryClipChangedListener(listener)

    awaitClose {
      clipboardManager.removePrimaryClipChangedListener(listener)
    }
  }.shareIn(
    scope,
    SharingStarted.WhileSubscribed()
  )

  private suspend fun ByteArray.toPng(): ByteArray? = withContext(Dispatchers.Default) {
    val bitmap = BitmapFactory.decodeByteArray(this@toPng, 0, size) ?: return@withContext null
    val out = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    out.toByteArray()
  }

  private suspend fun readUri(uri: Uri): ClipboardItem? = withContext(Dispatchers.IO) {
    val allowed = arrayOf(ClipboardMimeType.MIME_TEXT, ClipboardMimeType.MIME_PNG, ClipboardMimeType.MIME_HTML)
    val item = try {
      context.contentResolver.openInputStream(uri)
    } catch (_: FileNotFoundException) {
      return@withContext null
    } catch (_: SecurityException) {
      return@withContext null
    }.use { stream ->
      val mimeType = context.contentResolver.getType(uri) ?: return@use null
      val bytes = stream?.readBytes() ?: return@use null
      ClipboardItem(mimeType, bytes)
    } ?: return@withContext null

    val resolved = if (item.mimeType.startsWith("image/")) {
      ClipboardItem(ClipboardMimeType.MIME_PNG, item.data.toPng() ?: return@withContext null)
    } else {
      item
    }

    if (allowed.contains(resolved.mimeType)) resolved else null
  }

  private suspend fun writeFile(ext: String, data: ByteArray): Uri = withContext(Dispatchers.IO) {
    val file = File.createTempFile(LABEL, ext, context.cacheDir)
    file.writeBytes(data)
    FileProvider.getUriForFile(context, authority, file)
  }

  private suspend fun setAsImage(items: List<ClipboardItem>): Boolean {
    val image = items.find { it.mimeType == ClipboardMimeType.MIME_PNG } ?: return false
    val uri = writeFile(".png", image.data)
    val clip = ClipData.newUri(context.contentResolver, LABEL, uri)
    withContext(Dispatchers.Main) { clipboardManager.setPrimaryClip(clip) }
    return true
  }

  private suspend fun setAsTextAndHtml(items: List<ClipboardItem>): Boolean {
    val text = items.find { it.mimeType == ClipboardMimeType.MIME_TEXT } ?: return false
    val html = items.find { it.mimeType == ClipboardMimeType.MIME_HTML } ?: return false
    val textStr = text.data.toString(Charsets.UTF_8)
    val htmlStr = html.data.toString(Charsets.UTF_8)
    val clipData = if (textStr.length + htmlStr.length >= maxClipboardSize) {
      ClipData.newUri(context.contentResolver, LABEL, writeFile(".html", html.data))
    } else {
      ClipData.newHtmlText(LABEL, textStr, htmlStr)
    }
    withContext(Dispatchers.Main) { clipboardManager.setPrimaryClip(clipData) }
    return true
  }

  private suspend fun setAsHtml(items: List<ClipboardItem>): Boolean {
    val html = items.find { it.mimeType == ClipboardMimeType.MIME_HTML } ?: return false
    val htmlStr = html.data.toString(Charsets.UTF_8)
    val clipData = if (html.data.size >= maxClipboardSize) {
      ClipData.newUri(context.contentResolver, LABEL, writeFile(".html", html.data))
    } else {
      ClipData.newHtmlText(LABEL, htmlStr, htmlStr)
    }
    withContext(Dispatchers.Main) { clipboardManager.setPrimaryClip(clipData) }
    return true
  }

  private suspend fun setAsText(items: List<ClipboardItem>): Boolean {
    val text = items.find { it.mimeType == ClipboardMimeType.MIME_TEXT } ?: return false
    val textStr = text.data.toString(Charsets.UTF_8)
    val clipData = if (text.data.size >= maxClipboardSize) {
      ClipData.newUri(context.contentResolver, LABEL, writeFile(".txt", text.data))
    } else {
      ClipData.newPlainText(LABEL, textStr)
    }
    withContext(Dispatchers.Main) { clipboardManager.setPrimaryClip(clipData) }
    return true
  }

  override suspend fun get(): ClipboardContent {
    val clipData = clipboardManager.primaryClip ?: return ClipboardContent(emptyList())
    val items = mutableListOf<ClipboardItem>()
    for (i in 0 until clipData.itemCount) {
      val item = clipData.getItemAt(i)
      item.htmlText?.let { items.add(ClipboardItem(ClipboardMimeType.MIME_HTML, it.toByteArray(Charsets.UTF_8))) }
      item.uri?.let { readUri(it)?.let { ci -> items.add(ci) } }
      item.text?.let { items.add(ClipboardItem(ClipboardMimeType.MIME_TEXT, it.toString().toByteArray(Charsets.UTF_8))) }
    }
    return ClipboardContent(items)
  }

  override suspend fun set(content: ClipboardContent) {
    listOf(
      ::setAsImage,
      ::setAsTextAndHtml,
      ::setAsHtml,
      ::setAsText
    ).any {
      it(content.items)
    }
  }

  companion object {
    private const val LABEL = "clipbird"
  }
}
