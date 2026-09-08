package com.example.guardianai

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SafetyCheckActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnStart: MaterialButton
    private lateinit var btnSafe: MaterialButton
    private lateinit var btnCancel: MaterialButton

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var countDownTimer: CountDownTimer? = null
    private var timerRunning = false

    companion object {
        private const val SMS_PERMISSION_CODE = 201
        private const val LOCATION_PERMISSION_CODE = 202
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_safety_check)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        val tvBack = findViewById<TextView>(R.id.tvBack)

        tvTimer = findViewById(R.id.tvTimer)
        tvStatus = findViewById(R.id.tvStatus)
        btnStart = findViewById(R.id.btnStartTimer)
        btnSafe = findViewById(R.id.btnImSafe)
        btnCancel = findViewById(R.id.btnCancelTimer)

        tvTimer.text = "00:00"

        tvStatus.text =
            "Set a timer and let GuardianAI check your safety."

        btnSafe.isEnabled = false
        btnCancel.isEnabled = false

        tvBack.setOnClickListener {

            if (timerRunning) {

                AlertDialog.Builder(this)
                    .setTitle("⚠️ Timer Running")
                    .setMessage(
                        "A Safety Check timer is currently running. " +
                                "Are you sure you want to leave?"
                    )
                    .setPositiveButton("LEAVE") { _, _ ->
                        finish()
                    }
                    .setNegativeButton("STAY", null)
                    .show()

            } else {
                finish()
            }
        }

        btnStart.setOnClickListener {
            showTimerSelection()
        }

        btnSafe.setOnClickListener {
            markAsSafe()
        }

        btnCancel.setOnClickListener {
            cancelTimer()
        }
    }

    private fun showTimerSelection() {

        val options = arrayOf(
            "5 Minutes",
            "10 Minutes",
            "15 Minutes",
            "30 Minutes",
            "60 Minutes"
        )

        AlertDialog.Builder(this)
            .setTitle("🛡️ Set Safety Timer")
            .setItems(options) { _, which ->

                val minutes = when (which) {
                    0 -> 5
                    1 -> 10
                    2 -> 15
                    3 -> 30
                    4 -> 60
                    else -> 5
                }

                startSafetyTimer(minutes)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun startSafetyTimer(minutes: Int) {

        countDownTimer?.cancel()

        val duration = minutes * 60 * 1000L

        timerRunning = true

        btnStart.isEnabled = false
        btnSafe.isEnabled = true
        btnCancel.isEnabled = true

        tvStatus.text =
            "Safety timer is active. Reach your destination safely."

        countDownTimer =
            object : CountDownTimer(
                duration,
                1000
            ) {

                override fun onTick(millisUntilFinished: Long) {

                    val totalSeconds =
                        millisUntilFinished / 1000

                    val minutesRemaining =
                        totalSeconds / 60

                    val secondsRemaining =
                        totalSeconds % 60

                    tvTimer.text =
                        String.format(
                            "%02d:%02d",
                            minutesRemaining,
                            secondsRemaining
                        )
                }

                override fun onFinish() {

                    timerRunning = false

                    tvTimer.text = "00:00"

                    btnStart.isEnabled = true
                    btnSafe.isEnabled = false
                    btnCancel.isEnabled = false

                    tvStatus.text =
                        "⚠️ Safety check missed!"

                    // Save notification
                    saveSafetyMissedNotification()

                    // Alert emergency contacts
                    alertEmergencyContacts()

                    // Show warning dialog
                    showSafetyMissedDialog()
                }

            }.start()
    }

    // ---------------------------------------------------------
    // ALERT EMERGENCY CONTACTS
    // ---------------------------------------------------------

    private fun alertEmergencyContacts() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(
                this,
                "User not logged in",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Check SMS permission
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                SMS_PERMISSION_CODE
            )

            return
        }

        getEmergencyContactsAndSendAlert()
    }

    private fun getEmergencyContactsAndSendAlert() {

        val currentUser = auth.currentUser ?: return

        val userId = currentUser.uid

        firestore
            .collection("users")
            .document(userId)
            .collection("emergencyContacts")
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {

                    Toast.makeText(
                        this,
                        "No emergency contacts found",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                val phoneNumbers =
                    LinkedHashSet<String>()

                for (document in documents) {

                    val phone =
                        document.getString("phone")

                    if (!phone.isNullOrBlank()) {
                        phoneNumbers.add(phone)
                    }
                }

                if (phoneNumbers.isEmpty()) {

                    Toast.makeText(
                        this,
                        "No valid emergency phone numbers found",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                // Get user's current location
                getCurrentLocationAndSendSMS(
                    phoneNumbers.toList()
                )
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load emergency contacts",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun getCurrentLocationAndSendSMS(
        phoneNumbers: List<String>
    ) {

        // Check location permission
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Send SMS without location
            sendSafetyAlertSMS(
                phoneNumbers,
                null
            )

            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                sendSafetyAlertSMS(
                    phoneNumbers,
                    location
                )
            }
            .addOnFailureListener {

                sendSafetyAlertSMS(
                    phoneNumbers,
                    null
                )
            }
    }

    private fun sendSafetyAlertSMS(
        phoneNumbers: List<String>,
        location: Location?
    ) {

        val message = if (location != null) {

            val mapsLink =
                "https://maps.google.com/?q=" +
                        "${location.latitude},${location.longitude}"

            """
            ⚠️ GUARDIAN AI SAFETY ALERT
            
            The user's Safety Check timer has expired and they did not confirm that they are safe.
            
            Please check on them immediately.
            
            📍 Current Location:
            $mapsLink
            """.trimIndent()

        } else {

            """
            ⚠️ GUARDIAN AI SAFETY ALERT
            
            The user's Safety Check timer has expired and they did not confirm that they are safe.
            
            Please check on them immediately.
            
            📍 Current location is currently unavailable.
            """.trimIndent()
        }

        try {

            val smsManager =
                SmsManager.getDefault()

            var sentCount = 0

            for (phoneNumber in phoneNumbers) {

                smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    message,
                    null,
                    null
                )

                sentCount++
            }

            Toast.makeText(
                this,
                "🚨 Safety alert sent to $sentCount emergency contact(s)",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Failed to send safety alert",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ---------------------------------------------------------
    // SMS PERMISSION RESULT
    // ---------------------------------------------------------

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == SMS_PERMISSION_CODE) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {

                // Permission granted
                getEmergencyContactsAndSendAlert()

            } else {

                Toast.makeText(
                    this,
                    "SMS permission is required to alert emergency contacts",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ---------------------------------------------------------
    // MARK AS SAFE
    // ---------------------------------------------------------

    private fun markAsSafe() {

        countDownTimer?.cancel()

        timerRunning = false

        tvTimer.text = "00:00"

        tvStatus.text =
            "✅ You are marked as SAFE."

        btnStart.isEnabled = true
        btnSafe.isEnabled = false
        btnCancel.isEnabled = false

        saveSafetyCompletedNotification()

        Toast.makeText(
            this,
            "✅ Safety check completed",
            Toast.LENGTH_LONG
        ).show()
    }

    // ---------------------------------------------------------
    // CANCEL TIMER
    // ---------------------------------------------------------

    private fun cancelTimer() {

        countDownTimer?.cancel()

        timerRunning = false

        tvTimer.text = "00:00"

        tvStatus.text =
            "Safety timer cancelled."

        btnStart.isEnabled = true
        btnSafe.isEnabled = false
        btnCancel.isEnabled = false

        Toast.makeText(
            this,
            "Safety timer cancelled",
            Toast.LENGTH_SHORT
        ).show()
    }

    // ---------------------------------------------------------
    // FIRESTORE - COMPLETED
    // ---------------------------------------------------------

    private fun saveSafetyCompletedNotification() {

        val currentUser = auth.currentUser ?: return

        val userId = currentUser.uid

        val notification =
            hashMapOf(
                "type" to "SAFETY_CHECK",
                "title" to "Safety Check Completed",
                "description" to
                        "You confirmed that you are safe.",
                "time" to "Just now",
                "timestamp" to FieldValue.serverTimestamp()
            )

        firestore
            .collection("users")
            .document(userId)
            .collection("notifications")
            .add(notification)
    }

    // ---------------------------------------------------------
    // FIRESTORE - MISSED
    // ---------------------------------------------------------

    private fun saveSafetyMissedNotification() {

        val currentUser = auth.currentUser ?: return

        val userId = currentUser.uid

        val notification =
            hashMapOf(
                "type" to "SAFETY_CHECK",
                "title" to "Safety Check Missed",
                "description" to
                        "Safety timer expired. Emergency contacts were alerted.",
                "time" to "Just now",
                "timestamp" to FieldValue.serverTimestamp()
            )

        firestore
            .collection("users")
            .document(userId)
            .collection("notifications")
            .add(notification)
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to save safety notification",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // ---------------------------------------------------------
    // MISSED DIALOG
    // ---------------------------------------------------------

    private fun showSafetyMissedDialog() {

        AlertDialog.Builder(this)

            .setTitle("⚠️ Safety Check Missed")

            .setMessage(
                "You did not confirm that you are safe " +
                        "before the timer expired.\n\n" +
                        "Your emergency contacts have been alerted."
            )

            .setPositiveButton(
                "I'M SAFE NOW"
            ) { _, _ ->

                tvStatus.text =
                    "✅ You are marked as SAFE."

                Toast.makeText(
                    this,
                    "✅ Safety confirmed",
                    Toast.LENGTH_LONG
                ).show()

                saveSafetyCompletedNotification()
            }

            .setNegativeButton(
                "CLOSE",
                null
            )

            .show()
    }

    override fun onDestroy() {

        countDownTimer?.cancel()

        super.onDestroy()
    }
}