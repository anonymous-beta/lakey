package com.anonymousbeta.lakey.stego

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.common.PDStream
import java.io.File
import java.io.FileOutputStream

class PdfStego {

    fun embed(context: Context, pdfUri: Uri, payload: ByteArray): File {
        PDFBoxResourceLoader.init(context)

        val inputStream = context.contentResolver.openInputStream(pdfUri)
            ?: throw IllegalArgumentException("Cannot open PDF")
        val document = PDDocument.load(inputStream)
        inputStream.close()

        // Create a hidden stream with the payload
        val stream = PDStream(document, payload.inputStream())
        stream.addCompression()

        // Attach as an unreferenced object (most readers ignore it)
        // We also put a tiny marker in the document info so the payload side can find it
        document.documentInformation.setCustomMetadataValue("Lakey", "1")
        document.documentCatalog.metadata = null // keep it quiet

        // Force the stream into the document body
        document.addPage(PDPage()) // dummy page forces write of all objects
        document.removePage(document.numberOfPages - 1)

        val outFile = File(context.cacheDir, "lakey_${System.currentTimeMillis()}.pdf")
        document.save(outFile)
        document.close()
        return outFile
    }
}
