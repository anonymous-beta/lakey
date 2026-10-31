package com.anonymousbeta.lakey.stego

import android.content.Context
import android.net.Uri
import com.anonymousbeta.lakey.payload.PayloadConfig
import com.anonymousbeta.lakey.payload.PayloadGenerator
import java.io.File

enum class CarrierType {
    IMAGE, PDF, APK
}

class StegoEngine(private val context: Context) {

    private val payloadGenerator = PayloadGenerator(context)
    private val imageStego = ImageStego()
    private val pdfStego = PdfStego()
    private val apkStego = ApkStego(context)

    suspend fun embed(
        carrierUri: Uri,
        type: CarrierType,
        config: PayloadConfig
    ): File {
        val payloadBytes = payloadGenerator.generate(config)

        return when (type) {
            CarrierType.IMAGE -> imageStego.embed(context, carrierUri, payloadBytes)
            CarrierType.PDF   -> pdfStego.embed(context, carrierUri, payloadBytes)
            CarrierType.APK   -> apkStego.embed(context, carrierUri, payloadBytes)
        }
    }

    fun detectType(uri: Uri): CarrierType? {
        val mime = context.contentResolver.getType(uri) ?: return null
        return when {
            mime.startsWith("image/") -> CarrierType.IMAGE
            mime == "application/pdf" -> CarrierType.PDF
            mime == "application/vnd.android.package-archive" || 
            uri.toString().endsWith(".apk", true) -> CarrierType.APK
            else -> null
        }
    }
}
