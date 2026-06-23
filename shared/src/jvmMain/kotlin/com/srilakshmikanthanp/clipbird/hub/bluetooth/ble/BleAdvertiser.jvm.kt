package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.NativeClipbirdLoader.library
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.hub.AdvertisingException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.use
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalSerializationApi::class)
actual class BleAdvertiser(private val serviceUuid: Uuid, private val device: BleHubDevice) : Advertiser<BleHubDevice> {
  private val _advertisedDevice: MutableStateFlow<BleHubDevice?> = MutableStateFlow(null)
  actual override val advertisedDevice = _advertisedDevice.asStateFlow()

  private var bleAdvertiserFfi: BleAdvertiserFfi? = null

  actual override suspend fun startAdvertising() {
    if (bleAdvertiserFfi != null) {
      throw IllegalStateException("Advertiser already started")
    }

    val bleAdvertiserFfi = BleAdvertiserFfi(serviceUuid, ProtoBuf.encodeToByteArray(device))

    runCatching {
      bleAdvertiserFfi.start()
    }.onFailure {
      bleAdvertiserFfi.close()
    }.onSuccess {
      this.bleAdvertiserFfi = bleAdvertiserFfi
      _advertisedDevice.value = device
    }.getOrThrow()
  }

  actual override suspend fun stopAdvertising() {
    val ffi = bleAdvertiserFfi ?: throw IllegalStateException("Not advertising")

    try {
      ffi.stop()
    } finally {
      ffi.close()
      bleAdvertiserFfi = null
      _advertisedDevice.value = null
    }
  }
}

private object NativeFfi {
  private val createHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_ble_advertiser_create"),
      FunctionDescriptor.of(
        ValueLayout.ADDRESS,
        ValueLayout.ADDRESS,
        ValueLayout.ADDRESS,
        ValueLayout.JAVA_INT,
      ),
    )
  }

  private val startHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_ble_advertiser_start"),
      FunctionDescriptor.of(
        ValueLayout.JAVA_INT,
        ValueLayout.ADDRESS,
      ),
    )
  }

  private val stopHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_ble_advertiser_stop"),
      FunctionDescriptor.of(
        ValueLayout.JAVA_INT,
        ValueLayout.ADDRESS,
      ),
    )
  }

  private val destroyHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_ble_advertiser_destroy"),
      FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
    )
  }

  private val lastErrorHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_ble_advertiser_last_error"),
      FunctionDescriptor.of(ValueLayout.ADDRESS),
    )
  }

  @OptIn(ExperimentalUuidApi::class)
  fun create(serviceUuid: Uuid, serviceData: ByteArray): MemorySegment {
    Arena.ofConfined().use { arena ->
      val uuidSeg = arena.allocateFrom(serviceUuid.toString())
      val dataSeg = arena.allocate(serviceData.size.toLong())
      MemorySegment.copy(serviceData, 0, dataSeg, ValueLayout.JAVA_BYTE, 0L, serviceData.size)
      val result = createHandle.invoke(uuidSeg, dataSeg, dataSeg.byteSize()) as MemorySegment
      if (result == MemorySegment.NULL) throw AdvertisingException("Failed to create native BLE advertiser: ${lastError()}")
      return result
    }
  }

  fun start(advertiser: MemorySegment) {
    if (startHandle.invoke(advertiser) as Int != 0) {
      throw AdvertisingException("Failed to start advertising: ${lastError()}")
    }
  }

  fun stop(advertiser: MemorySegment) {
    if (stopHandle.invoke(advertiser) as Int != 0) {
      throw AdvertisingException("Failed to stop advertising: ${lastError()}")
    }
  }

  fun destroy(advertiser: MemorySegment) {
    destroyHandle.invoke(advertiser)
  }

  fun lastError(): String {
    val ptr = lastErrorHandle.invoke() as MemorySegment
    if (ptr == MemorySegment.NULL) throw IllegalStateException("Failed to get last error")
    return ptr.getString(0)
  }
}

@OptIn(ExperimentalUuidApi::class)
private class BleAdvertiserFfi(
  serviceUuid: Uuid,
  serviceData: ByteArray,
) : AutoCloseable {
  private val handle: MemorySegment = NativeFfi.create(serviceUuid, serviceData)

  fun start() {
    NativeFfi.start(handle)
  }

  fun stop() {
    NativeFfi.stop(handle)
  }

  override fun close() {
    NativeFfi.destroy(handle)
  }
}
