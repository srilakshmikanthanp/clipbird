package com.srilakshmikanthanp.clipbird.peer.server

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.common.closeQuietly
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.PeerConnection
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

open class ClipbirdProtocolServer<P: PairedDevice>(
  private val advertiser: Advertiser,
  private val server: ClipbirdServer,
  private val handshakeProtocol: ClipbirdServerHandshakeProtocol<P>
) {
  private val _devices = MutableSharedFlow<PeerConnection>(extraBufferCapacity = 64)
  val devices: SharedFlow<PeerConnection> = _devices.asSharedFlow()

  private suspend fun handleChannel(channel: Channel) {
    try {
      val (device, secureChannel) = handshakeProtocol.handshake(channel)
      _devices.emit(PeerConnection(device, secureChannel))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Logger.e("Handshake failed: ${e.message}", e, TAG)
      channel.closeQuietly()
    }
  }

  private suspend fun doAdvertise() = coroutineScope {
    while (isActive) {
      try {
        advertiser.advertise()
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Logger.e("Error in BLE advertising: ${e.message}", e, TAG)
        delay(5.seconds)
      }
    }
  }

  private suspend fun doServe() = coroutineScope {
    while (isActive) {
      try {
        server.channels.collect { channel -> this@coroutineScope.launch { handleChannel(channel) } }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Logger.e("Error collecting server channels: ${e.message}", e, TAG)
        delay(5.seconds)
      }
    }
  }

  suspend fun run() = coroutineScope {
    launch { doAdvertise() }
    launch { doServe() }
  }

  companion object {
    const val TAG = "ClipbirdProtocolServer"
  }
}
