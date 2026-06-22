package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.NativeClipbirdLoader.library
import com.srilakshmikanthanp.clipbird.hub.AdvertisingException
import java.lang.foreign.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

  private val destroyHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_ble_advertiser_destroy"),
      FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
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
class BleAdvertiserFfi(
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
