package com.srilakshmikanthanp.clipbird

import java.nio.file.Path

object AppDirs {
  val home: Path = Path.of(System.getProperty("user.home"), ".clipbird")
}