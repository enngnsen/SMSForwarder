package com.example.otpforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") return

        val prefs = PrefsManager(context)
        val targetPhone = prefs.getTargetPhone()

        // If no target phone is set, do nothing
        if (targetPhone.isNullOrEmpty()) return

        val bundle = intent.extras ?: return
        val pdus = bundle.get("pdus") as? Array<*> ?: return

        val messages = pdus.mapNotNull { pdu ->
            try {
                SmsMessage.createFromPdu(pdu as ByteArray)
            } catch (e: Exception) {
                null
            }
        }

        if (messages.isEmpty()) return

        // Combine all message parts
        val fullMessage = messages.joinToString("") { it.messageBody ?: "" }

        // Extract OTP (4-8 digit code)
        val otpCode = extractOtp(fullMessage)

        if (otpCode != null) {
            // Forward OTP message to target phone
            SmsSender.send(context, targetPhone, fullMessage)
        }
    }

    private fun extractOtp(message: String): String? {
        // Regex to find 4-8 digit OTP code
        val regex = Regex("\\b\\d{4,8}\\b")
        return regex.find(message)?.value
    }
}