package com.srilakshmikanthanp.clipbird.io.bluetooth

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BluetoothDevice(
  val address: Uuid,
  val name: String?,
  val serviceUuids: Set<Uuid>,
)
