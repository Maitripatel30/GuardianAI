package com.example.guardianai

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmergencyContactsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var btnAddContact: MaterialButton
    private lateinit var cardEmpty: MaterialCardView
    private lateinit var contactsContainer: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_emergency_contacts)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        btnAddContact = findViewById(R.id.btnAddContact)
        cardEmpty = findViewById(R.id.cardEmpty)
        contactsContainer = findViewById(R.id.contactsContainer)

        val tvBack = findViewById<TextView>(R.id.tvBack)

        tvBack.setOnClickListener {
            finish()
        }

        btnAddContact.setOnClickListener {
            showAddContactDialog()
        }

        loadContactsFromFirebase()
    }
    private fun showAddContactDialog() {

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_contact, null)

        val etName =
            dialogView.findViewById<EditText>(R.id.etContactName)

        val etPhone =
            dialogView.findViewById<EditText>(R.id.etContactPhone)

        val dialog = AlertDialog.Builder(this)
            .setTitle("👥 Add Emergency Contact")
            .setView(dialogView)
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("SAVE", null)
            .create()

        dialog.setOnShowListener {

            val saveButton =
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            saveButton.setOnClickListener {

                val name = etName.text
                    .toString()
                    .trim()

                val phone = etPhone.text
                    .toString()
                    .trim()

                if (name.isEmpty()) {

                    etName.error = "Enter contact name"
                    etName.requestFocus()

                    return@setOnClickListener
                }

                if (phone.isEmpty()) {

                    etPhone.error = "Enter phone number"
                    etPhone.requestFocus()

                    return@setOnClickListener
                }

                if (phone.length < 10) {

                    etPhone.error =
                        "Enter a valid phone number"

                    etPhone.requestFocus()

                    return@setOnClickListener
                }

                saveContactToFirebase(
                    name,
                    phone,
                    dialog
                )
            }
        }

        dialog.show()
    }
    private fun saveContactToFirebase(
        name: String,
        phone: String,
        dialog: AlertDialog
    ) {

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

        // First check existing contacts
        firestore
            .collection("users")
            .document(userId)
            .collection("emergencyContacts")
            .get()
            .addOnSuccessListener { documents ->
                val duplicateExists = documents.any { document ->

                    val existingName =
                        document.getString("name") ?: ""

                    val existingPhone =
                        document.getString("phone") ?: ""

                    existingName.equals(
                        name,
                        ignoreCase = true
                    ) && existingPhone == phone
                }

                if (duplicateExists) {

                    Toast.makeText(
                        this,
                        "This contact is already saved!",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val contactData = hashMapOf(
                    "name" to name,
                    "phone" to phone
                )

                firestore
                    .collection("users")
                    .document(userId)
                    .collection("emergencyContacts")
                    .add(contactData)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Contact added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        dialog.dismiss()

                        loadContactsFromFirebase()
                    }
                    .addOnFailureListener { exception ->

                        Toast.makeText(
                            this,
                            "Failed to save contact: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Unable to check contacts: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
    private fun loadContactsFromFirebase() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            return
        }

        val userId = currentUser.uid

        firestore
            .collection("users")
            .document(userId)
            .collection("emergencyContacts")
            .get()
            .addOnSuccessListener { documents ->

                contactsContainer.removeAllViews()

                val displayedContacts =
                    mutableSetOf<String>()

                for (document in documents) {

                    val name =
                        document.getString("name") ?: ""

                    val phone =
                        document.getString("phone") ?: ""

                    val contactKey =
                        "$name|$phone"

                    if (displayedContacts.add(contactKey)) {

                        showContact(
                            name,
                            phone,
                            document.id
                        )
                    }
                }

                if (displayedContacts.isEmpty()) {

                    cardEmpty.visibility =
                        View.VISIBLE

                    contactsContainer.visibility =
                        View.GONE

                } else {

                    cardEmpty.visibility =
                        View.GONE

                    contactsContainer.visibility =
                        View.VISIBLE
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load contacts",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
    private fun showContact(
        name: String,
        phone: String,
        documentId: String
    ) {

        val contactCard =
            MaterialCardView(this)

        val cardParams =
            ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                dpToPx(90)
            )

        cardParams.setMargins(
            0,
            dpToPx(10),
            0,
            0
        )

        contactCard.layoutParams =
            cardParams

        contactCard.radius =
            dpToPx(18).toFloat()

        contactCard.cardElevation =
            dpToPx(4).toFloat()

        contactCard.setCardBackgroundColor(
            Color.WHITE
        )

        contactCard.strokeWidth =
            dpToPx(1)

        contactCard.strokeColor =
            Color.rgb(220, 222, 240)

        val mainLayout =
            LinearLayout(this)

        mainLayout.orientation =
            LinearLayout.HORIZONTAL

        mainLayout.gravity =
            Gravity.CENTER_VERTICAL

        mainLayout.setPadding(
            dpToPx(15),
            dpToPx(8),
            dpToPx(10),
            dpToPx(8)
        )

        mainLayout.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

        val icon = TextView(this)

        icon.text = "👤"
        icon.textSize = 25f
        icon.gravity = Gravity.CENTER

        val iconParams =
            LinearLayout.LayoutParams(
                dpToPx(50),
                dpToPx(50)
            )

        iconParams.setMargins(
            0,
            0,
            dpToPx(12),
            0
        )

        mainLayout.addView(
            icon,
            iconParams
        )

        val textLayout =
            LinearLayout(this)

        textLayout.orientation =
            LinearLayout.VERTICAL

        textLayout.gravity =
            Gravity.CENTER_VERTICAL

        val textParams =
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )

        val nameText = TextView(this)

        nameText.text = name
        nameText.textSize = 17f

        nameText.setTypeface(
            null,
            Typeface.BOLD
        )

        nameText.setTextColor(
            Color.rgb(24, 43, 91)
        )

        val phoneText = TextView(this)

        phoneText.text = phone
        phoneText.textSize = 14f

        phoneText.setTextColor(
            Color.rgb(110, 118, 135)
        )

        phoneText.setPadding(
            0,
            dpToPx(3),
            0,
            0
        )

        textLayout.addView(nameText)
        textLayout.addView(phoneText)

        mainLayout.addView(
            textLayout,
            textParams
        )

        val deleteButton = TextView(this)

        deleteButton.text = "✕"
        deleteButton.textSize = 20f
        deleteButton.gravity = Gravity.CENTER

        deleteButton.setTextColor(
            Color.rgb(190, 60, 60)
        )

        deleteButton.setPadding(
            dpToPx(10),
            0,
            dpToPx(5),
            0
        )

        deleteButton.setOnClickListener {

            deleteContact(
                documentId,
                name,
                contactCard
            )
        }

        mainLayout.addView(
            deleteButton
        )

        contactCard.addView(
            mainLayout
        )

        contactsContainer.addView(
            contactCard
        )
    }
    private fun deleteContact(
        documentId: String,
        name: String,
        contactCard: MaterialCardView
    ) {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            return
        }

        val userId = currentUser.uid

        firestore
            .collection("users")
            .document(userId)
            .collection("emergencyContacts")
            .document(documentId)
            .delete()
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "$name removed",
                    Toast.LENGTH_SHORT
                ).show()

                loadContactsFromFirebase()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to remove contact",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
    private fun dpToPx(dp: Int): Int {

        return (
                dp *
                        resources.displayMetrics.density
                ).toInt()
    }
}