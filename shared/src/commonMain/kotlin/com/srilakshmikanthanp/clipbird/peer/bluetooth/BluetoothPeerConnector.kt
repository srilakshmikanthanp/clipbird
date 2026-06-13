package com.srilakshmikanthanp.clipbird.peer.bluetooth

import com.srilakshmikanthanp.clipbird.common.HostDevice
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerEndpoint
import com.srilakshmikanthanp.clipbird.packet.*
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.PeerConnector
import com.srilakshmikanthanp.clipbird.peer.SignatureVerificationException
import com.srilakshmikanthanp.clipbird.utility.Nonce
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BluetoothPeerConnector(
  private val bluetoothManager: BluetoothManager,
  private val serviceUuid: Uuid,
  private val hostDevice: HostDevice,
) : PeerConnector<BluetoothPairedDevice> {
  override suspend fun connect(pairedDevice: BluetoothPairedDevice): Channel {
    val serverEndpoint = BluetoothServerEndpoint(pairedDevice.address, serviceUuid)
    val channel = bluetoothManager.connect(serverEndpoint)

    // Generate local nonce and send it to the remote device
    val localNonce = NoncePacket(Nonce.generateNonce()).also { channel.sendPacket(it) }

    // Receive remote nonce, sign it with the host device's
    // private key and send the signature back to the remote
    // device
    val remoteNonce = channel.nextPacket().asNoncePacket()
    val localSign = SignaturePacket(Nonce.signNonce(hostDevice.privateKey, remoteNonce.nonce))
    channel.sendPacket(localSign)

    // Receive the signature of the remote nonce and verify
    // it using the remote device's public key
    val remoteSign = channel.nextPacket().asSignaturePacket()

    if (!Nonce.verifyNonce(pairedDevice.publicKey, localNonce.nonce, remoteSign.signature)) {
      throw SignatureVerificationException("Failed to verify the signature")
    }

    // If the signature verification is successful, return
    // the channel for further communication
    return channel
  }
}
