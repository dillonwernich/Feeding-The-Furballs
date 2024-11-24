package com.dillonwernich.feedingthefurballs

// Import necessary Android components and libraries
import android.content.Intent            // Used to create and launch a new activity or action
import android.net.Uri                  // Used for handling URIs (like URLs for browser)
import androidx.appcompat.app.AppCompatActivity  // Base class for activities using ActionBar features
import android.os.Bundle                // Bundle is used to pass data between activities
import android.widget.ImageButton       // ImageButton widget for clickable social media icons
import androidx.appcompat.app.AppCompatDelegate  // Used to control the day/night mode

// This class represents the "Our Suppawters" activity, which lists social media buttons for user interaction
// Users can click on these buttons to visit the social media pages of Feeding the Furballs
class Our_Suppawters : AppCompatActivity() {

    // Declare UI elements for social media buttons (Facebook, Twitter, TikTok)
    // These buttons provide direct access to social media pages
    private lateinit var facebookButton: ImageButton
    private lateinit var twitterButton: ImageButton
    private lateinit var tiktokButton: ImageButton

    // The onCreate method is called when the activity is first created
    // This is where we initialize the UI components and define their behavior
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force light mode to ensure the app uses a consistent visual theme
        // Light mode is enforced across the app to maintain readability and consistent design
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view to the layout file associated with this activity
        // This binds the activity to the UI defined in activity_our_suppawters.xml
        setContentView(R.layout.activity_our_suppawters)

        // Initialize buttons by finding them in the layout using their respective IDs
        // This connects the button variables to the actual ImageButton UI elements in the XML layout
        facebookButton = findViewById(R.id.facebook_button)
        twitterButton = findViewById(R.id.twitter_button)
        tiktokButton = findViewById(R.id.tiktok_button)

        // Set up click listener for the Facebook button
        // When the button is clicked, it opens the Feeding the Furballs Facebook page in a browser
        facebookButton.setOnClickListener {
            openUrl("https://www.facebook.com/feedingthefurballs/?fref=ts")  // Open Facebook link
        }

        // Set up click listener for the Twitter button
        // When the button is clicked, it opens the Feeding the Furballs Twitter page in a browser
        twitterButton.setOnClickListener {
            openUrl("https://x.com/i/flow/login?redirect_after_login=%2FFeedTheFurballs")  // Open Twitter link
        }

        // Set up click listener for the TikTok button
        // When the button is clicked, it opens the Feeding the Furballs TikTok page in a browser
        tiktokButton.setOnClickListener {
            openUrl("https://www.tiktok.com/@farrahmaharajh")  // Open TikTok link
        }
    }

    // Helper function to open a URL in the browser
    // This function simplifies the process of opening different URLs, reducing repetition in code
    private fun openUrl(url: String) {
        // Create an Intent to view the provided URL in a browser
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)  // Parse the string URL into a URI format that the browser can open
        }
        // Start the activity to open the URL in the default web browser
        startActivity(intent)
    }
}
