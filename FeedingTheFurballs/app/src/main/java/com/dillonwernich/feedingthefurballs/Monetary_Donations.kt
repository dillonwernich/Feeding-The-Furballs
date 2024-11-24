package com.dillonwernich.feedingthefurballs

// Import necessary Android components and libraries
import android.content.Intent             // Used to create and launch a new activity or action
import android.net.Uri                   // Used for handling URIs (like URLs for browser)
import androidx.appcompat.app.AppCompatActivity  // Base class for activities using ActionBar features
import android.os.Bundle                 // Bundle is used to pass data between activities
import android.widget.Button             // Button widget for the UI elements
import androidx.appcompat.app.AppCompatDelegate  // Used to control the day/night mode

// This class represents the "Monetary Donations" activity, extending AppCompatActivity
// It provides users with options for different donation methods (debit order and Zapper)
class Monetary_Donations : AppCompatActivity() {

    // Declare buttons for different donation methods
    // These buttons allow users to access either a debit order form or Zapper for payments
    private lateinit var debitOrderFormButton: Button
    private lateinit var zapperButton: Button

    // The onCreate method is called when the activity is first created
    // This is where the UI components are initialized and behaviors are defined
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force light mode
        // The light mode is enforced to ensure that the app has a consistent appearance,
        // regardless of the device's current theme settings (dark mode is disabled)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view to the corresponding XML layout (activity_monetary_donations.xml)
        // This binds the activity to the UI defined in the layout XML file
        setContentView(R.layout.activity_monetary_donations)

        // Initialize buttons by finding them in the layout using their respective IDs
        // This connects the button variables to the actual buttons in the XML layout
        debitOrderFormButton = findViewById(R.id.debit_order_form_button)
        zapperButton = findViewById(R.id.zapper_button)

        // Set up click listeners for each button
        // This defines what happens when each button is clicked

        // When the debit order form button is clicked, it opens the provided URL in a browser
        debitOrderFormButton.setOnClickListener {
            openUrl("https://www.feedingthefurballs.org/payment-mandate.php")  // Open the debit order form link
        }

        // When the Zapper button is clicked, it opens the provided Zapper payment URL in a browser
        zapperButton.setOnClickListener {
            openUrl("https://www.zapper.com/payWithZapper/?qr=http%3A%2F%2F2.zap.pe%3Ft%3D8%26i%3D18686%3A16856%3A7%5B34%7C10.00-20.00-50.00-1%7C15%2C61%3A10%5B39%7CZAR%2C38%7CFeeding%20the%20Furballs")  // Open the Zapper payment link
        }
    }

    // Helper function to open a URL in the browser
    // This method simplifies the process of opening URLs from button clicks, keeping the code DRY (Don't Repeat Yourself)
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)  // Parse the URL into a URI format that can be opened by a browser
        }
        startActivity(intent)  // Launch the browser to open the specified URL
    }
}
