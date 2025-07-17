package com.radhikaagrawal.carbometricnew

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ElectricityActivity : AppCompatActivity() {

    private lateinit var applianceSpinner: Spinner
    private lateinit var durationEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_electricity)

        // Initialize views
        applianceSpinner = findViewById(R.id.spinnerAppliance)
        durationEditText = findViewById(R.id.editTextDuration)
        dateEditText = findViewById(R.id.editTextDate)
        timeEditText = findViewById(R.id.editTextTime)
        submitButton = findViewById(R.id.submitBtn)

        // Set up appliance options
        val appliances = listOf(
            "Select Appliance",
            "AC",
            "Laptop",
            "Household Appliance",
            "Commercial Machines"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, appliances)
        applianceSpinner.adapter = adapter

        // Date picker logic
        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val formatted = "${dayOfMonth.toString().padStart(2, '0')}/" +
                            "${(month + 1).toString().padStart(2, '0')}/$year"
                    dateEditText.setText(formatted)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Time picker logic
        timeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val formatted = "${hourOfDay.toString().padStart(2, '0')}:" +
                            "${minute.toString().padStart(2, '0')}"
                    timeEditText.setText(formatted)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        }

        // Submit button logic
        submitButton.setOnClickListener {
            val appliance = applianceSpinner.selectedItem.toString()
            val duration = durationEditText.text.toString()
            val date = dateEditText.text.toString()
            val time = timeEditText.text.toString()

            if (appliance == "Select Appliance" || duration.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Electricity data submitted", Toast.LENGTH_SHORT).show()
                // Add Firestore or database logic here
            }
        }
    }
}
