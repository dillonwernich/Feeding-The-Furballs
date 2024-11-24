package com.dillonwernich.feedingthefurballs

// Import necessary Android components and libraries
import android.content.Intent            // Used to create and launch new activities
import android.os.Bundle                 // Bundle is used to pass data between activities
import android.util.Log                  // Used for logging debug messages
import android.view.View                 // View class handles the UI elements
import android.widget.AdapterView         // AdapterView is a parent class for Spinner
import android.widget.ArrayAdapter        // ArrayAdapter connects data to a Spinner view
import android.widget.Spinner             // Spinner is a dropdown UI element
import android.widget.Toast               // Toast is used to display brief messages on the screen
import androidx.appcompat.app.AppCompatActivity  // Base class for activities using ActionBar
import androidx.appcompat.app.AppCompatDelegate  // Used to control the day/night mode

// Main activity class for the app, extends AppCompatActivity
class MainActivity : AppCompatActivity() {

    // Declare a Spinner element for the navigation menu
    // The Spinner is used to allow users to select from a list of options (navigation menu)
    private lateinit var selectSpinner: Spinner

    // The onCreate method is called when the activity is first created
    // This is where we initialize the UI and set up the event listeners
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force the app to use light mode, disabling any dark mode
        // This ensures that the app always appears in light mode for a consistent user experience
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view to the main layout (activity_main.xml)
        // This binds the activity to the UI defined in the XML layout file
        setContentView(R.layout.activity_main)

        // Initialize the Spinner view by finding it by its ID in the layout
        // This connects the Spinner object in the code to the actual Spinner in the UI
        selectSpinner = findViewById(R.id.navigation_spinner)

        // Set up the adapter to populate the Spinner with items from the string array resource
        // The adapter acts as a bridge between the Spinner and the data it displays (array of options)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.navigation_spinner,  // Reference to the string array in resources for Spinner items
            android.R.layout.simple_spinner_item  // Default layout for each item in the Spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)  // Dropdown layout
        selectSpinner.adapter = adapter  // Connect the adapter to the Spinner

        // Set the listener for item selection events on the Spinner
        // This listener detects when an item is selected and handles the corresponding action
        selectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Called when an item is selected in the Spinner
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Ensure 'view' is not null before proceeding
                // This check ensures that we only proceed if the UI view exists (prevent potential crashes)
                if (view != null) {
                    // Use a `when` block to determine which item was selected (based on position)
                    // Each position corresponds to a different activity or action to navigate to
                    when (position) {
                        1 -> navigateToActivity(How_Can_You_Help::class.java)  // Go to "How Can You Help" screen
                        2 -> navigateToActivity(Our_Suppawters::class.java)    // Go to "Our Suppawters" screen
                        3 -> navigateToActivity(Gallery::class.java)           // Go to "Gallery" screen
                        4 -> navigateToActivity(Donation_Goal::class.java)     // Go to "Donation Goal" screen
                        5 -> navigateToActivity(Contact_Us::class.java)        // Go to "Contact Us" screen
                        6 -> navigateToActivity(Disclaimer::class.java)        // Go to "Disclaimer" screen
                        7 -> navigateToActivity(Admin_Login::class.java)       // Go to "Admin Login" screen
                        else -> Log.d("MainActivity", "No valid selection made!")  // Log message for invalid selection
                    }
                } else {
                    // Log a message if the view is null (this should not normally happen)
                    Log.d("MainActivity", "View is null")
                }
            }

            // Called when no item is selected; no action is needed here
            // This method is required by the interface but remains empty as no action is needed
            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed when nothing is selected
            }
        }
    }

    // Helper function to navigate to another activity using an Intent
    // This method was coded to reduce redundancy by handling activity transitions in a reusable way
    private fun <T> navigateToActivity(activityClass: Class<T>) {
        try {
            // Create an Intent to start the specified activity
            // Intents are used in Android to navigate between different activities or screens
            val intent = Intent(this@MainActivity, activityClass)
            startActivity(intent)  // Start the activity
        } catch (e: Exception) {
            // If something goes wrong (like activity not found), display a toast message
            // Toast messages provide brief feedback to users for errors or successes
            Toast.makeText(this, "Failed to open activity!", Toast.LENGTH_SHORT).show()
        }
    }

    // Override onResume to reset the spinner when returning to this activity
    // This ensures that when the user navigates back to this activity, the spinner shows the default item
    override fun onResume() {
        super.onResume()
        selectSpinner.setSelection(0)  // Reset spinner to the default (first) item
    }

    // Override onBackPressed to customize behavior when back button is pressed
    // This provides better user experience by resetting the spinner instead of immediately exiting
    override fun onBackPressed() {
        // If an item other than the default is selected, reset the spinner
        if (selectSpinner.selectedItemPosition > 0) {
            selectSpinner.setSelection(0)  // Reset spinner to default (first) option
        } else {
            // If already on the default item, perform the standard back button action (exit or navigate back)
            super.onBackPressed()
        }
    }
}
