package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import com.srilakshmikanthanp.clipbird.pairing.PairingCandidate

data class BluetoothPairingCandidate(
  override val name: String,
  val address: String,
) : PairingCandidate
