package com.srilakshmikanthanp.clipbird.ffi.loader

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

@Suppress("UnsafeDynamicallyLoadedCode")
object NativeClipbirdLoader {
  private val loaded by lazy {
    val extension = when {
      System.getProperty("os.name").startsWith("Windows", true) -> ".dll"
      System.getProperty("os.name").startsWith("Linux", true) -> ".so"
      System.getProperty("os.name").startsWith("Mac", true) -> ".dylib"
      else -> error("Unsupported OS: ${System.getProperty("os.name")}")
    }

    val dir = Path.of(System.getProperty("user.home"), ".clipbird")

    Files.createDirectories(dir)

    val path = javaClass.getResourceAsStream("/native/libclipbird$extension")?.use { input ->
      val temp = Files.createTempFile(dir, "clipbird-", extension)
      temp.toFile().deleteOnExit()
      Files.copy(input, temp, StandardCopyOption.REPLACE_EXISTING)
      temp
    } ?: error("Native library not found: /native/libclipbird$extension")

    System.load(path.toAbsolutePath().toString())
  }

  fun load() = loaded
}
