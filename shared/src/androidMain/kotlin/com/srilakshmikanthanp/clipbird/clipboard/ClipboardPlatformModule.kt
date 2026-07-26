package com.srilakshmikanthanp.clipbird.clipboard

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class ClipboardPlatformModule {
  @Single
  fun clipboard(scope: CoroutineScope, context: Context): Clipboard = AndroidClipboard(context, scope)
}
