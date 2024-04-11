package com.shrey.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// Splash screen activity to display app logo briefly on startup
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create an intent to navigate to the main activity
        val intent = Intent(this, MainActivity::class.java)
        // Start the main activity
        startActivity(intent)
        // Finish the splash screen activity to prevent returning to it when pressing back button
        finish()
    }
}
