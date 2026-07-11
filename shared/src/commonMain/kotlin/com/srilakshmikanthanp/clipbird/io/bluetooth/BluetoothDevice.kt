package com.srilakshmikanthanp.clipbird.io.bluetooth

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class BluetoothDevice(
  val address: String,
  val name: String?
)
