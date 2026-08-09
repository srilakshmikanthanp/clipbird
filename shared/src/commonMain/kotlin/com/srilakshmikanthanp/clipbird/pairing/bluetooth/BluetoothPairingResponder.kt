package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.common.toCertificate
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import com.srilakshmikanthanp.clipbird.packet.PairingPacket
import com.srilakshmikanthanp.clipbird.packet.nextPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket
import com.srilakshmikanthanp.clipbird.pairing.InvalidPairingPacketException
import com.srilakshmikanthanp.clipbird.pairing.PairingFailedException
import com.srilakshmikanthanp.clipbird.pairing.PairingResponder
import com.srilakshmikanthanp.clipbird.pairing.PairingVerifier
import com.srilakshmikanthanp.clipbird.utility.CodeGenerator

class BluetoothPairingResponder(
  private val hostDeviceProvider: HostDeviceProvider,
  private val pairingVerifier: PairingVerifier,
) : PairingResponder<BluetoothChannel, BluetoothPairedDevice> {
  override suspend fun respond(channel: BluetoothChannel): BluetoothPairedDevice {
    val hostDevice = hostDeviceProvider.get()
    val packet = channel.nextPacket()

    if (packet !is PairingPacket) {
      throw InvalidPairingPacketException("Received invalid packet type: $packet")
    }

    val pairingPacket = PairingPacket(
      deviceId = hostDevice.id,
      deviceName = hostDevice.name,
      certificate = hostDevice.certificate.encoded
    )

    channel.sendPacket(pairingPacket)

    val remote = BluetoothPairedDevice(
      id = packet.deviceId,
      name = packet.deviceName,
      certificate = packet.certificate.toCertificate(),
      address = channel.remoteAddress,
    )

    val code = CodeGenerator.generate(
      hostDevice.certificate.encoded,
      remote.certificate.encoded,
    )

    if (pairingVerifier.verify(hostDevice, remote, code)) {
      return remote
    } else {
      throw PairingFailedException("Pairing verification failed for device: ${remote.name} (${remote.id})")
    }
  }
}
