package com.srilakshmikanthanp.clipbird.peer.client

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.common.closeQuietly
import com.srilakshmikanthanp.clipbird.paring.ActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.ChannelConnectionChecker
import com.srilakshmikanthanp.clipbird.peer.ConnectedDevice
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

open class ClipbirdClientCoordinator<P : PairedDevice>(
  private val activeDeviceProvider: ActivePairedDeviceProvider<P>,
  private val connector: ClientServerConnector<P>,
  private val decider: ClientServerConnectionInitiationDecider,
  private val connectionChecker: ChannelConnectionChecker,
  private val handshakeProtocol: ClientServerHandshakeProtocol
) {
  private val _devices = MutableSharedFlow<ConnectedDevice>(extraBufferCapacity = 64)
  val devices: SharedFlow<ConnectedDevice> = _devices.asSharedFlow()

  @Volatile
  private var activeDevices: Set<P> = emptySet()

  private suspend fun connect(device: P) {
    var channel: Channel? = null
    try {
      if (!decider.shouldInitiateConnection(device)) return
      if (connectionChecker.isConnected(device)) return
      channel = connector.connect(device)
      handshakeProtocol.handshake(channel, device)
      _devices.emit(ConnectedDevice(device, channel))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Logger.e("Connect to ${device.name} failed: ${e.message}", e, TAG)
      channel?.closeQuietly()
    }
  }

  private suspend fun collectDevices() {
    activeDeviceProvider.devices.collect { devices ->
      activeDevices = devices.toSet()
    }
  }

  private suspend fun loop() {
    while (true) {
      activeDevices.forEach { connect(it) }
      delay(RETRY_DELAY)
    }
  }

  suspend fun run() = coroutineScope {
    launch { collectDevices() }
    launch { loop() }
  }

  companion object {
    const val TAG = "ClipbirdClientCoordinator"
    val RETRY_DELAY = 5.seconds
  }
}
