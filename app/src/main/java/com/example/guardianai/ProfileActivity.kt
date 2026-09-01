package com.example.guardianai

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        // Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Find views
        val tvBack =
            findViewById<TextView>(R.id.tvBack)

        val tvEmail =
            findViewById<TextView>(R.id.tvEmail)

        val btnLogout =
            findViewById<MaterialButton>(R.id.btnLogout)

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
        // SHOW EMAIL
        // =====================================

        tvEmail.text =
            currentUser.email ?: "No email available"

        // =====================================
        // BACK BUTTON
        // =====================================

        tvBack.setOnClickListener {

            finish()
        }

        // =====================================
        // LOGOUT
        // =====================================

        btnLogout.setOnClickListener {

            showLogoutConfirmation()
        }
    }

    // =====================================
    // LOGOUT CONFIRMATION
    // =====================================

    private fun showLogoutConfirmation() {

        AlertDialog.Builder(this)

            .setTitle("🚪 Logout")

            .setMessage(
                "Are you sure you want to logout?"
            )

            .setPositiveButton(
                "LOGOUT"
            ) { _, _ ->

                logout()
            }

            .setNegativeButton(
                "CANCEL",
                null
            )

            .show()
    }

    // =====================================
    // LOGOUT
    // =====================================

    private fun logout() {

        auth.signOut()

        Toast.makeText(
            this,
            "Logged out successfully",
            Toast.LENGTH_SHORT
        ).show()

        val intent =
            Intent(
                this,
                LoginActivity::class.java
            )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        finish()
    }
}