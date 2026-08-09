package com.srilakshmikanthanp.clipbird.io

import com.srilakshmikanthanp.clipbird.common.HostDevice
import com.srilakshmikanthanp.clipbird.common.followedBy
import com.srilakshmikanthanp.clipbird.crypto.PeerTrustManagers
import com.srilakshmikanthanp.clipbird.crypto.SslEngines
import com.srilakshmikanthanp.clipbird.utility.ByteQueue
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.io.IOException
import java.nio.ByteBuffer
import java.security.cert.X509Certificate
import javax.net.ssl.SSLEngine
import javax.net.ssl.SSLEngineResult.HandshakeStatus
import javax.net.ssl.SSLEngineResult.Status
import javax.net.ssl.SSLHandshakeException

private class TlsReader(private val channel: Channel, private val engine: SSLEngine) {
  private var application = ByteBuffer.allocate(engine.session.applicationBufferSize)
  private var ciphertext: ByteBuffer = ByteBuffer.allocate(0)
  private val plaintext = ByteQueue()

  private suspend fun readRecord(): ByteBuffer {
    val header = channel.readExactly(HEADER_SIZE)
    val length = ((header[3].toInt() and 0xFF) shl 8) or (header[4].toInt() and 0xFF)

    if (length > MAX_BODY) {
      throw IOException("Peer announced an oversized TLS record of $length bytes")
    }

    return ByteBuffer.wrap(if (length == 0) header else header + channel.readExactly(length))
  }

  private suspend fun unwrap(): Status {
    while (true) {
      application.clear()
      when (val status = engine.unwrap(ciphertext, application).status) {
        Status.BUFFER_OVERFLOW -> application = ByteBuffer.allocate(application.capacity() * 2)
        Status.BUFFER_UNDERFLOW -> ciphertext = ciphertext.followedBy(readRecord())
        Status.OK, Status.CLOSED -> return status
        else -> throw IOException("Unexpected TLS unwrap status $status")
      }
    }
  }

  suspend fun unwrapBuffered() {
    val status = unwrap()

    application.flip()

    val produced = application.remaining()

    if (produced > 0) {
      plaintext.append(ByteArray(produced).also(application::get))
    } else if (status == Status.CLOSED) {
      throw EOFException("TLS peer closed the connection")
    }
  }

  suspend fun readAndDecrypt() {
    if (!ciphertext.hasRemaining()) ciphertext = readRecord()
    unwrapBuffered()
  }

  suspend fun read(size: Int): ByteArray {
    while (plaintext.available < size) readAndDecrypt()
    return plaintext.take(size)
  }

  private companion object {
    const val HEADER_SIZE = 5
    const val MAX_BODY = 18432
  }
}

private class TlsWriter(private val channel: Channel, private val engine: SSLEngine) {
  private var packet = ByteBuffer.allocate(engine.session.packetBufferSize)
  private val mutex = Mutex()

  private fun encrypt(source: ByteBuffer): ByteArray {
    loop@ while (true) {
      packet.clear()
      when (val status = engine.wrap(source, packet).status) {
        Status.BUFFER_OVERFLOW -> packet = ByteBuffer.allocate(packet.capacity() * 2)
        Status.OK, Status.CLOSED -> break@loop
        else -> throw IOException("Unexpected TLS wrap status $status")
      }
    }

    packet.flip()

    return ByteArray(
      packet.remaining()
    ).also(
      packet::get
    )
  }

  suspend fun sendHandshakeRecord() = mutex.withLock {
    encrypt(ByteBuffer.allocate(0)).takeIf {
      it.isNotEmpty()
    }?.let {
      channel.write(it)
    }
  }

  suspend fun write(data: ByteArray, offset: Int, length: Int): Unit = mutex.withLock {
    if (length == 0) return
    val source = ByteBuffer.wrap(data, offset, length)
    val output = ByteArrayOutputStream()

    while (source.hasRemaining()) {
      val record = encrypt(source)

      if (record.isEmpty()) {
        throw IOException("TLS engine produced no output while plaintext remained")
      }

      output.write(record)
    }

    channel.write(output.toByteArray())
  }
}

class TlsChannel private constructor(
  private val channel: Channel,
  private val engine: SSLEngine,
) : Channel {
  lateinit var peerCertificate: X509Certificate private set

  private val reader = TlsReader(channel, engine)
  private val writer = TlsWriter(channel, engine)

  private suspend fun handshake() {
    engine.beginHandshake()

    loop@ while (true) {
      when (engine.handshakeStatus) {
        HandshakeStatus.NEED_UNWRAP -> reader.readAndDecrypt()
        HandshakeStatus.NEED_WRAP -> writer.sendHandshakeRecord()
        HandshakeStatus.NEED_TASK -> runPendingTasks()
        HandshakeStatus.FINISHED, HandshakeStatus.NOT_HANDSHAKING -> break@loop
        else -> reader.unwrapBuffered()
      }
    }

    peerCertificate = engine
      .session
      .peerCertificates
      .firstOrNull() as? X509Certificate
      ?: throw SSLHandshakeException("Peer completed the handshake without a certificate")
  }

  private fun runPendingTasks() {
    while (true) (engine.delegatedTask ?: return).run()
  }

  override suspend fun readExactly(size: Int): ByteArray {
    require(size >= 0) { "size must not be negative" }
    return reader.read(size)
  }

  override suspend fun write(data: ByteArray, offset: Int, length: Int) {
    writer.write(data, offset, length)
  }

  override fun close() {
    channel.use { engine.closeOutbound() }
  }

  companion object {
    private suspend fun open(
      channel: Channel,
      host: HostDevice,
      trusted: Collection<X509Certificate>,
      client: Boolean,
    ): TlsChannel {
      val trustManager = PeerTrustManagers.forCertificates(trusted)
      val engine = SslEngines.create(host.privateKey, host.certificate, trustManager, client)
      return TlsChannel(channel, engine).also { it.handshake() }
    }

    suspend fun client(
      channel: Channel,
      host: HostDevice,
      trusted: Collection<X509Certificate>,
    ): TlsChannel = open(
      channel,
      host,
      trusted,
      client = true
    )

    suspend fun server(
      channel: Channel,
      host: HostDevice,
      trusted: Collection<X509Certificate>,
    ): TlsChannel = open(
      channel,
      host,
      trusted,
      client = false
    )
  }
}
