package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.io.ServerEndpoint
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BluetoothServerEndpoint (
  val address: String,
  val serviceUuid: Uuid
) : ServerEndpoint
