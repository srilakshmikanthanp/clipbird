package com.srilakshmikanthanp.clipbird.pairing

import kotlinx.coroutines.flow.StateFlow

interface PairingCandidateProvider<P : PairingCandidate> {
  val devices: StateFlow<Collection<P>>
}
