package com.srilakshmikanthanp.clipbird.hub.bluetooth

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object BluetoothConstants {
  val clipbirdPairingServiceUuid = Uuid.parse("c7d9a4f1-3e2b-4a6c-9d8e-1f0b2c3d4e5f")
  val clipbirdServiceUuid = Uuid.parse("f484e2db-2efa-4b58-96be-f89372a3ef82")
}
