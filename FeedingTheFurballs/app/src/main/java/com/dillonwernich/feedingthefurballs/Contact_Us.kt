package com.dillonwernich.feedingthefurballs

// Import necessary Android components and libraries
import android.content.Intent             // Used to create and launch new activities (email, browser, phone)
import android.net.Uri                   // Used for handling URIs (URLs, email addresses, phone numbers)
import androidx.appcompat.app.AppCompatActivity  // Base class for activities using ActionBar features
import android.os.Bundle                 // Bundle is used to pass data between activities
import android.widget.ImageButton        // ImageButton for social media icons
import android.widget.TextView           // TextView for displaying and interacting with contact info
import androidx.appcompat.app.AppCompatDelegate  // Used to control the day/night mode

// This class represents the "Contact Us" activity, where users can find contact information and social media links.
class Contact_Us : AppCompatActivity() {

    // Declare UI elements for social media buttons and contact details
    // These include buttons for social media and TextViews for email addresses and phone numbers
    private lateinit var facebookButton: ImageButton
    private lateinit var twitterButton: ImageButton
    private lateinit var tiktokButton: ImageButton
    private lateinit var farrahEmail: TextView       // Farrah's email
    private lateinit var adminEmail: TextView        // Admin email (TechForGood)
    private lateinit var phoneNumber: TextView       // Farrah's phone number

    // The onCreate method is called when the activity is first created
    // This is where we initialize the UI components and define their behavior
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force light mode to ensure consistent visual design across the app
        // This prevents dark mode from altering the appearance of the app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view to the layout file for this activity (activity_contact_us.xml)
        setContentView(R.layout.activity_contact_us)

        // Initialize UI elements by finding them in the layout using their respective IDs
        // This connects the buttons and text views to the actual UI elements in the XML layout
        facebookButton = findViewById(R.id.facebook_button)
        twitterButton = findViewById(R.id.twitter_button)
        tiktokButton = findViewById(R.id.tiktok_button)
        farrahEmail = findViewById(R.id.farrah_email_txt)
        adminEmail = findViewById(R.id.email_details_txt)
        phoneNumber = findViewById(R.id.farrah_number_txt)

        // Set up listeners for social media buttons to open the respective URLs in the browser
        facebookButton.setOnClickListener {
            openUrl("https://www.facebook.com/feedingthefurballs/?fref=ts")  // Open Facebook link
        }

        twitterButton.setOnClickListener {
            openUrl("https://x.com/i/flow/login?redirect_after_login=%2FFeedTheFurballs")  // Open Twitter link
        }

        tiktokButton.setOnClickListener {
            openUrl("https://www.tiktok.com/@farrahmaharajh")  // Open TikTok link
        }

        // Set up listeners for email and phone number TextViews
        // Clicking an email will open the email client, and clicking the phone number will open the dialer

        // Open email client to send an email to Farrah
        farrahEmail.setOnClickListener {
            sendEmail("farrah@feedingthefurballs.org")  // Predefined email address
        }

        // Open email client to send an email to the admin (TechForGood)
        adminEmail.setOnClickListener {
            sendEmail("techforgoodgroup@gmail.com")  // Predefined admin email
        }

        // Open the dialer with Farrah's phone number
        phoneNumber.setOnClickListener {
            callPhoneNumber("+27837936897")  // Predefined phone number
        }
    }

    // Function to open a URL in the browser
    // This is used to open social media links
    private fun openUrl(url: String) {
        // Create an intent to open the specified URL in the default browser
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)  // Launch the browser with the provided URL
    }

    // Function to open the email client with a predefined email address
    // This prepares an email for the user to send when they click on an email address
    private fun sendEmail(email: String) {
        // Create an intent to open the email client with the "mailto" scheme
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")  // Parse the email address into a "mailto" URI
        }
        startActivity(intent)  // Launch the email client with the provided email address
    }

    // Function to open the dialer with a predefined phone number
    // This prepares the phone dialer with the number when the user clicks on the phone number
    private fun callPhoneNumber(number: String) {
        // Create an intent to open the dialer with the "tel" scheme
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        startActivity(intent)  // Launch the dialer with the provided phone number
    }
}
