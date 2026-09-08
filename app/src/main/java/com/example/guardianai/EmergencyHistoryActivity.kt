package com.example.guardianai

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class EmergencyHistoryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var recyclerHistory: RecyclerView
    private lateinit var tvEmptyHistory: TextView

    private lateinit var historyAdapter: EmergencyHistoryAdapter

    private val historyList =
        ArrayList<HistoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_emergency_history
        )

        // Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Views
        val tvBack =
            findViewById<TextView>(
                R.id.tvBack
            )

        recyclerHistory =
            findViewById<RecyclerView>(
                R.id.recyclerHistory
            )

        tvEmptyHistory =
            findViewById<TextView>(
                R.id.tvEmptyHistory
            )

        // Adapter
        historyAdapter =
            EmergencyHistoryAdapter(
                historyList
            )

        recyclerHistory.layoutManager =
            LinearLayoutManager(this)

        recyclerHistory.adapter =
            historyAdapter

        // Back button
        tvBack.setOnClickListener {
            finish()
        }

        // Load history
        loadEmergencyHistory()
    }

    private fun loadEmergencyHistory() {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "Please login first",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        val userId =
            currentUser.uid

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

                historyList.clear()

                for (document in documents) {

                    // =========================
                    // GET NOTIFICATION DETAILS
                    // =========================

                    val type =
                        document.getString("type")
                            ?: "UNKNOWN"

                    val title =
                        document.getString("title")
                            ?: "Emergency Activity"

                    val description =
                        document.getString("description")
                            ?: ""


                    // =========================
                    // GET REAL DATE & TIME
                    // =========================

                    val timestamp =
                        document.getTimestamp(
                            "timestamp"
                        )

                    val time =
                        if (timestamp != null) {

                            val dateFormat =
                                SimpleDateFormat(
                                    "dd MMM yyyy, hh:mm a",
                                    Locale.getDefault()
                                )

                            dateFormat.format(
                                timestamp.toDate()
                            )

                        } else {

                            // Fallback for old records
                            document.getString("time")
                                ?: "Unknown time"
                        }


                    // =========================
                    // SELECT ICON
                    // =========================

                    val icon =
                        when (type) {

                            "SOS" -> {
                                "🚨"
                            }

                            "LOCATION" -> {
                                "📍"
                            }

                            "CALL" -> {
                                "📞"
                            }

                            "SAFETY_CHECK" -> {

                                if (
                                    title.contains(
                                        "Missed",
                                        ignoreCase = true
                                    )
                                ) {
                                    "⚠️"
                                } else {
                                    "🛡️"
                                }
                            }

                            else -> {
                                "🔔"
                            }
                        }


                    // =========================
                    // CREATE HISTORY ITEM
                    // =========================

                    val historyItem =
                        HistoryItem(
                            icon = icon,
                            title = title,
                            description = description,
                            time = time
                        )

                    historyList.add(
                        historyItem
                    )
                }


                // =========================
                // EMPTY / NON EMPTY STATE
                // =========================

                if (historyList.isEmpty()) {

                    tvEmptyHistory.visibility =
                        View.VISIBLE

                    recyclerHistory.visibility =
                        View.GONE

                } else {

                    tvEmptyHistory.visibility =
                        View.GONE

                    recyclerHistory.visibility =
                        View.VISIBLE
                }


                // Refresh RecyclerView
                historyAdapter
                    .notifyDataSetChanged()
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to load history",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}