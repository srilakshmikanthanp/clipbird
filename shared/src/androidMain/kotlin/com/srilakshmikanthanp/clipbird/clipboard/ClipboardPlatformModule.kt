package com.srilakshmikanthanp.clipbird.clipboard

import android.content.Context
import com.srilakshmikanthanp.clipbird.AppConstants
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class ClipboardPlatformModule {
  @Single
  fun clipboard(scope: CoroutineScope, context: Context): Clipboard =
    SizedClipboard(AndroidClipboard(context, scope), maxBytes = AppConstants.CLIPBOARD_MAX_SIZE)
}
