package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.ffi

import com.srilakshmikanthanp.clipbird.ffi.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.NativeClipbirdLoader.library
import com.srilakshmikanthanp.clipbird.ffi.NativeFfiError
import com.srilakshmikanthanp.clipbird.hub.AdvertisingException
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.use
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private object BleAdvertiserFfiBindings {
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

  @OptIn(ExperimentalUuidApi::class)
  fun create(serviceUuid: Uuid, serviceData: ByteArray): MemorySegment {
    Arena.ofConfined().use { arena ->
      val uuidSeg = arena.allocateFrom(serviceUuid.toString())
      val dataSeg = arena.allocate(serviceData.size.toLong())
      MemorySegment.copy(serviceData, 0, dataSeg, ValueLayout.JAVA_BYTE, 0L, serviceData.size)
      val result = createHandle.invoke(uuidSeg, dataSeg, serviceData.size) as MemorySegment
      if (result == MemorySegment.NULL) throw AdvertisingException("Failed to create native BLE advertiser: ${NativeFfiError.lastErrorMessage()}")
      return result
    }
  }

  private val startHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_ble_advertiser_start"),
      FunctionDescriptor.of(
        ValueLayout.JAVA_BOOLEAN,
        ValueLayout.ADDRESS,
      ),
    )
  }

  fun start(advertiser: MemorySegment) {
    if (!(startHandle.invoke(advertiser) as Boolean)) {
      throw AdvertisingException("Failed to start advertising: ${NativeFfiError.lastErrorMessage()}")
    }
  }

  private val stopHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_ble_advertiser_stop"),
      FunctionDescriptor.of(
        ValueLayout.JAVA_BOOLEAN,
        ValueLayout.ADDRESS,
      ),
    )
  }

  fun stop(advertiser: MemorySegment) {
    if (!(stopHandle.invoke(advertiser) as Boolean)) {
      throw AdvertisingException("Failed to stop advertising: ${NativeFfiError.lastErrorMessage()}")
    }
  }

  private val destroyHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_ble_advertiser_destroy"),
      FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
    )
  }

  fun destroy(advertiser: MemorySegment) {
    destroyHandle.invoke(advertiser)
  }
}

@OptIn(ExperimentalUuidApi::class)
class BleAdvertiserFfi(
  serviceUuid: Uuid,
  serviceData: ByteArray,
) : AutoCloseable {
  private val advertiser = BleAdvertiserFfiBindings.create(serviceUuid, serviceData)

  private val cleanable = NativeCleaners.cleaner.register(this) {
    BleAdvertiserFfiBindings.destroy(advertiser)
  }

  fun start() {
    BleAdvertiserFfiBindings.start(advertiser)
  }

  fun stop() {
    BleAdvertiserFfiBindings.stop(advertiser)
  }

  override fun close() {
    cleanable.clean()
  }
}
