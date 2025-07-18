package com.radhikaagrawal.carbometricnew

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NextDetailsFormActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnSaveProfile: MaterialButton
    private lateinit var userPreferences: UserPreferences

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next_details_form)

        // Initialize Views
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        userPreferences = UserPreferences(this)

        // Prefill name if available from signup
        val userName = intent.getStringExtra("userName")
        if (!userName.isNullOrEmpty()) {
            etName.setText(userName)
        } else {
            // Try to get name from preferences
            userPreferences.getUserName()?.let { name ->
                etName.setText(name)
            }
        }

        // Save profile on button click
        btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        // Instead of blocking, close the app
        // When user reopens, they'll be forced to complete the form again
        finishAffinity() // This closes the entire app task stack
    }

    private fun saveProfile() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val uid = auth.currentUser?.uid
        val email = auth.currentUser?.email

        if (uid == null || email == null) {
            Toast.makeText(this, "User not logged in properly", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate input fields
        if (!isValidInput(name, phone)) {
            return
        }

        val userData = hashMapOf(
            "name" to name,
            "phone" to phone,
            "email" to email,
            "uid" to uid,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        // Show loading state
        btnSaveProfile.isEnabled = false
        btnSaveProfile.text = "Saving..."

        db.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener {
                // Mark that user has completed their profile details
                userPreferences.setHasCompletedDetails(true)
                userPreferences.setUserName(name) // Update the name in preferences

                Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // finish this activity to prevent going back with back button
            }
            .addOnFailureListener { exception ->
                // Reset button state
                btnSaveProfile.isEnabled = true
                btnSaveProfile.text = "Save Profile"

                Toast.makeText(this, "Failed to save profile: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun isValidInput(name: String, phone: String): Boolean {
        // Validate name
        if (name.isEmpty()) {
            etName.error = "Name is required"
            etName.requestFocus()
            return false
        }

        if (name.length < 2) {
            etName.error = "Name must be at least 2 characters"
            etName.requestFocus()
            return false
        }

        if (!name.matches(Regex("^[a-zA-Z\\s]+$"))) {
            etName.error = "Name can only contain letters and spaces"
            etName.requestFocus()
            return false
        }

        // Validate phone
        if (phone.isEmpty()) {
            etPhone.error = "Phone number is required"
            etPhone.requestFocus()
            return false
        }

        if (!phone.matches(Regex("^[+]?[0-9]{10,15}$"))) {
            etPhone.error = "Please enter a valid phone number (10-15 digits)"
            etPhone.requestFocus()
            return false
        }

        // Clear any existing errors
        etName.error = null
        etPhone.error = null

        return true
    }
}
