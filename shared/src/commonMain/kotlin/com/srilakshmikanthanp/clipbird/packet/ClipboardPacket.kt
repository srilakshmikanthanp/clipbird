package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
class ClipboardSyncingPacket(
  override val id: Uuid,
  val datum: ClipboardDatum
) : RoutedPacket

@Serializable
class ClipboardDatum(
  private val datum: List<ClipboardData>
)

@Serializable
class ClipboardData(
  val mimeType: String,
  val data: ByteArray,
)
