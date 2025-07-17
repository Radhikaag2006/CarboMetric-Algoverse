package com.radhikaagrawal.carbometricnew

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class GetStartedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        // Assume you go to ChoiceActivity after pressing a button
        val getStartedBtn: Button = findViewById(R.id.btnGetStarted)
        getStartedBtn.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
            finish()
        }
    }
}
