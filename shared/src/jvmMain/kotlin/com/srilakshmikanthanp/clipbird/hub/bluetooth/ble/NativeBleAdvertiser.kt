package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.NativeClipbirdLoader
import com.srilakshmikanthanp.clipbird.hub.AdvertisingException
import java.lang.foreign.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private object Native {
  private val lib: SymbolLookup by lazy {
    SymbolLookup.libraryLookup(
      NativeClipbirdLoader.libraryPath, Arena.global()
    )
  }

  private val linker = Linker.nativeLinker()

  private val createHandle by lazy {
    linker.downcallHandle(
      lib.findOrThrow("clipbird_ble_advertiser_create"),
      FunctionDescriptor.of(
        ValueLayout.ADDRESS,
        ValueLayout.ADDRESS,
        ValueLayout.ADDRESS,
        ValueLayout.JAVA_INT,
      ),
    )
  }

  private val destroyHandle by lazy {
    linker.downcallHandle(
      lib.findOrThrow("clipbird_ble_advertiser_destroy"),
      FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
    )
  }

  private val startHandle by lazy {
    linker.downcallHandle(
      lib.findOrThrow("clipbird_ble_advertiser_start"),
      FunctionDescriptor.of(
        ValueLayout.JAVA_INT,
        ValueLayout.ADDRESS,
      ),
    )
  }

  private val stopHandle by lazy {
    linker.downcallHandle(
      lib.findOrThrow("clipbird_ble_advertiser_stop"),
      FunctionDescriptor.of(
        ValueLayout.JAVA_INT,
        ValueLayout.ADDRESS,
      ),
    )
  }

  private val isAdvertisingHandle by lazy {
    linker.downcallHandle(
      lib.findOrThrow("clipbird_ble_advertiser_is_advertising"),
      FunctionDescriptor.of(
        ValueLayout.JAVA_INT,
        ValueLayout.ADDRESS,
        ValueLayout.ADDRESS,
      ),
    )
  }

  private val lastErrorHandle by lazy {
    linker.downcallHandle(
      lib.findOrThrow("clipbird_ble_advertiser_last_error"),
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

  fun destroy(advertiser: MemorySegment) {
    destroyHandle.invoke(advertiser)
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

  fun isAdvertising(advertiser: MemorySegment): Boolean {
    Arena.ofConfined().use { arena ->
      val flagSeg = arena.allocate(ValueLayout.JAVA_BYTE)
      if (isAdvertisingHandle.invoke(advertiser, flagSeg) as Int != 0) {
        throw AdvertisingException("Failed to check advertising status: ${lastError()}")
      }
      return flagSeg.get(ValueLayout.JAVA_BYTE, 0L) != 0.toByte()
    }
  }

  fun lastError(): String {
    val ptr = lastErrorHandle.invoke() as MemorySegment
    if (ptr == MemorySegment.NULL) throw IllegalStateException("Failed to get last error")
    return ptr.getString(0)
  }
}

@OptIn(ExperimentalUuidApi::class)
class NativeBleAdvertiser(
  private val serviceUuid: Uuid,
  private val serviceData: ByteArray,
) : AutoCloseable {
  private val handle: MemorySegment = Native.create(serviceUuid, serviceData)

  fun start() {
    Native.start(handle)
  }

  fun stop() {
    Native.stop(handle)
  }

  fun isAdvertising(): Boolean {
    return Native.isAdvertising(handle)
  }

  override fun close() {
    Native.destroy(handle)
  }
}
