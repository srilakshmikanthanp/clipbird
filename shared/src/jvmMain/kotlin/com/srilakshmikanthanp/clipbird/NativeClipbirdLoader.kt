package com.srilakshmikanthanp.clipbird

import java.nio.file.Files
import java.nio.file.StandardCopyOption

object NativeClipbirdLoader {
  val libraryPath by lazy {
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
}
