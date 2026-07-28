package com.srilakshmikanthanp.clipbird.pairing

interface Pairer<C : PairingCandidate, P : PairedDevice> {
  suspend fun pair(candidate: C): P
}
