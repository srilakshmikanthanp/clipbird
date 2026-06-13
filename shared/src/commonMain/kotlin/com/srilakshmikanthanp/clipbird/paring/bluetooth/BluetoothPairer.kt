package com.srilakshmikanthanp.clipbird.paring.bluetooth

import com.srilakshmikanthanp.clipbird.common.HostDevice
import com.srilakshmikanthanp.clipbird.common.toPublicKey
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerEndpoint
import com.srilakshmikanthanp.clipbird.packet.PairingPacket
import com.srilakshmikanthanp.clipbird.packet.nextPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket
import com.srilakshmikanthanp.clipbird.paring.InvalidPairingPacketException
import com.srilakshmikanthanp.clipbird.paring.Pairer
import com.srilakshmikanthanp.clipbird.paring.PairingVerifier
import com.srilakshmikanthanp.clipbird.paring.PairingFailedException
import com.srilakshmikanthanp.clipbird.utility.CodeGenerator
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalSerializationApi::class)
class BluetoothPairer(
  private val bluetoothManager: BluetoothManager,
  private val hostDevice: HostDevice,
  private val pairingVerifier: PairingVerifier,
  private val serviceUuid: Uuid,
): Pairer<BluetoothPairingCandidate, BluetoothPairedDevice> {
  override suspend fun pair(candidate: BluetoothPairingCandidate): BluetoothPairedDevice {
    val serverEndpoint = BluetoothServerEndpoint(candidate.address, serviceUuid)
    val channel = bluetoothManager.connect(serverEndpoint)
    val pairingPacket = PairingPacket(hostDevice.id, hostDevice.name, hostDevice.publicKey.encoded)
    channel.sendPacket(pairingPacket)

    val packet = channel.nextPacket()

    if (packet !is PairingPacket) {
      throw InvalidPairingPacketException("Received invalid packet type: $packet")
    }

    val remote = BluetoothPairedDevice(
      publicKey = packet.publicKey.toPublicKey(),
      name = packet.deviceName,
      id = packet.deviceId,
      address = candidate.address
    )

    val code = CodeGenerator.generate(
      hostDevice.publicKey.encoded,
      packet.publicKey
    )

    if (pairingVerifier.verify(hostDevice, remote, code)) {
      return remote
    } else {
      throw PairingFailedException("Pairing verification failed for device: ${remote.name} (${remote.id})")
    }
  }
}
