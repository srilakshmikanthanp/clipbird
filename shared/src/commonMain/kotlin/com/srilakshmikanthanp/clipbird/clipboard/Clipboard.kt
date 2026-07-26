package com.srilakshmikanthanp.clipbird.clipboard

import kotlinx.coroutines.flow.Flow

interface Clipboard {
  val data: Flow<ClipboardContent>
  suspend fun get(): ClipboardContent
  suspend fun set(content: ClipboardContent)
}
