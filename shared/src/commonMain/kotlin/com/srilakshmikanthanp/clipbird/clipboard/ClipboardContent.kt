package com.srilakshmikanthanp.clipbird.clipboard

import kotlinx.serialization.Serializable

@Serializable
class ClipboardContent(
  val items: List<ClipboardItem>
) {
  companion object {
    val Empty = ClipboardContent(emptyList())
  }
}
