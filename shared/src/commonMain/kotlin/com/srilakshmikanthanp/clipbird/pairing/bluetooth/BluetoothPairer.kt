package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.common.toCertificate
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerEndpoint
import com.srilakshmikanthanp.clipbird.packet.PairingPacket
import com.srilakshmikanthanp.clipbird.packet.nextPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket
import com.srilakshmikanthanp.clipbird.pairing.InvalidPairingPacketException
import com.srilakshmikanthanp.clipbird.pairing.Pairer
import com.srilakshmikanthanp.clipbird.pairing.PairingVerifier
import com.srilakshmikanthanp.clipbird.pairing.PairingFailedException
import com.srilakshmikanthanp.clipbird.utility.CodeGenerator
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BluetoothPairer(
  private val bluetoothManager: BluetoothManager,
  private val hostDeviceProvider: HostDeviceProvider,
  private val pairingVerifier: PairingVerifier,
  private val serviceUuid: Uuid,
): Pairer<BluetoothPairingCandidate, BluetoothPairedDevice> {
  override suspend fun pair(candidate: BluetoothPairingCandidate): BluetoothPairedDevice {
    val hostDevice = hostDeviceProvider.get()
    val serverEndpoint = BluetoothServerEndpoint(candidate.address, serviceUuid)
    val channel = bluetoothManager.connect(serverEndpoint)
    val pairingPacket = PairingPacket(hostDevice.id, hostDevice.name, hostDevice.certificate.encoded)
    channel.sendPacket(pairingPacket)

    val packet = channel.nextPacket()

    if (packet !is PairingPacket) {
      throw InvalidPairingPacketException("Received invalid packet type: $packet")
    }

    val remote = BluetoothPairedDevice(
      certificate = packet.certificate.toCertificate(),
      name = packet.deviceName,
      id = packet.deviceId,
      address = candidate.address
    )

    val code = CodeGenerator.generate(
      hostDevice.certificate.encoded,
      remote.certificate.encoded
    )

    if (pairingVerifier.verify(hostDevice, remote, code)) {
      return remote
    } else {
      throw PairingFailedException("Pairing verification failed for device: ${remote.name} (${remote.id})")
    }
  }
}
