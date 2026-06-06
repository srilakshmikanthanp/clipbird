package com.srilakshmikanthanp.clipbird

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform