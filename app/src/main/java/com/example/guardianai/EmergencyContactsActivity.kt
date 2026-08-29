package com.example.guardianai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class EmergencyContactsActivity : AppCompatActivity() {

    private lateinit var btnAddContact: MaterialButton
    private lateinit var cardEmpty: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_emergency_contacts)

        // Find views
        btnAddContact = findViewById(R.id.btnAddContact)
        cardEmpty = findViewById(R.id.cardEmpty)

        val tvBack = findViewById<TextView>(R.id.tvBack)

        // Back button
        tvBack.setOnClickListener {
            finish()
        }

        // Add contact button
        btnAddContact.setOnClickListener {
            showAddContactDialog()
        }
    }

    private fun showAddContactDialog() {

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_contact, null)

        val etName = dialogView.findViewById<EditText>(
            R.id.etContactName
        )

        val etPhone = dialogView.findViewById<EditText>(
            R.id.etContactPhone
        )

        val dialog = AlertDialog.Builder(this)
            .setTitle("👥 Add Emergency Contact")
            .setView(dialogView)
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("SAVE", null)
            .create()

        dialog.setOnShowListener {

            val saveButton = dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            )

            saveButton.setOnClickListener {

                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()

                // Name validation
                if (name.isEmpty()) {
                    etName.error = "Enter contact name"
                    etName.requestFocus()
                    return@setOnClickListener
                }

                // Phone validation
                if (phone.isEmpty()) {
                    etPhone.error = "Enter phone number"
                    etPhone.requestFocus()
                    return@setOnClickListener
                }

                if (phone.length < 10) {
                    etPhone.error = "Enter a valid phone number"
                    etPhone.requestFocus()
                    return@setOnClickListener
                }

                // Hide empty card
                cardEmpty.visibility = View.GONE

                Toast.makeText(
                    this,
                    "$name added as emergency contact",
                    Toast.LENGTH_SHORT
                ).show()

                dialog.dismiss()
            }
        }

        dialog.show()
    }
}