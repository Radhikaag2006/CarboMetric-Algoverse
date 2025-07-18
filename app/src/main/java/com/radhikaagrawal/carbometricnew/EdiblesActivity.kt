package com.radhikaagrawal.carbometricnew

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EdiblesActivity : AppCompatActivity() {

    private lateinit var spinnerMealType: Spinner
    private lateinit var etQuantity: EditText
    private lateinit var spinnerDineOption: Spinner
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var btnSubmit: Button
    private lateinit var db: FirebaseFirestore

    // Replace with actual user id retrieval logic if using Firebase Auth
    private val userId: String = "user_003"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edibles)

        db = FirebaseFirestore.getInstance()

        spinnerMealType = findViewById(R.id.spinnerMealType)
        etQuantity = findViewById(R.id.etQuantity)
        spinnerDineOption = findViewById(R.id.spinnerDineOption)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        btnSubmit = findViewById(R.id.btnSubmit)

        val mealTypes = listOf("Vegetarian", "Dairy", "Poultry", "Paneer Tikka", "Breakfast", "Lunch", "Dinner", "Snacks")
        val dineOptions = listOf("Dine In", "Order")

        spinnerMealType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mealTypes)
        spinnerDineOption.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dineOptions)

        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                etDate.setText("${d.toString().padStart(2, '0')}/${(m + 1).toString().padStart(2, '0')}/$y")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        etTime.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(this, { _, h, min ->
                etTime.setText(String.format("%02d:%02d", h, min))
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        btnSubmit.setOnClickListener {
            val mealType = spinnerMealType.selectedItem.toString()
            val quantityStr = etQuantity.text.toString()
            val dineOption = spinnerDineOption.selectedItem.toString()
            val date = etDate.text.toString()
            val time = etTime.text.toString()

            if (mealType.isBlank() || quantityStr.isBlank() || dineOption.isBlank() || date.isBlank() || time.isBlank()) {
                Toast.makeText(this, "Please fill all details.", Toast.LENGTH_SHORT).show()
            } else {
                val amount = quantityStr.toDoubleOrNull() ?: 0.0
                val emissionFactor = getEmissionFactor(mealType, amount)
                val level = getEmissionLevel(emissionFactor)
                val emissionDateIso = parseToIso(date, time)
                val nowIso = getCurrentIsoDateTime()

                val itemDetails = "$mealType, $quantityStr plate"

                val ediblesData = hashMapOf(
                    "userId" to userId,
                    "category" to "EDIBLES",
                    "amount" to amount,
                    "emissionDate" to emissionDateIso,
                    "level" to level,
                    "details" to hashMapOf(
                        "source" to if (dineOption == "Order") "OUTSIDE" else "INSIDE",
                        "category" to "FOOD",
                        "mealType" to mealType.uppercase(),
                        "itemDetails" to itemDetails
                    ),
                    "createdAt" to nowIso,
                    "updatedAt" to nowIso
                )

                db.collection("emissions")
                    .add(ediblesData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Edible data submitted to Firestore!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to submit: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // Returns emission factor (kg CO2e per plate) based on meal type
    private fun getEmissionFactor(mealType: String, quantity: Double): Double {
        // Typical emission factors (kg CO2e per plate). Adjust as needed.
        return when (mealType.uppercase()) {
            "VEGETARIAN" -> 1.5 * quantity     // 1.5 kg CO2e/plate
            "DAIRY" -> 2.0 * quantity          // 2.0 kg CO2e/plate
            "POULTRY" -> 4.5 * quantity        // 4.5 kg CO2e/plate
            "PANEER TIKKA" -> 2.2 * quantity   // 2.2 kg CO2e/plate
            "BREAKFAST", "LUNCH", "DINNER", "SNACKS" -> 1.0 * quantity
            else -> 1.0 * quantity
        }
    }

    // Curated emission level based on emission factor (total kg CO2e)
    private fun getEmissionLevel(emissionFactor: Double): String {
        return when {
            emissionFactor < 2.0 -> "LOW"
            emissionFactor < 5.0 -> "MEDIUM"
            emissionFactor < 10.0 -> "HIGH"
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