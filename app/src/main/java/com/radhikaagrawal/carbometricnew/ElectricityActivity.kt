package com.radhikaagrawal.carbometricnew

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ElectricityActivity : AppCompatActivity() {

    private lateinit var applianceSpinner: Spinner
    private lateinit var durationEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var db: FirebaseFirestore

    // Replace with actual user id retrieval logic if using Firebase Auth
    private val userId: String = "user_003"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_electricity)

        db = FirebaseFirestore.getInstance()

        applianceSpinner = findViewById(R.id.spinnerAppliance)
        durationEditText = findViewById(R.id.editTextDuration)
        dateEditText = findViewById(R.id.editTextDate)
        timeEditText = findViewById(R.id.editTextTime)
        submitButton = findViewById(R.id.submitBtn)

        val appliances = listOf(
            "Select Appliance",
            "AC",
            "Laptop",
            "Household Appliance",
            "Commercial Machines"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, appliances)
        applianceSpinner.adapter = adapter

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

        submitButton.setOnClickListener {
            val appliance = applianceSpinner.selectedItem.toString()
            val durationStr = durationEditText.text.toString()
            val date = dateEditText.text.toString()
            val time = timeEditText.text.toString()

            if (appliance == "Select Appliance" || durationStr.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val duration = durationStr.toDoubleOrNull() ?: 0.0
                val emissionAmount = getEmissionFactor(appliance, duration)
                val emissionLevel = getEmissionLevel(emissionAmount)
                val emissionDateIso = parseToIso(date, time)
                val nowIso = getCurrentIsoDateTime()

                val itemDetails = "$appliance, $duration hour(s)"

                val electricityData = hashMapOf(
                    "userId" to userId,
                    "category" to "ELECTRICITY",
                    "amount" to emissionAmount,
                    "emissionDate" to emissionDateIso,
                    "level" to emissionLevel,
                    "details" to hashMapOf(
                        "source" to "ELECTRICITY",
                        "category" to "ELECTRICITY",
                        "appliance" to appliance.uppercase(),
                        "itemDetails" to itemDetails,
                        "duration" to duration
                    ),
                    "createdAt" to nowIso,
                    "updatedAt" to nowIso
                )

                db.collection("emissions")
                    .add(electricityData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Electricity data submitted to Firestore!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to submit: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // Returns emission factor (kg CO2e) based on appliance and duration (hours)
    private fun getEmissionFactor(appliance: String, duration: Double): Double {
        // Example emission factors (kg CO2e per hour). Adjust as needed.
        return when (appliance.uppercase()) {
            "AC" -> 1.5 * duration
            "LAPTOP" -> 0.05 * duration
            "HOUSEHOLD APPLIANCE" -> 0.2 * duration
            "COMMERCIAL MACHINES" -> 2.0 * duration
            else -> 0.1 * duration
        }
    }

    // Curated emission level based on emission amount (total kg CO2e)
    private fun getEmissionLevel(emissionAmount: Double): String {
        return when {
            emissionAmount < 1.0 -> "LOW"
            emissionAmount < 5.0 -> "MEDIUM"
            emissionAmount < 10.0 -> "HIGH"
            else -> "EXTREME"
        }
    }

    // Converts date (dd/MM/yyyy) and time (HH:mm) to ISO 8601
    private fun parseToIso(date: String, time: String): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dt = sdf.parse("$date $time")
            val isoSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            isoSdf.timeZone = TimeZone.getTimeZone("UTC")
            isoSdf.format(dt!!)
        } catch (e: Exception) {
            ""
        }
    }

    private fun getCurrentIsoDateTime(): String {
        val isoSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        isoSdf.timeZone = TimeZone.getTimeZone("UTC")
        return isoSdf.format(Date())
    }
}