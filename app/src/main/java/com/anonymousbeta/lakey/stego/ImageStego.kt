package com.anonymousbeta.lakey.stego

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import kotlin.math.min

class ImageStego {

    // 4-bit redundancy + length header so it survives moderate compression
    fun embed(context: Context, imageUri: Uri, payload: ByteArray): File {
        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalArgumentException("Cannot open image")
        val original = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val mutable = original.copy(Bitmap.Config.ARGB_8888, true)
        original.recycle()

        val width = mutable.width
        val height = mutable.height
        val capacity = (width * height * 3) / 2 // using 4 bits per channel roughly

        if (payload.size + 8 > capacity) {
            throw IllegalArgumentException("Payload too large for this image. Use a larger image.")
        }

        // Header: 4 bytes magic + 4 bytes length
        val header = ByteBuffer.allocate(8)
            .putInt(0x4C414B45) // "LAKE"
            .putInt(payload.size)
            .array()

        val data = header + payload
        var dataIndex = 0
        var bitIndex = 0

        outer@ for (y in 0 until height) {
            for (x in 0 until width) {
                if (dataIndex >= data.size) break@outer

                var pixel = mutable.getPixel(x, y)
                val a = pixel shr 24 and 0xFF
                var r = pixel shr 16 and 0xFF
                var g = pixel shr 8 and 0xFF
                var b = pixel and 0xFF

                // Embed 2 bits per channel (R,G,B) = 6 bits per pixel
                for (channel in 0..2) {
                    if (dataIndex >= data.size) break
                    val byte = data[dataIndex].toInt() and 0xFF
                    val bits = (byte shr (6 - bitIndex)) and 0x03

                    when (channel) {
                        0 -> r = (r and 0xFC) or bits
                        1 -> g = (g and 0xFC) or bits
                        2 -> b = (b and 0xFC) or bits
                    }

                    bitIndex += 2
                    if (bitIndex >= 8) {
                        bitIndex = 0
                        dataIndex++
                    }
                }

                pixel = (a shl 24) or (r shl 16) or (g shl 8) or b
                mutable.setPixel(x, y, pixel)
            }
        }

        val outFile = File(context.cacheDir, "lakey_${System.currentTimeMillis()}.png")
        FileOutputStream(outFile).use { fos ->
            mutable.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
        mutable.recycle()
        return outFile
    }

    fun extract(bitmap: Bitmap): ByteArray? {
        val width = bitmap.width
        val height = bitmap.height
        val bytes = mutableListOf<Byte>()
        var currentByte = 0
        var bitCount = 0

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                val r = pixel shr 16 and 0xFF
                val g = pixel shr 8 and 0xFF
                val b = pixel and 0xFF

                for (channel in listOf(r, g, b)) {
                    val bits = channel and 0x03
                    currentByte = (currentByte shl 2) or bits
                    bitCount += 2
                    if (bitCount >= 8) {
                        bytes.add(currentByte.toByte())
                        currentByte = 0
                        bitCount = 0

                        // Check header after we have 8 bytes
                        if (bytes.size == 8) {
                            val magic = ByteBuffer.wrap(bytes.take(4).toByteArray()).int
                            if (magic != 0x4C414B45) return null
                            val length = ByteBuffer.wrap(bytes.drop(4).take(4).toByteArray()).int
                            if (length <= 0 || length > 5_000_000) return null
                        }

                        if (bytes.size > 8) {
                            val length = ByteBuffer.wrap(bytes.drop(4).take(4).toByteArray()).int
                            if (bytes.size >= 8 + length) {
                                return bytes.drop(8).take(length).toByteArray()
                            }
                        }
                    }
                }
            }
        }
        return null
    }
}
