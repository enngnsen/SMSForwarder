package com.example.otpforwarder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etTargetPhone: EditText
    private lateinit var btnSave: Button
    private lateinit var tvStatus: TextView
    private lateinit var prefs: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = PrefsManager(this)

        etTargetPhone = findViewById(R.id.etTargetPhone)
        btnSave = findViewById(R.id.btnSave)
        tvStatus = findViewById(R.id.tvStatus)

        // Load saved target phone
        val savedPhone = prefs.getTargetPhone()
        if (!savedPhone.isNullOrEmpty()) {
            etTargetPhone.setText(savedPhone)
        }

        // Check permissions
        if (!hasPermissions()) {
            requestPermissions()
        }

        // Update status
        updateStatus()

        // Save button click
        btnSave.setOnClickListener {
            val phone = etTargetPhone.text.toString().trim()
            if (phone.isNotEmpty() && phone.length >= 4 && phone.length <= 8) {
                prefs.setTargetPhone(phone)
                Toast.makeText(this, "Hedef numara kaydedildi", Toast.LENGTH_SHORT).show()
                updateStatus()
            } else {
                Toast.makeText(this, "Geçerli bir numara girin (4-8 rakam)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS),
            1001
        )
    }

    private fun updateStatus() {
        val target = prefs.getTargetPhone()
        if (!target.isNullOrEmpty()) {
            tvStatus.text = "Hedef: $target - Aktif"
        } else {
            tvStatus.text = "Hedef numara belirtilmemiş"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "İzinler verildi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "İzinler gerekli", Toast.LENGTH_SHORT).show()
            }
        }
    }
}