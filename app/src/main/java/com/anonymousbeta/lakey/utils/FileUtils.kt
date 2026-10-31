package com.anonymousbeta.lakey.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    fun uriToTempFile(context: Context, uri: Uri, suffix: String): File {
        val temp = File(context.cacheDir, "temp_${System.currentTimeMillis()}$suffix")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(temp).use { output ->
                input.copyTo(output)
            }
        }
        return temp
    }
}
