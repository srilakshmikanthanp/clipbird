package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.extensions.orThrow
import com.srilakshmikanthanp.clipbird.ffi.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.NativeError
import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import java.io.IOException
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import kotlin.use
import kotlin.uuid.ExperimentalUuidApi

private object BluetoothManagerHandle {
  @OptIn(ExperimentalUuidApi::class)
  fun connect(manager: MemorySegment, endpoint: BluetoothServerEndpoint): MemorySegment {
    return Arena.ofConfined().use { arena ->
      Clipbird.clipbird_bluetooth_manager_connect_rfcomm(
        manager,
        arena.allocateFrom(endpoint.address),
        arena.allocateFrom(endpoint.serviceUuid.toString())
      )
    }.orThrow {
      IOException("Failed to connect to Bluetooth device: ${NativeError.lastErrorMessage()}")
    }
  }

  @OptIn(ExperimentalUuidApi::class)
  fun start(manager: MemorySegment, config: BluetoothServerConfig): MemorySegment {
    return Arena.ofConfined().use { arena ->
      Clipbird.clipbird_bluetooth_manager_start_rfcomm_server(
        manager,
        arena.allocateFrom(config.serviceName),
        arena.allocateFrom(config.serviceUuid.toString())
      )
    }.orThrow {
      IOException("Failed to start Bluetooth server: ${NativeError.lastErrorMessage()}")
    }
  }

  fun create(): MemorySegment {
    return Clipbird.clipbird_bluetooth_manager_create().orThrow {
      IOException("Failed to create native Bluetooth manager: ${NativeError.lastErrorMessage()}")
    }
  }

  fun destroy(manager: MemorySegment) {
    Clipbird.clipbird_bluetooth_manager_destroy(manager)
  }
}

class NativeBluetoothManager : AutoCloseable {
  private val memorySegment = BluetoothManagerHandle.create()
  private val cleanable = NativeCleaners.cleaner.register(this) {
    BluetoothManagerHandle.destroy(memorySegment)
  }

  fun connect(endpoint: BluetoothServerEndpoint): MemorySegment {
    return BluetoothManagerHandle.connect(memorySegment, endpoint)
  }

  fun start(config: BluetoothServerConfig): MemorySegment {
    return BluetoothManagerHandle.start(memorySegment, config)
  }

  override fun close() {
    cleanable.clean()
  }
}
