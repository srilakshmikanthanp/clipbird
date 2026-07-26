package com.srilakshmikanthanp.clipbird.packet

import com.srilakshmikanthanp.clipbird.clipboard.ClipboardContent
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
class ClipboardSyncingPacket(
  override val id: Uuid,
  val content: ClipboardContent
) : RoutedPacket {
  companion object {
    fun create(content: ClipboardContent): ClipboardSyncingPacket {
      return ClipboardSyncingPacket(id = Uuid.random(), content = content)
    }
  }
}
