package com.example.guardianai

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // =====================================
        // FIREBASE INITIALIZATION
        // =====================================

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)

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

            Toast.makeText(
                this,
                "📍 Location feature selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        // =====================================
        // CONTACTS NAVIGATION
        // =====================================

        navContacts.setOnClickListener {

            val intent = Intent(
                this,
                EmergencyContactsActivity::class.java
            )

            startActivity(intent)
        }
    }

    // =====================================================
    // SOS CONFIRMATION
    // =====================================================

    private fun showSOSConfirmation() {

        val builder =
            AlertDialog.Builder(this)

        builder.setTitle(
            "🚨 Emergency SOS"
        )

        builder.setMessage(
            "Are you sure you want to activate SOS?\n\n" +
                    "Guardian AI will check your emergency contacts."
        )

        builder.setPositiveButton(
            "ACTIVATE SOS"
        ) { dialog, _ ->

            activateSOS()

            dialog.dismiss()
        }

        builder.setNegativeButton(
            "CANCEL"
        ) { dialog, _ ->

            dialog.dismiss()
        }

        builder.setCancelable(true)

        val dialog = builder.create()

        dialog.show()
    }

    // =====================================================
    // ACTIVATE SOS
    // =====================================================

    private fun activateSOS() {

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

        // =====================================
        // GET EMERGENCY CONTACTS
        // =====================================

        firestore
            .collection("users")
            .document(userId)
            .collection("emergencyContacts")
            .get()
            .addOnSuccessListener { documents ->

                // =====================================
                // NO CONTACTS
                // =====================================

                if (documents.isEmpty()) {

                    AlertDialog.Builder(this)
                        .setTitle("🚨 SOS Activated")
                        .setMessage(
                            "SOS has been activated.\n\n" +
                                    "No emergency contacts are saved yet."
                        )
                        .setPositiveButton("OK", null)
                        .show()

                    return@addOnSuccessListener
                }

                // =====================================
                // CONTACTS FOUND
                // =====================================

                val contactsList =
                    StringBuilder()

                contactsList.append(
                    "SOS is activated.\n\n"
                )

                contactsList.append(
                    "Emergency contacts:\n\n"
                )

                for (document in documents) {

                    val name =
                        document.getString("name")
                            ?: "Unknown"

                    val phone =
                        document.getString("phone")
                            ?: "No number"

                    contactsList.append(
                        "👤 $name\n"
                    )

                    contactsList.append(
                        "📞 $phone\n\n"
                    )
                }

                // =====================================
                // SHOW CONTACTS
                // =====================================

                AlertDialog.Builder(this)
                    .setTitle(
                        "🚨 SOS Activated"
                    )
                    .setMessage(
                        contactsList.toString()
                    )
                    .setPositiveButton(
                        "OK",
                        null
                    )
                    .show()
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Unable to load emergency contacts: " +
                            exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}