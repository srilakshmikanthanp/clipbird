package com.srilakshmikanthanp.clipbird.paring

import kotlinx.coroutines.flow.StateFlow

interface PairingCandidateProvider<P : PairingCandidate> {
  val devices: StateFlow<Set<P>>
}
