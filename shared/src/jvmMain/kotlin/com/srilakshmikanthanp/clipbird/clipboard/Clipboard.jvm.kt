package com.srilakshmikanthanp.clipbird.clipboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class JvmClipboard : Clipboard {
  private val clipboard = Toolkit.getDefaultToolkit().systemClipboard
  override val data: Flow<ClipboardContent> = emptyFlow()

  private fun BufferedImage.toPngBytes(): ByteArray {
    val out = ByteArrayOutputStream()
    ImageIO.write(this, "PNG", out)
    return out.toByteArray()
  }

  override suspend fun get(): ClipboardContent = withContext(Dispatchers.IO) {
    val transferable = clipboard.getContents(null) ?: return@withContext ClipboardContent(emptyList())
    val items = mutableListOf<ClipboardItem>()

    if (transferable.isDataFlavorSupported(ClipboardTransferable.htmlFlavor)) {
      runCatching { transferable.getTransferData(ClipboardTransferable.htmlFlavor) as String }.onSuccess {
        items.add(ClipboardItem(ClipboardMimeType.MIME_HTML, it.toByteArray(Charsets.UTF_8)))
      }
    }

    if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      runCatching { transferable.getTransferData(DataFlavor.stringFlavor) as String }.onSuccess {
        items.add(ClipboardItem(ClipboardMimeType.MIME_TEXT, it.toByteArray(Charsets.UTF_8)))
      }
    }

    if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
      runCatching { transferable.getTransferData(DataFlavor.imageFlavor) as BufferedImage }.onSuccess { img ->
        items.add(ClipboardItem(ClipboardMimeType.MIME_PNG, img.toPngBytes()))
      }
    }

    ClipboardContent(items)
  }

  override suspend fun set(content: ClipboardContent) = withContext(Dispatchers.IO) {
    clipboard.setContents(ClipboardTransferable(content), null)
  }
}
