package com.example.guardianai

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // =====================================
    // NOTIFICATION CARDS
    // =====================================

    private lateinit var cardSOS: MaterialCardView
    private lateinit var cardLocation: MaterialCardView
    private lateinit var cardCall: MaterialCardView
    private lateinit var cardSafety: MaterialCardView

    // =====================================
    // SOS VIEWS
    // =====================================

    private lateinit var tvSOSTitle: TextView
    private lateinit var tvSOSDescription: TextView
    private lateinit var tvSOSTime: TextView
    private lateinit var tvSOSIcon: TextView

    // =====================================
    // LOCATION VIEWS
    // =====================================

    private lateinit var tvLocationTitle: TextView
    private lateinit var tvLocationDescription: TextView
    private lateinit var tvLocationTime: TextView
    private lateinit var tvLocationIcon: TextView

    // =====================================
    // CALL VIEWS
    // =====================================

    private lateinit var tvCallTitle: TextView
    private lateinit var tvCallDescription: TextView
    private lateinit var tvCallTime: TextView
    private lateinit var tvCallIcon: TextView

    // =====================================
    // SAFETY CHECK VIEWS
    // =====================================

    private lateinit var tvSafetyTitle: TextView
    private lateinit var tvSafetyDescription: TextView
    private lateinit var tvSafetyTime: TextView
    private lateinit var tvSafetyIcon: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notifications)

        // =====================================
        // FIREBASE
        // =====================================

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // =====================================
        // FIND BACK BUTTON
        // =====================================

        val tvBack =
            findViewById<TextView>(R.id.tvBack)

        // =====================================
        // SOS
        // =====================================

        cardSOS =
            findViewById(R.id.cardSOSNotification)

        tvSOSTitle =
            findViewById(R.id.tvSOSTitle)

        tvSOSDescription =
            findViewById(R.id.tvSOSDescription)

        tvSOSTime =
            findViewById(R.id.tvSOSTime)

        tvSOSIcon =
            findViewById(R.id.tvSOSIcon)

        // =====================================
        // LOCATION
        // =====================================

        cardLocation =
            findViewById(R.id.cardLocationNotification)

        tvLocationTitle =
            findViewById(R.id.tvLocationTitle)

        tvLocationDescription =
            findViewById(R.id.tvLocationDescription)

        tvLocationTime =
            findViewById(R.id.tvLocationTime)

        tvLocationIcon =
            findViewById(R.id.tvLocationIcon)

        // =====================================
        // CALL
        // =====================================

        cardCall =
            findViewById(R.id.cardCallNotification)

        tvCallTitle =
            findViewById(R.id.tvCallTitle)

        tvCallDescription =
            findViewById(R.id.tvCallDescription)

        tvCallTime =
            findViewById(R.id.tvCallTime)

        tvCallIcon =
            findViewById(R.id.tvCallIcon)

        // =====================================
        // SAFETY CHECK
        // =====================================

        cardSafety =
            findViewById(R.id.cardSafetyNotification)

        tvSafetyTitle =
            findViewById(R.id.tvSafetyTitle)

        tvSafetyDescription =
            findViewById(R.id.tvSafetyDescription)

        tvSafetyTime =
            findViewById(R.id.tvSafetyTime)

        tvSafetyIcon =
            findViewById(R.id.tvSafetyIcon)

        // =====================================
        // BACK BUTTON
        // =====================================

        tvBack.setOnClickListener {
            finish()
        }

        // =====================================
        // HIDE ALL CARDS INITIALLY
        // =====================================

        cardSOS.visibility = View.GONE
        cardLocation.visibility = View.GONE
        cardCall.visibility = View.GONE
        cardSafety.visibility = View.GONE

        // =====================================
        // LOAD NOTIFICATIONS
        // =====================================

        loadNotifications()
    }


    // =====================================================
    // LOAD NOTIFICATIONS FROM FIRESTORE
    // =====================================================

    private fun loadNotifications() {

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
            .collection("notifications")
            .orderBy(
                "timestamp",
                Query.Direction.DESCENDING
            )
            .get()
            .addOnSuccessListener { documents ->

                // =====================================
                // NO NOTIFICATIONS
                // =====================================

                if (documents.isEmpty()) {

                    Toast.makeText(
                        this,
                        "No recent activity",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                // =====================================
                // LATEST NOTIFICATION OF EACH TYPE
                // =====================================

                var sosShown = false
                var locationShown = false
                var callShown = false
                var safetyShown = false

                for (document in documents) {

                    val type =
                        document.getString("type")
                            ?.uppercase()
                            ?: ""

                    val title =
                        document.getString("title")
                            ?: "Notification"

                    val description =
                        document.getString("description")
                            ?: ""

                    val time =
                        document.getString("time")
                            ?: "Recent"

                    // =====================================
                    // SOS
                    // =====================================

                    if (
                        type == "SOS" &&
                        !sosShown
                    ) {

                        showSOSNotification(
                            title,
                            description,
                            time
                        )

                        sosShown = true
                    }

                    // =====================================
                    // LOCATION
                    // =====================================

                    else if (
                        type == "LOCATION" &&
                        !locationShown
                    ) {

                        showLocationNotification(
                            title,
                            description,
                            time
                        )

                        locationShown = true
                    }

                    // =====================================
                    // CALL
                    // =====================================

                    else if (
                        type == "CALL" &&
                        !callShown
                    ) {

                        showCallNotification(
                            title,
                            description,
                            time
                        )

                        callShown = true
                    }

                    // =====================================
                    // SAFETY CHECK
                    // =====================================

                    else if (
                        type == "SAFETY_CHECK" &&
                        !safetyShown
                    ) {

                        showSafetyNotification(
                            title,
                            description,
                            time
                        )

                        safetyShown = true
                    }

                    // =====================================
                    // STOP AFTER ALL 4 FOUND
                    // =====================================

                    if (
                        sosShown &&
                        locationShown &&
                        callShown &&
                        safetyShown
                    ) {
                        break
                    }
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load notifications",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    // =====================================================
    // SHOW SOS NOTIFICATION
    // =====================================================

    private fun showSOSNotification(
        title: String,
        description: String,
        time: String
    ) {

        cardSOS.visibility = View.VISIBLE

        tvSOSIcon.text = "🚨"

        tvSOSTitle.text = title

        tvSOSDescription.text = description

        tvSOSTime.text = time
    }


    // =====================================================
    // SHOW LOCATION NOTIFICATION
    // =====================================================

    private fun showLocationNotification(
        title: String,
        description: String,
        time: String
    ) {

        cardLocation.visibility = View.VISIBLE

        tvLocationIcon.text = "📍"

        tvLocationTitle.text = title

        tvLocationDescription.text = description

        tvLocationTime.text = time
    }


    // =====================================================
    // SHOW CALL NOTIFICATION
    // =====================================================

    private fun showCallNotification(
        title: String,
        description: String,
        time: String
    ) {

        cardCall.visibility = View.VISIBLE

        tvCallIcon.text = "📞"

        tvCallTitle.text = title

        tvCallDescription.text = description

        tvCallTime.text = time
    }


    // =====================================================
    // SHOW SAFETY CHECK NOTIFICATION
    // =====================================================

    private fun showSafetyNotification(
        title: String,
        description: String,
        time: String
    ) {

        cardSafety.visibility = View.VISIBLE

        tvSafetyIcon.text = "🛡️"

        tvSafetyTitle.text = title

        tvSafetyDescription.text = description

        tvSafetyTime.text = time
    }
}