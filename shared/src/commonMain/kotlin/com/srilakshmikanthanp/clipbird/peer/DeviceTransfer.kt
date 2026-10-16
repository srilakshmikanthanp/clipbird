package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.pairing.PairedDevice

data class DeviceTransfer(
    val device: PairedDevice,
    val state: TransferState
)
