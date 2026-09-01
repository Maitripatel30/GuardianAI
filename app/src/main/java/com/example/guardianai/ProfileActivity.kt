package com.example.guardianai

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var tvProfileName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvContactsCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        // =====================================
        // FIREBASE
        // =====================================

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // =====================================
        // FIND VIEWS
        // =====================================

        val tvBack =
            findViewById<TextView>(R.id.tvBack)

        tvProfileName =
            findViewById(R.id.tvProfileName)

        tvEmail =
            findViewById(R.id.tvEmail)

        tvContactsCount =
            findViewById(R.id.tvContactsCount)

        val btnEditProfile =
            findViewById<MaterialButton>(R.id.btnEditProfile)

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

        val email = currentUser.email

        tvEmail.text =
            email ?: "No email available"

        // =====================================
        // LOAD PROFILE
        // =====================================

        loadProfile(currentUser.uid)

        // =====================================
        // LOAD CONTACT COUNT
        // =====================================

        loadContactCount(
            currentUser.uid
        )

        // =====================================
        // BACK
        // =====================================

        tvBack.setOnClickListener {
            finish()
        }

        // =====================================
        // EDIT PROFILE
        // =====================================

        btnEditProfile.setOnClickListener {

            showEditProfileDialog(
                currentUser.uid
            )
        }

        // =====================================
        // LOGOUT
        // =====================================

        btnLogout.setOnClickListener {

            showLogoutConfirmation()
        }
    }


    // =====================================================
    // LOAD PROFILE
    // =====================================================

    private fun loadProfile(userId: String) {

        firestore
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val name =
                        document.getString("name")

                    if (!name.isNullOrEmpty()) {

                        tvProfileName.text = name

                    } else {

                        loadNameFromEmail()
                    }

                } else {

                    loadNameFromEmail()
                }
            }
            .addOnFailureListener {

                loadNameFromEmail()
            }
    }


    // =====================================================
    // LOAD NAME FROM EMAIL
    // =====================================================

    private fun loadNameFromEmail() {

        val email =
            auth.currentUser?.email

        if (!email.isNullOrEmpty()) {

            val name =
                email.substringBefore("@")

            tvProfileName.text =
                name.replaceFirstChar {
                    it.uppercase()
                }

        } else {

            tvProfileName.text =
                "Guardian User"
        }
    }


    // =====================================================
    // EDIT PROFILE DIALOG
    // =====================================================

    private fun showEditProfileDialog(
        userId: String
    ) {

        val editText =
            EditText(this)

        editText.hint =
            "Enter your name"

        editText.setPadding(
            40,
            20,
            40,
            20
        )

        editText.setText(
            tvProfileName.text.toString()
        )

        AlertDialog.Builder(this)

            .setTitle("✏️ Edit Profile")

            .setMessage(
                "Update your profile name"
            )

            .setView(editText)

            .setPositiveButton(
                "SAVE"
            ) { _, _ ->

                val newName =
                    editText.text
                        .toString()
                        .trim()

                if (newName.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Please enter your name",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                saveProfile(
                    userId,
                    newName
                )
            }

            .setNegativeButton(
                "CANCEL",
                null
            )

            .show()
    }


    // =====================================================
    // SAVE PROFILE
    // =====================================================

    private fun saveProfile(
        userId: String,
        name: String
    ) {

        val profileData =
            hashMapOf(
                "name" to name,
                "email" to (
                        auth.currentUser?.email
                            ?: ""
                        )
            )

        firestore
            .collection("users")
            .document(userId)
            .set(
                profileData,
                com.google.firebase.firestore.SetOptions.merge()
            )
            .addOnSuccessListener {

                tvProfileName.text = name

                Toast.makeText(
                    this,
                    "Profile updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to update profile",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    // =====================================================
    // LOAD CONTACT COUNT
    // =====================================================

    private fun loadContactCount(
        userId: String
    ) {

        firestore
            .collection("users")
            .document(userId)
            .collection("emergencyContacts")
            .get()
            .addOnSuccessListener { documents ->

                tvContactsCount.text =
                    documents.size().toString()
            }
            .addOnFailureListener {

                tvContactsCount.text = "0"
            }
    }


    // =====================================================
    // LOGOUT CONFIRMATION
    // =====================================================

    private fun showLogoutConfirmation() {

        AlertDialog.Builder(this)

            .setTitle("🚪 Logout")

            .setMessage(
                "Are you sure you want to logout?"
            )

            .setPositiveButton(
                "LOGOUT"
            ) { _, _ ->

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

            .setNegativeButton(
                "CANCEL",
                null
            )

            .show()
    }
}