package com.anonymousbeta.lakey.stego

import android.content.Context
import android.net.Uri
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ApkStego(private val context: Context) {

    fun embed(context: Context, apkUri: Uri, payload: ByteArray): File {
        val inputStream = context.contentResolver.openInputStream(apkUri)
            ?: throw IllegalArgumentException("Cannot open APK")

        val outFile = File(context.cacheDir, "lakey_${System.currentTimeMillis()}.apk")
        val zis = ZipInputStream(BufferedInputStream(inputStream))
        val zos = ZipOutputStream(BufferedOutputStream(FileOutputStream(outFile)))

        val buffer = ByteArray(8192)
        var entry: ZipEntry?

        // Copy all original entries
        while (zis.nextEntry.also { entry = it } != null) {
            val current = entry!!
            zos.putNextEntry(ZipEntry(current.name))
            var len: Int
            while (zis.read(buffer).also { len = it } > 0) {
                zos.write(buffer, 0, len)
            }
            zos.closeEntry()
        }

        // Inject payload as a hidden asset
        val payloadEntry = ZipEntry("assets/lakey_payload.bin")
        zos.putNextEntry(payloadEntry)
        zos.write(payload)
        zos.closeEntry()

        // Inject a tiny bootstrap classes.dex marker (optional secondary dex name)
        val markerEntry = ZipEntry("assets/lakey_marker")
        zos.putNextEntry(markerEntry)
        zos.write("LAKEY_V1".toByteArray())
        zos.closeEntry()

        zos.finish()
        zos.close()
        zis.close()
        inputStream.close()

        return outFile
    }
}
