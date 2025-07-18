package com.radhikaagrawal.carbometricnew

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EcommerceActivity : AppCompatActivity() {

    private lateinit var categorySpinner: Spinner
    private lateinit var purchaseTypeSpinner: Spinner
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecommerce)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize views
        categorySpinner = findViewById(R.id.spinnerCategory)
        purchaseTypeSpinner = findViewById(R.id.spinnerPurchaseType)
        dateEditText = findViewById(R.id.etDate)
        timeEditText = findViewById(R.id.etTime)
        submitButton = findViewById(R.id.btnSubmit)

        // Spinner options
        val categories = listOf("Select Category", "Apparels", "Furniture", "Electronic Gadgets", "Stationary")
        val purchaseOptions = listOf("Select Option", "Bought in Store", "Ordered")

        categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        purchaseTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, purchaseOptions)

        // Date picker logic
        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, { _, year, month, day ->
                val formatted = "${day.toString().padStart(2, '0')}/" +
                        "${(month + 1).toString().padStart(2, '0')}/$year"
                dateEditText.setText(formatted)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        // Time picker logic
        timeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePicker = TimePickerDialog(this, { _, hour, minute ->
                val formatted = "${hour.toString().padStart(2, '0')}:" +
                        "${minute.toString().padStart(2, '0')}"
                timeEditText.setText(formatted)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            timePicker.show()
        }

        // Submit button logic
        submitButton.setOnClickListener {
            val category = categorySpinner.selectedItem.toString()
            val purchaseType = purchaseTypeSpinner.selectedItem.toString()
            val date = dateEditText.text.toString()
            val time = timeEditText.text.toString()

            if (category == "Select Category" || purchaseType == "Select Option" ||
                date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                // Emission level logic for e-commerce
                val rating = getEcommerceEmissionLevel(category, purchaseType)

                // Prepare data to send to Firestore
                val ecommerceData = hashMapOf(
                    "category" to category,
                    "purchaseType" to purchaseType,
                    "date" to date,
                    "time" to time,
                    "type" to "E-COMMERCE", // ALL CAPS
                    "rating" to rating
                )

                db.collection("emissions")
                    .add(ecommerceData)
                    .addOnSuccessListener {

                        Toast.makeText(this, "E-commerce data submitted to Firestore!", Toast.LENGTH_SHORT).show()
                        // Optionally, clear fields or finish activity

                        val intent = Intent(this@EcommerceActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to submit: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // Emission level logic for e-commerce
    private fun getEcommerceEmissionLevel(category: String, purchaseType: String): String {
        return when (category.uppercase()) {
            "ELECTRONIC GADGETS", "FURNITURE" ->
                if (purchaseType.uppercase() == "ORDERED") "HIGH" else "MEDIUM"
            "APPARELS" -> "MEDIUM"
            "STATIONARY" -> "LOW"
            else -> "MEDIUM"
        }
    }
}