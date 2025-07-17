package com.radhikaagrawal.carbometricnew

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class TransportActivity : AppCompatActivity() {

    private lateinit var modeEditText: EditText
    private lateinit var distanceEditText: EditText
    private lateinit var transportSpinner: Spinner
    private lateinit var travelTypeSpinner: Spinner
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var submitButton: Button
// comment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transport)

        // Initialize views
       // modeEditText = findViewById(R.id.etMode)
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
           // val mode = modeEditText.text.toString().trim()
            val distance = distanceEditText.text.toString().trim()
            val selectedTransport = transportSpinner.selectedItem.toString()
            val selectedTravelType = travelTypeSpinner.selectedItem.toString()
            val date = dateEditText.text.toString().trim()
            val time = timeEditText.text.toString().trim()

            if (distance.isEmpty() || selectedTransport == "Select Mode" ||
                selectedTravelType == "Select Option" || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Calculate the emission amount
                val emissionFactor = getEmissionFactor(selectedTransport)
                val distanceKm = distance.toDoubleOrNull() ?: 0.0
                val amount = emissionFactor * distanceKm

                // Determine emission level
                val level = getEmissionLevel(selectedTransport, amount)

                // Convert date and time to ISO format
                val emissionDate = convertToISO(date, time)

                // Prepare Firestore data
                val db = FirebaseFirestore.getInstance()
                val transportData = hashMapOf(
                    "userId" to "user_001", // Replace with actual user id logic if available
                    "category" to "TRANSPORT",
                    "amount" to amount,
                    "emissionDate" to emissionDate,
                    "level" to level,
                    "details" to hashMapOf(
                        "mode" to selectedTransport.uppercase(),
                        "distanceKm" to distanceKm,
                        "travelType" to selectedTravelType.uppercase(),
                        //"notes" to mode
                    ),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )

                db.collection("emissions")
                    .add(transportData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Transport info saved!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // Lookup table for emission factor (kg CO2 per km)
    private fun getEmissionFactor(mode: String): Double {
        return when (mode.uppercase()) {
            "CAR" -> 0.192
            "METRO" -> 0.041
            "TRAIN" -> 0.045
            "BIKE" -> 0.016
            "BUS" -> 0.105
            "AEROPLANE" -> 0.255
            else -> 0.0
        }
    }

    // Emission level logic
    private fun getEmissionLevel(mode: String, amount: Double): String {
        return when (mode.uppercase()) {
            "CAR" -> when {
                amount < 2 -> "LOW"
                amount < 10 -> "MEDIUM"
                amount < 30 -> "HIGH"
                else -> "EXTREME"
            }
            "METRO", "TRAIN" -> when {
                amount < 1 -> "LOW"
                amount < 5 -> "MEDIUM"
                amount < 10 -> "HIGH"
                else -> "EXTREME"
            }
            "BIKE" -> when {
                amount < 0.5 -> "LOW"
                amount < 2 -> "MEDIUM"
                amount < 5 -> "HIGH"
                else -> "EXTREME"
            }
            "BUS" -> when {
                amount < 1 -> "LOW"
                amount < 5 -> "MEDIUM"
                amount < 15 -> "HIGH"
                else -> "EXTREME"
            }
            "AEROPLANE" -> when {
                amount < 10 -> "LOW"
                amount < 50 -> "MEDIUM"
                amount < 100 -> "HIGH"
                else -> "EXTREME"
            }
            else -> "LOW"
        }
    }

    // Converts date ("dd/MM/yyyy") and time ("HH:mm") to ISO 8601 string ("yyyy-MM-ddTHH:mm:ssZ")
    private fun convertToISO(date: String, time: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getDefault()
            val dateObj = inputFormat.parse("$date $time")
            outputFormat.timeZone = TimeZone.getTimeZone("UTC")
            outputFormat.format(dateObj ?: Date())
        } catch (e: Exception) {
            // fallback: current UTC time
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .format(Date())
        }
    }
}