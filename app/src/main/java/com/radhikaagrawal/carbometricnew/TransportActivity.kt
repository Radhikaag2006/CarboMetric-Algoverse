package com.radhikaagrawal.carbometricnew

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TransportActivity : AppCompatActivity() {

    private lateinit var modeEditText: EditText
    private lateinit var distanceEditText: EditText
    private lateinit var transportSpinner: Spinner
    private lateinit var travelTypeSpinner: Spinner
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transport)

        modeEditText = findViewById(R.id.etMode)
        distanceEditText = findViewById(R.id.etDistance)
        transportSpinner = findViewById(R.id.spinnerTransport)
        travelTypeSpinner = findViewById(R.id.spinnerTravelType)
        submitButton = findViewById(R.id.btnSubmit)

        val transportOptions = listOf("Select Mode", "Car", "Metro", "Train", "Bike", "Bus", "Aeroplane")
        val travelTypes = listOf("Select Option", "Solo", "Pooling", "Public Transport")

        transportSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, transportOptions)
        travelTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, travelTypes)

        submitButton.setOnClickListener {
            val mode = modeEditText.text.toString().trim()
            val distance = distanceEditText.text.toString().trim()
            val selectedTransport = transportSpinner.selectedItem.toString()
            val selectedTravelType = travelTypeSpinner.selectedItem.toString()

            if (mode.isEmpty() || distance.isEmpty() || selectedTransport == "Select Mode" || selectedTravelType == "Select Option") {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Transport info submitted", Toast.LENGTH_SHORT).show()
                // Add database or navigation logic here
            }
        }
    }
}
