package com.example.guardianai

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SafetyCheckActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnStart: MaterialButton
    private lateinit var btnSafe: MaterialButton
    private lateinit var btnCancel: MaterialButton

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var countDownTimer: CountDownTimer? = null
    private var timerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_safety_check)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val tvBack =
            findViewById<TextView>(R.id.tvBack)

        tvTimer =
            findViewById(R.id.tvTimer)

        tvStatus =
            findViewById(R.id.tvStatus)

        btnStart =
            findViewById(R.id.btnStartTimer)

        btnSafe =
            findViewById(R.id.btnImSafe)

        btnCancel =
            findViewById(R.id.btnCancelTimer)

        tvTimer.text = "00:00"

        tvStatus.text =
            "Set a timer and let GuardianAI check your safety."

        btnSafe.isEnabled = false

        btnCancel.isEnabled = false

        tvBack.setOnClickListener {

            if (timerRunning) {

                AlertDialog.Builder(this)
                    .setTitle("⚠️ Timer Running")
                    .setMessage(
                        "A Safety Check timer is currently running. " +
                                "Are you sure you want to leave?"
                    )
                    .setPositiveButton("LEAVE") { _, _ ->
                        finish()
                    }
                    .setNegativeButton(
                        "STAY",
                        null
                    )
                    .show()

            } else {

                finish()
            }
        }

        btnStart.setOnClickListener {

            showTimerSelection()
        }
        btnSafe.setOnClickListener {

            markAsSafe()
        }

        btnCancel.setOnClickListener {

            cancelTimer()
        }
    }

    private fun showTimerSelection() {

        val options =
            arrayOf(
                "5 Minutes",
                "10 Minutes",
                "15 Minutes",
                "30 Minutes",
                "60 Minutes"
            )

        AlertDialog.Builder(this)
            .setTitle("🛡️ Set Safety Timer")
            .setItems(options) { _, which ->

                val minutes =
                    when (which) {

                        0 -> 5
                        1 -> 10
                        2 -> 15
                        3 -> 30
                        4 -> 60

                        else -> 5
                    }

                startSafetyTimer(minutes)
            }
            .setNegativeButton(
                "CANCEL",
                null
            )
            .show()
    }
    private fun startSafetyTimer(minutes: Int) {

        // Cancel previous timer
        countDownTimer?.cancel()

        val duration =
            minutes * 60 * 1000L

        timerRunning = true

        btnStart.isEnabled = false
        btnSafe.isEnabled = true
        btnCancel.isEnabled = true

        tvStatus.text =
            "Safety timer is active. Reach your destination safely."

        countDownTimer =
            object : CountDownTimer(
                duration,
                1000
            ) {

                override fun onTick(
                    millisUntilFinished: Long
                ) {

                    val totalSeconds =
                        millisUntilFinished / 1000

                    val minutesRemaining =
                        totalSeconds / 60

                    val secondsRemaining =
                        totalSeconds % 60

                    tvTimer.text =
                        String.format(
                            "%02d:%02d",
                            minutesRemaining,
                            secondsRemaining
                        )
                }

                override fun onFinish() {

                    timerRunning = false

                    tvTimer.text = "00:00"

                    btnStart.isEnabled = true
                    btnSafe.isEnabled = false
                    btnCancel.isEnabled = false

                    tvStatus.text =
                        "⚠️ Safety check missed!"

                    // Save missed safety check
                    saveSafetyMissedNotification()

                    // Show emergency warning
                    showSafetyMissedDialog()
                }

            }.start()
    }
    private fun markAsSafe() {

        countDownTimer?.cancel()

        timerRunning = false

        tvTimer.text = "00:00"

        tvStatus.text =
            "✅ You are marked as SAFE."

        btnStart.isEnabled = true
        btnSafe.isEnabled = false
        btnCancel.isEnabled = false

        // Save completed notification
        saveSafetyCompletedNotification()

        Toast.makeText(
            this,
            "✅ Safety check completed",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun cancelTimer() {

        countDownTimer?.cancel()

        timerRunning = false

        tvTimer.text = "00:00"

        tvStatus.text =
            "Safety timer cancelled."

        btnStart.isEnabled = true
        btnSafe.isEnabled = false
        btnCancel.isEnabled = false

        Toast.makeText(
            this,
            "Safety timer cancelled",
            Toast.LENGTH_SHORT
        ).show()
    }
    private fun saveSafetyCompletedNotification() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            return
        }

        val userId = currentUser.uid

        val notification =
            hashMapOf(
                "type" to "SAFETY_CHECK",
                "title" to "Safety Check Completed",
                "description" to
                        "You confirmed that you are safe.",
                "time" to "Just now",
                "timestamp" to FieldValue.serverTimestamp()
            )

        firestore
            .collection("users")
            .document(userId)
            .collection("notifications")
            .add(notification)
    }
    private fun saveSafetyMissedNotification() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            return
        }

        val userId = currentUser.uid

        val notification =
            hashMapOf(
                "type" to "SAFETY_CHECK",
                "title" to "Safety Check Missed",
                "description" to
                        "You did not confirm your safety before the timer expired.",
                "time" to "Just now",
                "timestamp" to FieldValue.serverTimestamp()
            )

        firestore
            .collection("users")
            .document(userId)
            .collection("notifications")
            .add(notification)
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Unable to save safety notification",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
    private fun showSafetyMissedDialog() {

        AlertDialog.Builder(this)

            .setTitle("⚠️ Safety Check Missed")

            .setMessage(
                "You did not confirm that you are safe " +
                        "before the timer expired.\n\n" +
                        "Please confirm your current safety status."
            )

            .setPositiveButton(
                "I'M SAFE NOW"
            ) { _, _ ->

                tvStatus.text =
                    "✅ You are marked as SAFE."

                Toast.makeText(
                    this,
                    "✅ Safety confirmed",
                    Toast.LENGTH_LONG
                ).show()

                saveSafetyCompletedNotification()
            }

            .setNegativeButton(
                "CLOSE",
                null
            )

            .show()
    }


    override fun onDestroy() {

        countDownTimer?.cancel()

        super.onDestroy()
    }
}