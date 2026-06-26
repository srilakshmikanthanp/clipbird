package com.srilakshmikanthanp.clipbird.ffi

import java.lang.foreign.Arena
import java.lang.foreign.SymbolLookup
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal object NativeClipbirdLoader {
  private val libraryPath by lazy {
    val extension = when {
      System.getProperty("os.name").startsWith("Windows", ignoreCase = true) -> ".dll"
      System.getProperty("os.name").startsWith("Linux", ignoreCase = true) -> ".so"
      else -> throw UnsupportedOperationException("Unsupported OS: ${System.getProperty("os.name")}")
    }

    javaClass.getResourceAsStream("/native/libclipbird$extension")?.use {
      val file = Files.createTempFile("clipbird", extension).toFile()
      file.deleteOnExit()
      Files.copy(it, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
      file.absolutePath
    } ?: throw IllegalStateException("Native library not found in resources")
  }

  val library: SymbolLookup by lazy {
    SymbolLookup.libraryLookup(libraryPath, Arena.global())
  }
}
