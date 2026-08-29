package com.example.guardianai

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Find views
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val cardSOS = findViewById<MaterialCardView>(R.id.cardSOS)
        val cardLocation = findViewById<MaterialCardView>(R.id.cardLocation)

        // Get currently logged-in user
        val currentUser = auth.currentUser

        // Check login status
        if (currentUser == null) {

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)
            finish()

            return
        }

        // Get user's email
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
    }

    // =====================================
    // SOS CONFIRMATION
    // =====================================

    private fun showSOSConfirmation() {

        val builder = AlertDialog.Builder(this)

        builder.setTitle("🚨 Emergency SOS")

        builder.setMessage(
            "Are you sure you want to activate SOS?\n\n" +
                    "Guardian AI will prepare to notify your emergency contacts."
        )

        builder.setPositiveButton("ACTIVATE SOS") { dialog, _ ->

            activateSOS()

            dialog.dismiss()
        }

        builder.setNegativeButton("CANCEL") { dialog, _ ->

            dialog.dismiss()
        }

        builder.setCancelable(true)

        val dialog = builder.create()

        dialog.show()
    }

    // =====================================
    // ACTIVATE SOS
    // =====================================

    private fun activateSOS() {

        Toast.makeText(
            this,
            "🚨 SOS ACTIVATED!",
            Toast.LENGTH_LONG
        ).show()
    }

    // =====================================
    // LOGOUT
    // =====================================

    private fun logout() {

        auth.signOut()

        val intent = Intent(
            this,
            LoginActivity::class.java
        )

        startActivity(intent)

        finish()
    }
}