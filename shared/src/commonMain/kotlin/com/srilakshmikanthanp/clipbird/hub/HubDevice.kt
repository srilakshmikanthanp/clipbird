package com.srilakshmikanthanp.clipbird.hub

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface HubDevice {
  val id: ULong
}
