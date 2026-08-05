package com.srilakshmikanthanp.clipbird.clipboard

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SizedClipboard(
  private val delegate: Clipboard,
  private val maxBytes: Int,
) : Clipboard {
  override val data: Flow<ClipboardContent> = delegate.data.map { it.orEmptyIfOversized() }
  override suspend fun get(): ClipboardContent = delegate.get().orEmptyIfOversized()
  override suspend fun set(content: ClipboardContent) = delegate.set(content)

  private fun ClipboardContent.orEmptyIfOversized(): ClipboardContent {
    return if (items.sumOf { it.data.size } > maxBytes) ClipboardContent.Empty else this
  }
}
