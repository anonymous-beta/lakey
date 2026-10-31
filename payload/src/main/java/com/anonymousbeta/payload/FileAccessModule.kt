package com.anonymousbeta.payload

import android.content.Context
import android.os.Environment
import java.io.File

object FileAccessModule {

    fun listFiles(context: Context, path: String = ""): List<String> {
        val base = if (path.isEmpty()) {
            Environment.getExternalStorageDirectory()
        } else {
            File(path)
        }
        return base.listFiles()?.map { it.absolutePath } ?: emptyList()
    }

    fun readFile(path: String): ByteArray? {
        return try {
            File(path).readBytes()
        } catch (_: Exception) {
            null
        }
    }
}
