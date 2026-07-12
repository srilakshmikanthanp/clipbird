package com.srilakshmikanthanp.clipbird.paring.bluetooth

import com.srilakshmikanthanp.clipbird.paring.PairingCandidate

data class BluetoothPairingCandidate(
  override val name: String,
  val address: String,
) : PairingCandidate
