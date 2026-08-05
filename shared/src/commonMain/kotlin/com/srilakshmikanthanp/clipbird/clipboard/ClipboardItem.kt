package com.srilakshmikanthanp.clipbird.clipboard

import kotlinx.serialization.Serializable

@Serializable
data class ClipboardItem(
  val mimeType: String,
  val data: ByteArray,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ClipboardItem) return false
    if (mimeType != other.mimeType) return false
    if (!data.contentEquals(other.data)) return false
    return true
  }

  override fun hashCode(): Int {
    var result = mimeType.hashCode()
    result = 31 * result + data.contentHashCode()
    return result
  }
}
