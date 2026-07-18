package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.hub.Advertiser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
actual class BleAdvertiser(private val serviceUuid: Uuid, private val device: BleHubDevice) : Advertiser<BleHubDevice> {
  private val _advertisedDevice: MutableStateFlow<BleHubDevice?> = MutableStateFlow(null)
  actual override val advertisedDevice = _advertisedDevice.asStateFlow()

  private var nativeBleAdvertiser: NativeBleAdvertiser? = null

  actual override suspend fun startAdvertising() {
    if (nativeBleAdvertiser != null) {
      throw IllegalStateException("Advertiser already started")
    }

    val idBytes = ByteBuffer.allocate(8).putLong(device.id).array()
    val nativeBleAdvertiser = NativeBleAdvertiser(serviceUuid, idBytes)

    runCatching {
      nativeBleAdvertiser.start()
    }.onFailure {
      nativeBleAdvertiser.close()
    }.onSuccess {
      this.nativeBleAdvertiser = nativeBleAdvertiser
      _advertisedDevice.value = device
    }.getOrThrow()
  }

  actual override suspend fun stopAdvertising() {
    val ffi = nativeBleAdvertiser ?: throw IllegalStateException("Not advertising")

    try {
      ffi.stop()
    } finally {
      ffi.close()
      nativeBleAdvertiser = null
      _advertisedDevice.value = null
    }
  }
}
