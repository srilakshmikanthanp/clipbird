package com.srilakshmikanthanp.clipbird.clipboard

import com.srilakshmikanthanp.clipbird.AppConstants
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class ClipboardPlatformModule {
  @Single
  fun clipboard(): Clipboard = SizedClipboard(JvmClipboard(), maxBytes = AppConstants.CLIPBOARD_MAX_SIZE)
}