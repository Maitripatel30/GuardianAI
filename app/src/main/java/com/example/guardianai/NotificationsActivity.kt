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
    private lateinit var cardSOS: MaterialCardView
    private lateinit var cardLocation: MaterialCardView
    private lateinit var cardCall: MaterialCardView
    private lateinit var cardSafety: MaterialCardView
    private lateinit var tvSOSTitle: TextView
    private lateinit var tvSOSDescription: TextView
    private lateinit var tvSOSTime: TextView
    private lateinit var tvSOSIcon: TextView
    private lateinit var tvLocationTitle: TextView
    private lateinit var tvLocationDescription: TextView
    private lateinit var tvLocationTime: TextView
    private lateinit var tvLocationIcon: TextView
    private lateinit var tvCallTitle: TextView
    private lateinit var tvCallDescription: TextView
    private lateinit var tvCallTime: TextView
    private lateinit var tvCallIcon: TextView
    private lateinit var tvSafetyTitle: TextView
    private lateinit var tvSafetyDescription: TextView
    private lateinit var tvSafetyTime: TextView
    private lateinit var tvSafetyIcon: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notifications)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val tvBack =
            findViewById<TextView>(R.id.tvBack)
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
        tvBack.setOnClickListener {
            finish()
        }

        cardSOS.visibility = View.GONE
        cardLocation.visibility = View.GONE
        cardCall.visibility = View.GONE
        cardSafety.visibility = View.GONE

        loadNotifications()
    }

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

                if (documents.isEmpty()) {

                    Toast.makeText(
                        this,
                        "No recent activity",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }
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