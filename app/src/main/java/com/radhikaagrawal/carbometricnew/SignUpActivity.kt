package com.radhikaagrawal.carbometricnew

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth



class SignUpActivity : AppCompatActivity() {

    private lateinit var fullNameEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var passEt: EditText
    private lateinit var signUpBtn: Button
    private lateinit var togglePasswordIcon: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var userPreferences: UserPreferences

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // 🔗 Connect XML views
        fullNameEt = findViewById(R.id.fullName)
        emailEt = findViewById(R.id.email)
        passEt = findViewById(R.id.password)
        signUpBtn = findViewById(R.id.signUpBtn)
        togglePasswordIcon = findViewById(R.id.togglePasswordVisibility)
        auth = FirebaseAuth.getInstance()
        userPreferences = UserPreferences(this)

        // 🔐 Toggle password visibility
        togglePasswordIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passEt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordIcon.setImageResource(R.drawable.visible) // 👁️ eye-open icon
            } else {
                passEt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordIcon.setImageResource(R.drawable.not_visible) // 🙈 eye-closed icon
            }
            passEt.setSelection(passEt.text.length)
        }

        // 🔐 Sign up and auto login
        signUpBtn.setOnClickListener {
            val fullName = fullNameEt.text.toString().trim()
            val email = emailEt.text.toString().trim()
            val password = passEt.text.toString().trim()

            if (fullName.isEmpty()) {
                fullNameEt.error = "Full name required"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                emailEt.error = "Email required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passEt.error = "Password required"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Save login state and user information
                        userPreferences.setLoggedIn(true)
                        userPreferences.setUserEmail(email)
                        userPreferences.setUserName(fullName)
                        userPreferences.setHasCompletedDetails(false)

                        Toast.makeText(this, "Welcome, $fullName!", Toast.LENGTH_SHORT).show()

                        // ✅ Navigate to UserNextDetailsActivity
                        val intent = Intent(this, NextDetailsFormActivity::class.java)
                        intent.putExtra("userName", fullName)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
