package com.srilakshmikanthanp.clipbird.packet

import com.srilakshmikanthanp.clipbird.clipboard.ClipboardContent
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
class ClipboardSyncingPacket(
  override val id: Uuid,
  override val createdAt: Long,
  override val ttlMillis: Long,
  val content: ClipboardContent
) : RoutedPacket {
  companion object {
    fun create(content: ClipboardContent): ClipboardSyncingPacket {
      return ClipboardSyncingPacket(
        id = Uuid.random(),
        createdAt = System.currentTimeMillis(),
        ttlMillis = RoutedPacket.DEFAULT_TTL_MILLIS,
        content = content,
      )
    }
  }
}