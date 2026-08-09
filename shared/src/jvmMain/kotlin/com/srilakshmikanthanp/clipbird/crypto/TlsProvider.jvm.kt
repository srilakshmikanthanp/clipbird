package com.srilakshmikanthanp.clipbird.crypto

import java.security.Provider
import javax.net.ssl.SSLContext

val provider: Provider by lazy { SSLContext.getDefault().provider }
internal actual fun tlsProvider(): Provider = provider
