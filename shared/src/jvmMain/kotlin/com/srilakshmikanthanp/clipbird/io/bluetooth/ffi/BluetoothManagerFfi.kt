package com.srilakshmikanthanp.clipbird.io.bluetooth.ffi

import com.srilakshmikanthanp.clipbird.ffi.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.NativeClipbirdLoader.library
import com.srilakshmikanthanp.clipbird.ffi.NativeFfiError
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerConfig
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerEndpoint
import java.io.IOException
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.use
import kotlin.uuid.ExperimentalUuidApi

private object BluetoothManagerFfiBindings {
  private val createHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_bluetooth_manager_create"),
      FunctionDescriptor.of(ValueLayout.ADDRESS),
    )
  }

  fun create(): MemorySegment {
    val result = createHandle.invoke() as MemorySegment
    if (result == MemorySegment.NULL) {
      throw IOException("Failed to create native Bluetooth manager: ${NativeFfiError.lastErrorMessage()}")
    }
    return result
  }

  private val connectHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_bluetooth_manager_connect"),
      FunctionDescriptor.of(
        ValueLayout.ADDRESS,
        ValueLayout.ADDRESS, // manager*
        ValueLayout.ADDRESS, // address (char*)
        ValueLayout.ADDRESS, // service_uuid (char*)
      ),
    )
  }

  @OptIn(ExperimentalUuidApi::class)
  fun connect(manager: MemorySegment, endpoint: BluetoothServerEndpoint): MemorySegment {
    Arena.ofConfined().use { arena ->
      val addressSeg = arena.allocateFrom(endpoint.address)
      val uuidSeg = arena.allocateFrom(endpoint.serviceUuid.toString())
      val result = connectHandle.invoke(manager, addressSeg, uuidSeg) as MemorySegment
      if (result == MemorySegment.NULL) {
        throw IOException("Failed to connect to Bluetooth device: ${NativeFfiError.lastErrorMessage()}")
      }
      return result
    }
  }

  private val startHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_bluetooth_manager_start"),
      FunctionDescriptor.of(
        ValueLayout.ADDRESS,
        ValueLayout.ADDRESS, // manager*
        ValueLayout.ADDRESS, // service_name (char*)
        ValueLayout.ADDRESS, // service_uuid (char*)
      ),
    )
  }

  @OptIn(ExperimentalUuidApi::class)
  fun start(manager: MemorySegment, config: BluetoothServerConfig): MemorySegment {
    Arena.ofConfined().use { arena ->
      val nameSeg = arena.allocateFrom(config.serviceName)
      val uuidSeg = arena.allocateFrom(config.serviceUuid.toString())
      val result = startHandle.invoke(manager, nameSeg, uuidSeg) as MemorySegment
      if (result == MemorySegment.NULL) {
        throw IOException("Failed to start Bluetooth server: ${NativeFfiError.lastErrorMessage()}")
      }
      return result
    }
  }

  private val destroyHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_bluetooth_manager_destroy"),
      FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
    )
  }

  fun destroy(manager: MemorySegment) {
    destroyHandle.invoke(manager)
  }
}

class BluetoothManagerFfi : AutoCloseable {
  private val memorySegment = BluetoothManagerFfiBindings.create()
  private val cleanable = NativeCleaners.cleaner.register(this) {
    BluetoothManagerFfiBindings.destroy(memorySegment)
  }

  fun connect(endpoint: BluetoothServerEndpoint): MemorySegment {
    return BluetoothManagerFfiBindings.connect(memorySegment, endpoint)
  }

  fun start(config: BluetoothServerConfig): MemorySegment {
    return BluetoothManagerFfiBindings.start(memorySegment, config)
  }

  override fun close() {
    cleanable.clean()
  }
}
