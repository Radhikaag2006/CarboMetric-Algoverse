package com.radhikaagrawal.carbometricnew

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class TransportActivity : AppCompatActivity() {

    private lateinit var modeEditText: EditText
    private lateinit var distanceEditText: EditText
    private lateinit var transportSpinner: Spinner
    private lateinit var travelTypeSpinner: Spinner
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transport)

        // Initialize views
      //  modeEditText = findViewById(R.id.etMode)
        distanceEditText = findViewById(R.id.etDistance)
        transportSpinner = findViewById(R.id.spinnerTransport)
        travelTypeSpinner = findViewById(R.id.spinnerTravelType)
        dateEditText = findViewById(R.id.etDate)
        timeEditText = findViewById(R.id.etTime)
        submitButton = findViewById(R.id.btnSubmit)

        // Dropdown data
        val transportOptions = listOf("Select Mode", "Car", "Metro", "Train", "Bike", "Bus", "Aeroplane")
        val travelTypes = listOf("Select Option", "Solo", "Pooling", "Public Transport")

        transportSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, transportOptions)
        travelTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, travelTypes)

        // Date Picker
        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val dateString = "${selectedDay.toString().padStart(2, '0')}/" +
                        "${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
                dateEditText.setText(dateString)
            }, year, month, day).show()
        }

        // Time Picker
        timeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val timeString = "${selectedHour.toString().padStart(2, '0')}:" +
                        "${selectedMinute.toString().padStart(2, '0')}"
                timeEditText.setText(timeString)
            }, hour, minute, true).show()
        }

        // Submit Button Click
        submitButton.setOnClickListener {
            val mode = modeEditText.text.toString().trim()
            val distance = distanceEditText.text.toString().trim()
            val selectedTransport = transportSpinner.selectedItem.toString()
            val selectedTravelType = travelTypeSpinner.selectedItem.toString()
            val date = dateEditText.text.toString().trim()
            val time = timeEditText.text.toString().trim()

            if (mode.isEmpty() || distance.isEmpty() || selectedTransport == "Select Mode" ||
                selectedTravelType == "Select Option" || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Transport info submitted", Toast.LENGTH_SHORT).show()
                // Database or navigation logic goes here
            }
        }
    }
}
