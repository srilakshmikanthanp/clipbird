package com.srilakshmikanthanp.clipbird.clipboard

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class ClipboardPlatformModule {
  @Single
  fun clipboard(): Clipboard = JvmClipboard()
}