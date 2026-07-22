package com.srilakshmikanthanp.clipbird.packet

import com.srilakshmikanthanp.clipbird.io.Channel
import java.nio.ByteBuffer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

suspend fun Channel.sendPacket(packet: Packet) {
  val encoded = packet.toBytes()
  val length = encoded.size
  val type = packet.getType()
  val buffer = ByteBuffer.allocate(Int.SIZE_BYTES + Int.SIZE_BYTES + length)
    .putInt(length)
    .putInt(type.value)
    .put(encoded)
    .array()
  this.write(buffer)
}

suspend fun Channel.nextPacket(): Packet {
  val length: Int = ByteBuffer.wrap(readExactly(4)).int
  val type: Int = ByteBuffer.wrap(readExactly(4)).int
  val data: ByteArray = readExactly(length)
  val packetType = PacketType.from(type)
  return data.toPacket(packetType)
}

fun Channel.readPackets(
  interceptor: PacketInterceptor
): Flow<Packet> = flow {
  while (true) {
    val packet = interceptor.intercept(this@readPackets, nextPacket())
    if (packet != null) {
      emit(packet)
    }
  }
}
