package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
actual class BleAdvertiser(
  private val serviceUuid: Uuid,
  private val hostDeviceProvider: HostDeviceProvider,
) : Advertiser {
  actual override suspend fun advertise() {
    val idBytes = ByteBuffer.allocate(8).putLong(hostDeviceProvider.get().id).array()
    NativeBleAdvertiser(serviceUuid, idBytes).use { native ->
      native.start()
      awaitCancellation()
    }
  }
}
