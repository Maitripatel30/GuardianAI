package com.example.guardianai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val CALL_PERMISSION_CODE = 101
    private val LOCATION_PERMISSION_CODE = 102
    private val SMS_PERMISSION_CODE = 103

    private var phoneToCall: String = ""
    private val NOTIFICATION_CHANNEL_ID = "guardian_ai_emergency"
    private val NOTIFICATION_ID = 1001
    private val NOTIFICATION_PERMISSION_CODE = 103

    // Pending contacts for SMS
    private var pendingNames: ArrayList<String> = arrayListOf()
    private var pendingPhones: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // =====================================
        // FIREBASE
        // =====================================

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // =====================================
        // LOCATION
        // =====================================

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        createNotificationChannel()

        // =====================================
        // FIND VIEWS
        // =====================================

        val tvUserName =
            findViewById<TextView>(R.id.tvUserName)

        val cardSOS =
            findViewById<MaterialCardView>(R.id.cardSOS)

        val cardLocation =
            findViewById<MaterialCardView>(R.id.cardLocation)

        val navContacts =
            findViewById<TextView>(R.id.tvContacts)

        // =====================================
        // CHECK LOGIN
        // =====================================

        val currentUser = auth.currentUser

        if (currentUser == null) {

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()
            return
        }

        // =====================================
        // SHOW USER EMAIL
        // =====================================

        val email = currentUser.email

        if (!email.isNullOrEmpty()) {
            tvUserName.text = email
        } else {
            tvUserName.text = "Welcome!"
        }

        // =====================================
        // SOS BUTTON
        // =====================================

        cardSOS.setOnClickListener {
            showSOSConfirmation()
        }

        // =====================================
        // LOCATION BUTTON
        // =====================================

        cardLocation.setOnClickListener {
            getCurrentLocation()
        }

        // =====================================
        // CONTACTS
        // =====================================

        navContacts.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    EmergencyContactsActivity::class.java
                )
            )
        }
    }

    // =====================================================
    // LOCATION
    // =====================================================

    // =====================================================
// CREATE NOTIFICATION CHANNEL
// =====================================================

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Guardian AI Emergency",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description =
                "Emergency notifications from Guardian AI"

            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }


