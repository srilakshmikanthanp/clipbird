package com.srilakshmikanthanp.clipbird.paring.bluetooth

import com.srilakshmikanthanp.clipbird.paring.PairingCandidate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BluetoothPairingCandidate(
  val address: Uuid,
  val serviceUuid: Uuid,
) : PairingCandidate
