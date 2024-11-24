package com.dillonwernich.feedingthefurballs

// Import statements for necessary Android components
import android.content.Intent             // Used to create and launch a new activity
import androidx.appcompat.app.AppCompatActivity  // Base class for activities with support for modern Android features
import android.os.Bundle                 // Bundle is used to pass data between activities
import android.widget.Button             // Button widget for the UI elements
import androidx.appcompat.app.AppCompatDelegate  // Allows controlling night/day mode in the app

// This class represents the "How Can You Help" activity, extending AppCompatActivity
// This activity provides users with options for different types of donations they can make
class How_Can_You_Help : AppCompatActivity() {

    // Declare buttons for monetary and item donations
    // These buttons will allow users to choose between monetary or item-based donations
    private lateinit var monetaryDonations: Button
    private lateinit var itemDonations: Button

    // The onCreate method is called when the activity is first created
    // This is where we initialize the UI components and define button click behaviors
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force light mode
        // The light mode is enforced to ensure a consistent look and feel in the app's design
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view for this activity to the corresponding layout XML
        // This binds the activity to the UI defined in activity_how_can_you_help.xml
        setContentView(R.layout.activity_how_can_you_help)

        // Initialize the buttons by finding them in the layout using their respective IDs
        // This connects the Button variables to the actual Button UI elements in the XML layout
        monetaryDonations = findViewById(R.id.monetary_donations_button)
        itemDonations = findViewById(R.id.item_donations_button)

        // Set up click listener for the monetary donations button
        // This listener will trigger when the user clicks on the button
        monetaryDonations.setOnClickListener {
            // Create an Intent to start the Monetary_Donations activity when the button is clicked
            // Intents are used in Android to move between different activities (screens)
            val intent = Intent(this, Monetary_Donations::class.java)
            startActivity(intent)  // Start the Monetary_Donations activity
        }

        // Set up click listener for the item donations button
        // This listener will trigger when the user clicks on the button
        itemDonations.setOnClickListener {
            // Create an Intent to start the Item_Donations activity when the button is clicked
            // The user will be redirected to a new screen where they can donate items
            val intent = Intent(this, Item_Donations::class.java)
            startActivity(intent)  // Start the Item_Donations activity
        }
    }
}
