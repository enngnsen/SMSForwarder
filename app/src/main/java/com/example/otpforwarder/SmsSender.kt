package com.example.otpforwarder

import android.content.Context
import android.telephony.SmsManager

object SmsSender {

    fun send(context: Context, destinationAddress: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()

            // Split message if longer than 160 characters
            val parts = smsManager.divideMessage(message)

            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(
                    destinationAddress,
                    null,
                    parts,
                    null,
                    null
                )
            } else {
                smsManager.sendTextMessage(
                    destinationAddress,
                    null,
                    message,
                    null,
                    null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}