package com.srilakshmikanthanp.clipbird.paring

open class PairingException (
  message: String,
  cause: Throwable? = null
) : Exception(message, cause)

class InvalidPairingPacketException(
  message: String
) : PairingException(message)

class PairingFailedException(
  message: String
) : PairingException(message)

class AlreadyPairingException(
  message: String
) : PairingException(message)

class IllegalPairingCandidateException(
  candidate: PairingCandidate
) : PairingException("Unsupported pairing candidate: ${candidate::class.simpleName}")
