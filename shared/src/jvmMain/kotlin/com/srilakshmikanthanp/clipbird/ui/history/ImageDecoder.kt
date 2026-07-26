package com.srilakshmikanthanp.clipbird.ui.history

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

internal actual fun decodeImageBitmap(
  data: ByteArray
): ImageBitmap = ImageIO.read(
  ByteArrayInputStream(data)
).toComposeImageBitmap()
