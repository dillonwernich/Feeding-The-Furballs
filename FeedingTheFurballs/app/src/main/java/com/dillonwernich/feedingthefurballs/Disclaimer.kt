package com.dillonwernich.feedingthefurballs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class Disclaimer : AppCompatActivity() {

    private lateinit var popi: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disclaimer)

        // Force the app to use light mode, disabling any dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Initialize the button for POPIA
        popi = findViewById(R.id.popi_button)

        // Set up an onClick listener to open the POPIA website
        popi.setOnClickListener {
            openBrowserWithUrl("https://popia.co.za/")
        }
    }

    // Helper function to open a URL in the default browser
    private fun openBrowserWithUrl(url: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        } catch (e: Exception) {
            // Show a toast message if the browser fails to open
            Toast.makeText(this, "Failed to open URL!", Toast.LENGTH_SHORT).show()
        }
    }
}
