package com.radhikaagrawal.carbometricnew

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var userPreferences: UserPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        userPreferences = UserPreferences(this)
        auth = FirebaseAuth.getInstance()

        // Delay to show splash screen (2.5 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginState()
        }, 2500)
    }

    private fun checkLoginState() {
        val currentUser = auth.currentUser
        val isLoggedIn = userPreferences.isLoggedIn()

        if (currentUser != null && isLoggedIn) {
            // User is logged in, check if they completed details form
            if (userPreferences.hasCompletedDetails()) {
                // Go directly to AddTransactionActivity
                startActivity(Intent(this, AddTransactionActivity::class.java))
            } else {
                // Go to NextDetailsFormActivity to complete profile
                startActivity(Intent(this, NextDetailsFormActivity::class.java))
            }
        } else {
            // User is not logged in, show the normal flow
            startActivity(Intent(this, GetStartedActivity::class.java))
        }
        finish() // close SplashActivity
    }
}
