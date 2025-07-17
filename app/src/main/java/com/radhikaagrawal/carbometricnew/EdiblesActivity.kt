package com.radhikaagrawal.carbometricnew

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class EdiblesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edibles)

        val spinnerMealType = findViewById<Spinner>(R.id.spinnerMealType)
        val etQuantity = findViewById<EditText>(R.id.etQuantity)
        val spinnerDineOption = findViewById<Spinner>(R.id.spinnerDineOption)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etTime = findViewById<EditText>(R.id.etTime)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        val mealTypes = listOf("Vegetarian", "Poultry", "Dairy")
        val dineOptions = listOf("Dine In", "Order")

        spinnerMealType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mealTypes)
        spinnerDineOption.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dineOptions)

        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                etDate.setText("$d/${m + 1}/$y")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        etTime.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(this, { _, h, min ->
                etTime.setText(String.format("%02d:%02d", h, min)) // comment
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        btnSubmit.setOnClickListener {
            val type = spinnerMealType.selectedItem.toString()
            val quantity = etQuantity.text.toString()
            val dine = spinnerDineOption.selectedItem.toString()
            val date = etDate.text.toString()
            val time = etTime.text.toString()

            if (quantity.isBlank() || date.isBlank() || time.isBlank()) {
                Toast.makeText(this, "Please fill all details.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Edible data submitted!", Toast.LENGTH_SHORT).show()
                // TODO: Submit or store the data
            }
        }
    }
}
