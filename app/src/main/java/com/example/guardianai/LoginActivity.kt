package com.example.guardianai

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvSignup = findViewById<TextView>(R.id.tvSignup)

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Please enter your email"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Please enter your password"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error =
                    "Password must be at least 6 characters"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            btnLogin.text = "LOGGING IN..."

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        Toast.makeText(
                            this,
                            "Login successful!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this,
                            MainActivity::class.java
                        )

                        startActivity(intent)

                        finish()

                    } else {

                        btnLogin.isEnabled = true
                        btnLogin.text = "LOGIN   →"

                        val errorMessage =
                            task.exception?.message ?: "Login failed"

                        Toast.makeText(
                            this,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
        tvSignup.setOnClickListener {

            val intent = Intent(
                this,
                SignupActivity::class.java
            )

            startActivity(intent)
        }
    }
}