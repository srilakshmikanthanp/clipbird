package com.srilakshmikanthanp.clipbird.ui.history

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

internal actual fun decodeImageBitmap(
  data: ByteArray
): ImageBitmap = BitmapFactory.decodeByteArray(
  data,
  0,
  data.size
).asImageBitmap()
