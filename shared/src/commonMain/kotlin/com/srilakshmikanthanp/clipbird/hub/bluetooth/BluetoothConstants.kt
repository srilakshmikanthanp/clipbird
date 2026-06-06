package com.srilakshmikanthanp.clipbird.hub.bluetooth

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object BluetoothConstants {
  val clipbirdServiceUuid = Uuid.parse("f484e2db-2efa-4b58-96be-f89372a3ef82")
}
