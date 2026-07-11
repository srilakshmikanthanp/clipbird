package com.srilakshmikanthanp.clipbird.ffi.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.error.NativeErrorHandle
import com.srilakshmikanthanp.clipbird.ffi.extensions.orThrow
import java.io.IOException
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import kotlin.use
import kotlin.uuid.ExperimentalUuidApi

object BluetoothManagerHandle {
  @OptIn(ExperimentalUuidApi::class)
  fun connect(manager: MemorySegment, address: String, serviceUuid: String): MemorySegment {
    return Arena.ofConfined().use { arena ->
      Clipbird.clipbird_bluetooth_manager_connect_rfcomm(manager, arena.allocateFrom(address), arena.allocateFrom(serviceUuid))
    }.orThrow {
      IOException("Failed to connect to Bluetooth device: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  @OptIn(ExperimentalUuidApi::class)
  fun start(manager: MemorySegment, serviceName: String, serviceUuid: String): MemorySegment {
    return Arena.ofConfined().use { arena ->
      Clipbird.clipbird_bluetooth_manager_start_rfcomm_server(manager, arena.allocateFrom(serviceName), arena.allocateFrom(serviceUuid))
    }.orThrow {
      IOException("Failed to start Bluetooth server: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun create(): MemorySegment {
    return Clipbird.clipbird_bluetooth_manager_create().orThrow {
      IOException("Failed to create native Bluetooth manager: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun destroy(manager: MemorySegment) {
    Clipbird.clipbird_bluetooth_manager_destroy(manager)
  }
}
