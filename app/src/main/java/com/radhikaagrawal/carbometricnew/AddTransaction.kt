package com.radhikaagrawal.carbometricnew

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AddTransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val btnTransport = findViewById<Button>(R.id.btnTransport)
        val btnEcommerce = findViewById<Button>(R.id.btnEcommerce)
        val btnElectricity = findViewById<Button>(R.id.btnElectricity)
        val btnEdibles = findViewById<Button>(R.id.btnEdibles)

        btnTransport.setOnClickListener {
            startActivity(Intent(this, TransportActivity::class.java))
        }

        btnEcommerce.setOnClickListener {
            startActivity(Intent(this, EcommerceActivity::class.java))
        }

        btnElectricity.setOnClickListener {
            startActivity(Intent(this, ElectricityActivity::class.java))
        }

        btnEdibles.setOnClickListener {
            startActivity(Intent(this, EdiblesActivity::class.java))
        }
    }
}
