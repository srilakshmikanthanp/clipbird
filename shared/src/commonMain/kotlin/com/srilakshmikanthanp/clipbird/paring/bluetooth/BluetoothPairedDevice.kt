package com.srilakshmikanthanp.clipbird.paring.bluetooth

import com.srilakshmikanthanp.clipbird.common.toPublicKey
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import java.security.PublicKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BluetoothPairedDevice(
  id: Long,
  name: String,
  publicKey: PublicKey,
  val address: Uuid
) : PairedDevice(
  id = id,
  name = name,
  publicKey = publicKey,
)
