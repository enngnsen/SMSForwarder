package com.example.otpforwarder

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PrefsManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "otp_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getTargetPhone(): String? {
        return prefs.getString("target_phone", null)
    }

    fun setTargetPhone(phone: String) {
        prefs.edit().putString("target_phone", phone).apply()
    }

    fun isForwardingEnabled(): Boolean {
        return prefs.getBoolean("forwarding_enabled", false)
    }

    fun setForwardingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("forwarding_enabled", enabled).apply()
    }
}