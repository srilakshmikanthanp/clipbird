package com.srilakshmikanthanp.clipbird.packet

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.io.write
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.ProgressListener
import com.srilakshmikanthanp.clipbird.packet.interceptor.PacketInterceptor
import java.nio.ByteBuffer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

suspend fun Channel.sendPacket(packet: Packet, progressListener: ProgressListener) {
  val encoded = packet.toBytes()
  val length = encoded.size
  val type = packet.getType()
  val buffer = ByteBuffer.allocate(Int.SIZE_BYTES + Int.SIZE_BYTES + encoded.size)
    .putInt(length)
    .putInt(type.value)
    .put(encoded)
    .array()
  this.write(data = buffer, listener = progressListener)
}

suspend fun Channel.sendPacket(packet: Packet) {
  sendPacket(packet, ProgressListener.NO_OP)
}

suspend fun Channel.trySendPacket(packet: Packet) {
  try {
    sendPacket(packet)
  } catch (e: Exception) {
    Logger.e("Failed to send packet: ${e.message}", e)
  }
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
