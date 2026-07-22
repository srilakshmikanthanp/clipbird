package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdClientCoordinator
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServerCoordinator
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class PeerModule {
  @Single
  fun channelHub(
    pairedDeviceService: PairedDeviceService<out PairedDevice>,
    scope: CoroutineScope,
  ): ChannelHub = ChannelHub(pairedDeviceService, scope)

  @Single
  fun channelConnectionChecker(
    hub: ChannelHub,
  ): ChannelConnectionChecker = ChannelHubConnectionChecker(hub)

  @Single
  fun channelCoordinator(
    serverCoordinator: ClipbirdServerCoordinator,
    clientCoordinator: ClipbirdClientCoordinator,
    channelHub: ChannelHub,
    scope: CoroutineScope,
  ): ChannelCollector = ChannelCollector(
    serverCoordinator,
    clientCoordinator,
    channelHub,
    scope,
  )
}