package com.srilakshmikanthanp.clipbird.io

suspend fun Channel.readByte(): Byte = readExactly(1).first()

interface ProgressListener {
  fun onProgress(progress: Int, total: Int)

  companion object {
    val NO_OP = object : ProgressListener {
      override fun onProgress(progress: Int, total: Int) {}
    }
  }
}

suspend fun Channel.write(
  data: ByteArray,
  chunkSize: Int = 8 * 1024,
  listener: ProgressListener
) {
  var offset = 0
  listener.onProgress(offset, data.size)

  if (data.isEmpty()) {
    listener.onProgress(0, 0)
    return
  }

  while (offset < data.size) {
    val size = minOf(chunkSize, data.size - offset)
    this.write(data, offset, size)
    offset += size
    listener.onProgress(offset, data.size)
  }
}
