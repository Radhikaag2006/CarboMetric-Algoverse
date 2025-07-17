package com.radhikaagrawal.carbometricnew

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NextDetailsFormActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var spinnerCountry: Spinner
    private lateinit var spinnerState: Spinner
    private lateinit var btnSaveProfile: MaterialButton

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next_details_form)

        // Initialize Views
        etUsername = findViewById(R.id.etUsername)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        spinnerCountry = findViewById(R.id.spinnerItem1)
        spinnerState = findViewById(R.id.spinnerItem2)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        // Setup Spinners
        val countryList = listOf("Select Country", "India", "USA", "Canada")
        val stateList = listOf("Select State", "Delhi", "California", "New York")

        val countryAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            R.id.spinnerItemText,
            countryList
        )
        spinnerCountry.adapter = countryAdapter

        val stateAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            R.id.spinnerItemText,
            stateList
        )
        spinnerState.adapter = stateAdapter

        // Save profile on button click
        btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val username = etUsername.text.toString().trim()
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val country = spinnerCountry.selectedItem.toString()
        val state = spinnerState.selectedItem.toString()
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (username.isEmpty() || name.isEmpty() || phone.isEmpty()
            || country == "Select Country" || state == "Select State") {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val userData = hashMapOf(
            "username" to username,
            "name" to name,
            "phone" to phone,
            "country" to country,
            "state" to state,
            "uid" to uid
        )

        db.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AddTransactionActivity::class.java)
                startActivity(intent)
                finish() // finish this activity to prevent going back with back button
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
