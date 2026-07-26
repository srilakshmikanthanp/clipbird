package com.srilakshmikanthanp.clipbird.io

suspend fun Channel.readByte(): Byte = readExactly(1).first()
