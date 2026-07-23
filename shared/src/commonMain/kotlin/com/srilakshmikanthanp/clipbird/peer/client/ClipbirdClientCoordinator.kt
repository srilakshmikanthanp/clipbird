package com.srilakshmikanthanp.clipbird.peer.client

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.common.closeQuietly
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.paring.ActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.ChannelConnectionChecker
import com.srilakshmikanthanp.clipbird.peer.ConnectedDevice
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
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
  private val jobs = mutableMapOf<Long, Job>()

  private fun CoroutineScope.job(device: P): Job = launch {
    while (isActive) {
      connect(device).also { delay(RETRY_DELAY) }
    }
  }

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

  private suspend fun CoroutineScope.doRun() {
    activeDeviceProvider.devices.collect { devices ->
      val current = devices.associateBy { it.id }
      val removed = jobs.keys - current.keys

      removed.forEach { id ->
        jobs.remove(id)?.cancel()
      }

      current.forEach { (id, device) ->
        if (id !in jobs) jobs[id] = this.job(device)
      }
    }
  }

  suspend fun run(): Unit = coroutineScope {
    while (isActive) {
      try {
        this.doRun()
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Logger.e("Error in client coordinator: ${e.message}", e, TAG)
        delay(RETRY_DELAY)
      }
    }
  }

  companion object {
    const val TAG = "ClipbirdClientCoordinator"
    val RETRY_DELAY = 5.seconds
  }
}
