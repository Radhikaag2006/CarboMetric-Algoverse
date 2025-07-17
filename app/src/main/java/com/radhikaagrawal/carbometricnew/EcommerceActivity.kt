package com.radhikaagrawal.carbometricnew

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class EcommerceActivity : AppCompatActivity() {

    private lateinit var categorySpinner: Spinner
    private lateinit var purchaseTypeSpinner: Spinner
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecommerce)

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
                Toast.makeText(this, "E-commerce data submitted", Toast.LENGTH_SHORT).show()
                // Add Firestore or local storage logic here
            }
        }
    }
}
