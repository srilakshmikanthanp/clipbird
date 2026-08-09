package com.srilakshmikanthanp.clipbird.crypto

import org.conscrypt.Conscrypt
import java.security.Provider

private val conscrypt: Provider by lazy { Conscrypt.newProvider() }
internal actual fun tlsProvider(): Provider = conscrypt
