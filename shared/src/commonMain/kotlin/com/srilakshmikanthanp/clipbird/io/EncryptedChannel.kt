package com.srilakshmikanthanp.clipbird.io

import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptedChannel(private val channel: Channel, key: ByteArray) : Channel {
  private val secretKey = SecretKeySpec(key, "AES")
  private val random = SecureRandom()
  private var readBuffer = ByteArray(0)
  private var bufferPos = 0

  private fun encrypt(data: ByteArray, offset: Int, length: Int, iv: ByteArray): ByteArray {
    val cipher = Cipher.getInstance(TRANSFORMATION)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(TAG_BITS, iv))
    val output = ByteArray(cipher.getOutputSize(length))
    val written = cipher.doFinal(data, offset, length, output, 0)
    return if (written == output.size) output else output.copyOf(written)
  }

  private fun decrypt(ciphertext: ByteArray, iv: ByteArray): ByteArray {
    val cipher = Cipher.getInstance(TRANSFORMATION)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(TAG_BITS, iv))
    return cipher.doFinal(ciphertext)
  }

  override suspend fun readExactly(size: Int): ByteArray {
    while (readBuffer.size - bufferPos < size) {
      val frameLength = ByteBuffer.wrap(channel.readExactly(Int.SIZE_BYTES)).int
      val ivLength = channel.readByte().toInt() and 0xFF
      val iv = channel.readExactly(ivLength)
      val ciphertext = channel.readExactly(frameLength - Byte.SIZE_BYTES - ivLength)
      val plaintext = decrypt(ciphertext, iv)
      val remaining = readBuffer.copyOfRange(bufferPos, readBuffer.size)
      readBuffer = remaining + plaintext
      bufferPos = 0
    }

    return readBuffer.copyOfRange(
      bufferPos, bufferPos + size
    ).also {
      bufferPos += size
    }
  }

  override suspend fun write(data: ByteArray, offset: Int, length: Int) {
    val iv = ByteArray(IV_LENGTH).also(random::nextBytes)
    val ciphertext = encrypt(data, offset, length, iv)
    val frame = ByteBuffer.allocate(Int.SIZE_BYTES + Byte.SIZE_BYTES + iv.size + ciphertext.size)
      .putInt(Byte.SIZE_BYTES + iv.size + ciphertext.size)
      .put(iv.size.toByte())
      .put(iv)
      .put(ciphertext)
      .array()
    channel.write(frame)
  }

  override fun close() = channel.close()

  companion object {
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_LENGTH = 12
    private const val TAG_BITS = 128
  }
}
