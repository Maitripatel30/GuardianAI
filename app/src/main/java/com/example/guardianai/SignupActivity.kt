package com.example.guardianai

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirmPassword =
            findViewById<TextInputEditText>(R.id.etConfirmPassword)

        val btnSignup = findViewById<MaterialButton>(R.id.btnSignup)

        val tvLogin = findViewById<android.widget.TextView>(R.id.tvLogin)
        btnSignup.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword =
                etConfirmPassword.text.toString().trim()
            if (name.isEmpty()) {
                etName.error = "Please enter your name"
                etName.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                etEmail.error = "Please enter your email"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                etPassword.error = "Please enter a password"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error =
                    "Password must be at least 6 characters"
                etPassword.requestFocus()
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                etConfirmPassword.error =
                    "Please confirm your password"
                etConfirmPassword.requestFocus()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                etConfirmPassword.error =
                    "Passwords do not match"
                etConfirmPassword.requestFocus()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        Toast.makeText(
                            this,
                            "Account created successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(
                            this,
                            LoginActivity::class.java
                        )

                        startActivity(intent)
                        finish()

                    } else {

                        Toast.makeText(
                            this,
                            "Signup failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        tvLogin.setOnClickListener {

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)
            finish()
        }
    }
}