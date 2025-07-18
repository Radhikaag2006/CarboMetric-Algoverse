package com.radhikaagrawal.carbometricnew

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        userPreferences = UserPreferences(this)

        val emailEditText = findViewById<EditText>(R.id.emailInput)
        val passwordEditText = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginBtn)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Save login state and user information
                        userPreferences.setLoggedIn(true)
                        userPreferences.setUserEmail(email)

                        // Get user name from Firebase if available, otherwise use email
                        val currentUser = auth.currentUser
                        val userName = currentUser?.displayName ?: email.substringBefore("@")
                        userPreferences.setUserName(userName)

                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                        // Check if user has completed details form
                        if (userPreferences.hasCompletedDetails()) {
                            // Go directly to AddTransactionActivity
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        } else {
                            // Go to NextDetailsFormActivity to complete profile
                            startActivity(Intent(this, NextDetailsFormActivity::class.java))
                        }
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
