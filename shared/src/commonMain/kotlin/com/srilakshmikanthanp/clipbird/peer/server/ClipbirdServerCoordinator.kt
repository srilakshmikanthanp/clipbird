package com.srilakshmikanthanp.clipbird.peer.server

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.authentication.AuthenticationException
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.closeQuietly
import com.srilakshmikanthanp.clipbird.packet.ErrorPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.ConnectedDevice
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class ClipbirdServerCoordinator(
  private val advertiser: Advertiser,
  private val server: ClipbirdServer,
  private val handshakeProtocol: ClipbirdServerHandshakeProtocol<out PairedDevice>
) {
  private val _devices = MutableSharedFlow<ConnectedDevice>(extraBufferCapacity = 64)
  val devices: SharedFlow<ConnectedDevice> = _devices.asSharedFlow()

  private suspend fun handleChannel(channel: Channel) {
    try {
      val device = handshakeProtocol.handshake(channel)
      _devices.emit(ConnectedDevice(device, channel))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Logger.e("Handshake failed: ${e.message}", e, TAG)
      channel.closeQuietly()
    }
  }

  private suspend fun doAdvertise() {
    try {
      advertiser.advertise()
    } catch (e: Exception) {
      Logger.e("Error in BLE advertising: ${e.message}", e, TAG)
    }
  }

  private suspend fun doServe() = coroutineScope {
    try {
      server.channels.collect { channel -> this@coroutineScope.launch { handleChannel(channel) } }
    } catch (e: Exception) {
      Logger.e("Error collecting server channels: ${e.message}", e, TAG)
    }
  }

  suspend fun run() = coroutineScope {
    launch { doAdvertise() }
    launch { doServe() }
  }

  companion object {
    const val TAG = "ClipbirdServerCoordinator"
  }
}
