package com.radhikaagrawal.carbometricnew

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class GetStartedActivity : AppCompatActivity() {

    private lateinit var getStartedButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        getStartedButton = findViewById(R.id.getStartedBtn)

        getStartedButton.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                // New user
                startActivity(Intent(this, SignUpActivity::class.java))
            } else {
                // Existing user
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }
    }
}
