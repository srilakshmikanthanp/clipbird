package com.srilakshmikanthanp.clipbird.clipboard

import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import javax.imageio.ImageIO

class ClipboardTransferable(private val content: ClipboardContent) : Transferable {
  private val itemsByFlavor = content.items.mapNotNull { item -> toFlavor(item.mimeType)?.let { it to item } }.toMap()

  private fun toFlavor(mimeType: String): DataFlavor? = when (mimeType) {
    MIME_TEXT -> DataFlavor.stringFlavor
    MIME_HTML -> htmlFlavor
    MIME_PNG -> DataFlavor.imageFlavor
    else -> null
  }

  override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
    return flavor in itemsByFlavor
  }

  override fun getTransferDataFlavors(): Array<DataFlavor> {
    return itemsByFlavor.keys.toTypedArray()
  }

  override fun getTransferData(flavor: DataFlavor): Any {
    val item = itemsByFlavor[flavor] ?: throw UnsupportedFlavorException(flavor)

    return when (flavor) {
      DataFlavor.stringFlavor, htmlFlavor -> item.data.toString(Charsets.UTF_8)
      DataFlavor.imageFlavor -> ImageIO.read(item.data.inputStream())
      else -> throw UnsupportedFlavorException(flavor)
    }
  }

  companion object {
    val htmlFlavor = DataFlavor("text/html;charset=UTF-8;class=java.lang.String")
    const val MIME_TEXT = "text/plain"
    const val MIME_PNG = "image/png"
    const val MIME_HTML = "text/html"
  }
}
