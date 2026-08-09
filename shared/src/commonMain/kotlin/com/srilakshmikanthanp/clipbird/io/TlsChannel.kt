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

class TlsChannel private constructor(
  private val channel: Channel,
  private val engine: SSLEngine,
) : Channel {
  private var application = ByteBuffer.allocate(engine.session.applicationBufferSize)
  private var packet = ByteBuffer.allocate(engine.session.packetBufferSize)
  private var ciphertext: ByteBuffer = EMPTY
  private val plaintext = ByteQueue()
  private val writing = Mutex()

  lateinit var peerCertificate: X509Certificate private set

  private suspend fun Channel.readTlsRecord(): ByteBuffer {
    val recordHeaderSize = 5
    val maxRecordBody = 18432
    val header = readExactly(recordHeaderSize)
    val length = ((header[3].toInt() and 0xFF) shl 8) or (header[4].toInt() and 0xFF)

    if (length > maxRecordBody) {
      throw IOException("Peer announced an oversized TLS record of $length bytes")
    }

    val array = if (length == 0) {
      header
    } else {
      header + readExactly(length)
    }

    return ByteBuffer.wrap(array)
  }

  private suspend fun send(record: ByteArray) {
    if (record.isNotEmpty()) channel.write(record)
  }

  private fun runPendingTasks() {
    while (true) (engine.delegatedTask ?: return).run()
  }

  private fun encrypt(source: ByteBuffer = EMPTY): ByteArray {
    loop@ while (true) {
      packet.clear()
      when (val status = engine.wrap(source, packet).status) {
        Status.BUFFER_OVERFLOW -> packet = ByteBuffer.allocate(packet.capacity() * 2)
        Status.OK, Status.CLOSED -> break@loop
        else -> throw IOException("Unexpected TLS wrap status $status")
      }
    }

    packet.flip()
    val result = ByteArray(packet.remaining())
    packet.get(result)
    return result
  }

  private suspend fun unwrapUntilSettled(): Status {
    while (true) {
      application.clear()

      when (val status = engine.unwrap(ciphertext, application).status) {
        Status.BUFFER_OVERFLOW -> application = ByteBuffer.allocate(application.capacity() * 2)
        Status.BUFFER_UNDERFLOW -> ciphertext = ciphertext.followedBy(channel.readTlsRecord())
        Status.OK, Status.CLOSED -> return status
        else -> throw IOException("Unexpected TLS unwrap status $status")
      }
    }
  }

  private suspend fun decrypt(readMore: Boolean) {
    if (readMore && !ciphertext.hasRemaining()) ciphertext = channel.readTlsRecord()

    val status = unwrapUntilSettled()

    application.flip()

    val produced = application.remaining()

    if (produced > 0) {
      plaintext.append(ByteArray(produced).also(application::get))
    } else if (status == Status.CLOSED) {
      throw EOFException("TLS peer closed the connection")
    }
  }

  private suspend fun handshake() {
    engine.beginHandshake()

    loop@ while (true) {
      when (engine.handshakeStatus) {
        HandshakeStatus.NEED_UNWRAP -> decrypt(readMore = true)
        HandshakeStatus.NEED_WRAP -> send(encrypt())
        HandshakeStatus.NEED_TASK -> runPendingTasks()
        HandshakeStatus.FINISHED, HandshakeStatus.NOT_HANDSHAKING -> break@loop
        else -> decrypt(readMore = false)
      }
    }

    peerCertificate = engine
      .session
      .peerCertificates
      .firstOrNull() as? X509Certificate
      ?: throw SSLHandshakeException("Peer completed the handshake without a certificate")
  }

  override suspend fun readExactly(size: Int): ByteArray {
    require(size >= 0) { "size must not be negative" }
    while (plaintext.available < size) decrypt(readMore = true)
    return plaintext.take(size)
  }

  override suspend fun write(data: ByteArray, offset: Int, length: Int): Unit = writing.withLock {
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

  override fun close() {
    runCatching { engine.closeOutbound() }
    channel.close()
  }

  companion object {
    private val EMPTY: ByteBuffer = ByteBuffer.allocate(0)

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
