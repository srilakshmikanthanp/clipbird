package com.srilakshmikanthanp.clipbird.hub

import kotlinx.coroutines.flow.Flow

interface Discoverer<T: HubDevice> {
  val events: Flow<DiscoveryEvent<T>>
}