// =====================================================
// SHOW EMERGENCY NOTIFICATION
// =====================================================

    private fun showEmergencyNotification() {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_CODE
            )

            return
        }

        val intent = Intent(
            this,
            MainActivity::class.java
        )

        val pendingIntent =
            android.app.PendingIntent.getActivity(
                this,
                0,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                        android.app.PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                this,
                NOTIFICATION_CHANNEL_ID
            )
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("🚨 Guardian AI Emergency")
                .setContentText("SOS has been activated!")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "SOS has been activated. " +
                                    "Emergency contacts have been notified."
                        )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

        NotificationManagerCompat
            .from(this)
            .notify(
                NOTIFICATION_ID,
                notification
            )
    }
    private fun getCurrentLocation() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_CODE
            )

            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {

                    val latitude = location.latitude
                    val longitude = location.longitude

                    showLocationDialog(
                        latitude,
                        longitude
                    )

                } else {

                    Toast.makeText(
                        this,
                        "Unable to get current location",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Location error",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =====================================================
    // LOCATION DIALOG
    // =====================================================

    private fun showLocationDialog(
        latitude: Double,
        longitude: Double
    ) {

        val message =
            "Your current location:\n\n" +
                    "Latitude: $latitude\n" +
                    "Longitude: $longitude"

        AlertDialog.Builder(this)
            .setTitle("📍 Current Location")
            .setMessage(message)
            .setPositiveButton("OPEN MAP") { _, _ ->

                openGoogleMaps(
                    latitude,
                    longitude
                )
            }
            .setNegativeButton("CLOSE", null)
            .show()
    }

    // =====================================================
    // OPEN GOOGLE MAPS
    // =====================================================

    private fun openGoogleMaps(
        latitude: Double,
        longitude: Double
    ) {

        val uri =
            Uri.parse(
                "geo:$latitude,$longitude?q=$latitude,$longitude"
            )

        val mapIntent =
            Intent(
                Intent.ACTION_VIEW,
                uri
            )

        mapIntent.setPackage(
            "com.google.android.apps.maps"
        )

        try {

            startActivity(mapIntent)

        } catch (e: Exception) {

            val browserUri =
                Uri.parse(
                    "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
                )

            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    browserUri
                )
            )
        }
    }

    // =====================================================
    // SOS CONFIRMATION
    // =====================================================

    private fun showSOSConfirmation() {

        AlertDialog.Builder(this)

            .setTitle("🚨 Emergency SOS")

            .setMessage(
                "Are you sure you want to activate SOS?"
            )

            .setPositiveButton(
                "ACTIVATE SOS"
            ) { dialog, _ ->

                activateSOS()

                dialog.dismiss()
            }

            .setNegativeButton(
                "CANCEL",
                null
            )

            .show()
    }

    // =====================================================
    // ACTIVATE SOS
    // =====================================================

    private fun activateSOS() {
        showEmergencyNotification()

        val currentUser = auth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please login first",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val userId = currentUser.uid

        firestore
            .collection("users")
            .document(userId)
            .collection("emergencyContacts")
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty()) {

                    AlertDialog.Builder(this)
                        .setTitle("🚨 SOS Activated")
                        .setMessage(
                            "No emergency contacts are saved."
                        )
                        .setPositiveButton("OK", null)
                        .show()

                    return@addOnSuccessListener
                }

                val uniqueContacts =
                    LinkedHashMap<String, String>()

                for (document in documents) {

                    val name =
                        document.getString("name")
                            ?: "Unknown"

                    val phone =
                        document.getString("phone")
                            ?: ""

                    if (phone.isNotEmpty()) {
                        uniqueContacts[phone] = name
                    }
                }

                if (uniqueContacts.isEmpty()) {

                    Toast.makeText(
                        this,
                        "No valid phone numbers found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val names =
                    uniqueContacts.values.toList()

                val phones =
                    uniqueContacts.keys.toList()

                val message =
                    StringBuilder()

                message.append(
                    "🚨 SOS is activated!\n\n"
                )

                message.append(
                    "Emergency Contacts:\n\n"
                )

                for (i in names.indices) {

                    message.append(
                        "👤 ${names[i]}\n"
                    )

                    message.append(
                        "📞 ${phones[i]}\n\n"
                    )
                }

                // =====================================
                // SEND AUTOMATIC SMS
                // =====================================

                sendEmergencySMS(
                    names,
                    phones
                )

                // =====================================
                // SHOW SOS DIALOG
                // =====================================

                if (names.size == 1) {

                    phoneToCall = phones[0]

                    AlertDialog.Builder(this)
                        .setTitle("🚨 SOS Activated")
                        .setMessage(message.toString())
                        .setPositiveButton("📞 CALL") { _, _ ->
                            makePhoneCall(phoneToCall)
                        }
                        .setNegativeButton(
                            "CANCEL",
                            null
                        )
                        .show()

                } else {

                    showMultipleContacts(
                        names,
                        phones,
                        message.toString()
                    )
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load emergency contacts",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =====================================================
    // SEND EMERGENCY SMS
    // =====================================================

    private fun sendEmergencySMS(
        names: List<String>,
        phones: List<String>
    ) {

        // Save contacts in case permission is needed
        pendingNames =
            ArrayList(names)

        pendingPhones =
            ArrayList(phones)

        // =====================================
        // CHECK SMS PERMISSION
        // =====================================

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.SEND_SMS
                ),
                SMS_PERMISSION_CODE
            )

            return
        }

        // =====================================
        // CHECK LOCATION PERMISSION
        // =====================================

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_CODE
            )

            return
        }

        getLocationAndSendSMS(
            names,
            phones
        )
    }

    // =====================================================
    // GET LOCATION AND SEND SMS
    // =====================================================

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getLocationAndSendSMS(
        names: List<String>,
        phones: List<String>
    ) {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {

                    val latitude =
                        location.latitude

                    val longitude =
                        location.longitude

                    val locationLink =
                        "https://maps.google.com/?q=$latitude,$longitude"

                    val smsMessage =
                        "🚨 GUARDIAN AI EMERGENCY!\n\n" +
                                "SOS has been activated.\n\n" +
                                "I may need help. " +
                                "Please check my current location.\n\n" +
                                "📍 My Location:\n" +
                                locationLink

                    sendSMSToContacts(
                        phones,
                        smsMessage
                    )

                } else {

                    // Location unavailable
                    val smsMessage =
                        "🚨 GUARDIAN AI EMERGENCY!\n\n" +
                                "SOS has been activated.\n\n" +
                                "I may need help. " +
                                "Please contact me immediately."

                    sendSMSToContacts(
                        phones,
                        smsMessage
                    )
                }
            }
            .addOnFailureListener {

                val smsMessage =
                    "🚨 GUARDIAN AI EMERGENCY!\n\n" +
                            "SOS has been activated.\n\n" +
                            "I may need help. " +
                            "Please contact me immediately."

                sendSMSToContacts(
                    phones,
                    smsMessage
                )
            }
    }

    // =====================================================
    // SEND SMS TO ALL CONTACTS
    // =====================================================

    private fun sendSMSToContacts(
        phones: List<String>,
        message: String
    ) {

        try {

            val smsManager =
                SmsManager.getDefault()

            for (phone in phones) {

                smsManager.sendTextMessage(
                    phone,
                    null,
                    message,
                    null,
                    null
                )
            }

            Toast.makeText(
                this,
                "🚨 Emergency SMS sent successfully!",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Unable to send SMS: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // =====================================================
    // MULTIPLE CONTACTS
    // =====================================================

    private fun showMultipleContacts(
        names: List<String>,
        phones: List<String>,
        message: String
    ) {

        val items =
            Array(names.size) { index ->
                "${names[index]} - ${phones[index]}"
            }

        AlertDialog.Builder(this)
            .setTitle("🚨 SOS Activated")
            .setMessage(message)
            .setItems(items) { _, which ->

                phoneToCall = phones[which]

                showCallConfirmation(
                    names[which],
                    phones[which]
                )
            }
            .setNegativeButton(
                "CLOSE",
                null
            )
            .show()
    }

    // =====================================================
    // CALL CONFIRMATION
    // =====================================================

    private fun showCallConfirmation(
        name: String,
        phone: String
    ) {

        AlertDialog.Builder(this)
            .setTitle("📞 Call Emergency Contact")
            .setMessage(
                "Call $name?\n\n$phone"
            )
            .setPositiveButton(
                "CALL"
            ) { _, _ ->

                makePhoneCall(phone)
            }
            .setNegativeButton(
                "CANCEL",
                null
            )
            .show()
    }

    // =====================================================
    // MAKE PHONE CALL
    // =====================================================

    private fun makePhoneCall(phone: String) {

        phoneToCall = phone

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CALL_PHONE
                ),
                CALL_PERMISSION_CODE
            )

            return
        }

        val intent = Intent(
            Intent.ACTION_CALL,
            Uri.parse("tel:$phone")
        )

        startActivity(intent)
    }

    // =====================================================
    // PERMISSION RESULT
    // =====================================================

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                Toast.makeText(
                    this,
                    "Notification permission granted",
                    Toast.LENGTH_SHORT
                ).show()

                showEmergencyNotification()

            } else {

                Toast.makeText(
                    this,
                    "Notification permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        // =====================================
        // LOCATION
        // =====================================

        if (requestCode == LOCATION_PERMISSION_CODE) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {

                Toast.makeText(
                    this,
                    "Location permission granted",
                    Toast.LENGTH_SHORT
                ).show()

                // If contacts are pending, send SMS
                if (pendingPhones.isNotEmpty()) {

                    if (
                        ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.SEND_SMS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        getLocationAndSendSMS(
                            pendingNames,
                            pendingPhones
                        )

                    } else {

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.SEND_SMS
                            ),
                            SMS_PERMISSION_CODE
                        )
                    }

                } else {

                    getCurrentLocation()
                }

            } else {

                Toast.makeText(
                    this,
                    "Location permission is required",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // =====================================
        // SMS
        // =====================================

        if (requestCode == SMS_PERMISSION_CODE) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {

                Toast.makeText(
                    this,
                    "SMS permission granted",
                    Toast.LENGTH_SHORT
                ).show()

                if (pendingPhones.isNotEmpty()) {

                    getLocationAndSendSMS(
                        pendingNames,
                        pendingPhones
                    )
                }

            } else {

                Toast.makeText(
                    this,
                    "SMS permission is required for emergency SMS",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // =====================================
        // CALL
        // =====================================

        if (requestCode == CALL_PERMISSION_CODE) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {

                makePhoneCall(phoneToCall)

            } else {

                Toast.makeText(
                    this,
                    "Call permission is required",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}