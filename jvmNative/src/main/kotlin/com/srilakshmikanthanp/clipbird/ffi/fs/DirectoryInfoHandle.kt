package com.srilakshmikanthanp.clipbird.ffi.fs

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.error.NativeErrorHandle
import com.srilakshmikanthanp.clipbird.ffi.extensions.orThrow
import java.io.IOException
import java.lang.foreign.Arena
import java.lang.foreign.ValueLayout
import java.nio.file.Path

object DirectoryInfoHandle {
  fun isNoexec(path: Path): Boolean = Arena.ofConfined().use { arena ->
    val result = arena.allocate(ValueLayout.JAVA_BOOLEAN)
    Clipbird.clipbird_fs_directory_info_is_noexec(arena.allocateFrom(path.toString()), result).orThrow {
      IOException("Failed to read directory info for $path: ${NativeErrorHandle.lastErrorMessage()}")
    }
    result.get(ValueLayout.JAVA_BOOLEAN, 0)
  }
}
