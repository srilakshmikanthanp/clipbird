package com.srilakshmikanthanp.clipbird.hub

sealed interface DiscoveryEvent<T: HubDevice> {
  data class Found<T: HubDevice>(val device: T) : DiscoveryEvent<T>
  data class Lost<T: HubDevice>(val device: T) : DiscoveryEvent<T>
}
