package com.srilakshmikanthanp.clipbird.paring

interface Pairer<C : PairingCandidate, P : PairedDevice> {
  suspend fun pair(candidate: C): P
}
