package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import com.srilakshmikanthanp.clipbird.common.toPublicKey
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import java.security.PublicKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BluetoothPairedDevice(
  id: Long,
  name: String,
  publicKey: PublicKey,
  val address: String
) : PairedDevice(
  id = id,
  name = name,
  publicKey = publicKey,
)
