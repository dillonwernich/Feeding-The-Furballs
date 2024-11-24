package com.dillonwernich.feedingthefurballs

// Import necessary Android components and Firebase libraries
import android.content.Context             // Provides access to system-level services, like input methods
import android.os.Bundle                   // Used to pass data between activities
import android.view.inputmethod.InputMethodManager  // Provides access to the input method service (keyboard)
import android.widget.ArrayAdapter         // Adapter used for the Spinner to display the list of months
import android.widget.Button               // Button widget for saving data
import android.widget.EditText             // EditText widget for user input fields (monthly donations and goals)
import android.widget.Spinner              // Spinner widget for selecting a month
import android.widget.Toast                // Toast is used to display short pop-up messages to the user
import androidx.appcompat.app.AppCompatActivity  // Base class for activities using ActionBar features
import androidx.appcompat.app.AppCompatDelegate  // Allows controlling the night/day mode
import com.google.firebase.database.DatabaseReference  // Reference to Firebase Realtime Database
import com.google.firebase.database.FirebaseDatabase   // Firebase Realtime Database for storing donation goals
import java.text.SimpleDateFormat            // Used for formatting dates (getting the current month)
import java.util.*                           // Provides utilities like Locale and Date for date manipulation

// This class represents the admin section for setting monthly donation goals and total donations
class Admin_Donation_Goal : AppCompatActivity() {

    // Declare UI elements and Firebase database reference
    private lateinit var monthSpinner: Spinner          // Spinner for selecting the month
    private lateinit var totalMonthlyDonations: EditText  // Input field for total monthly donations
    private lateinit var totalMonthlyGoal: EditText     // Input field for setting the donation goal
    private lateinit var saveButton: Button             // Button to trigger saving the donation goal
    private lateinit var database: DatabaseReference    // Reference to Firebase Realtime Database

    // The onCreate method is called when the activity is first created
    // This is where we initialize the UI components, set up Firebase, and configure event listeners
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force light mode for consistent visual design
        // Light mode ensures that the app looks consistent across devices, regardless of the user's theme settings
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view to the corresponding XML layout (activity_admin_donation_goal.xml)
        setContentView(R.layout.activity_admin_donation_goal)

        // Initialize Firebase Database reference
        // Firebase Realtime Database is used to store and retrieve data such as donation goals and totals
        database = FirebaseDatabase.getInstance().reference

        // Initialize UI elements by finding them in the layout using their respective IDs
        monthSpinner = findViewById(R.id.month_spinner)                 // Spinner for selecting a month
        totalMonthlyDonations = findViewById(R.id.total_monthly_donations)  // Input field for total monthly donations
        totalMonthlyGoal = findViewById(R.id.total_monthly_goal)        // Input field for setting the donation goal
        saveButton = findViewById(R.id.save_button)                     // Button to save the entered data

        // Populate the Spinner with the list of months from resources
        // The Spinner is populated with an array of months defined in the strings.xml file
        val months = resources.getStringArray(R.array.month_spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = adapter  // Attach the adapter to the Spinner

        // Set the current month as the default selection in the Spinner
        // The app automatically selects the current month when the admin opens the screen
        val currentMonth = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())  // Get the current month name
        val monthIndex = months.indexOf(currentMonth)  // Find the index of the current month in the list
        if (monthIndex >= 0) {
            monthSpinner.setSelection(monthIndex)  // Set the Spinner to the current month if it exists in the array
        }

        // Set up the save button click listener
        // When the admin clicks the save button, the data is validated and saved to Firebase
        saveButton.setOnClickListener {
            validateFieldsAndSave()  // Call method to validate inputs and save data on button click
        }
    }

    // Method to validate fields and save donation data to Firebase
    // This method checks if the input fields are filled and then saves the data to Firebase
    private fun validateFieldsAndSave() {
        // Get the selected month and the entered donation data
        val month = monthSpinner.selectedItem.toString()  // Get the selected month from the Spinner
        val monthlyDonations = totalMonthlyDonations.text.toString()  // Get the total donations from the input field
        val monthlyGoal = totalMonthlyGoal.text.toString()  // Get the monthly goal from the input field

        // Validate the monthly donations field (cannot be empty)
        if (monthlyDonations.isEmpty()) {
            totalMonthlyDonations.error = "Please enter the total monthly donations!"  // Show error if donations field is empty
            totalMonthlyDonations.requestFocus()  // Focus on the field to prompt the user to enter data
            return  // Stop execution if validation fails
        }

        // Validate the monthly goal field (cannot be empty)
        if (monthlyGoal.isEmpty()) {
            totalMonthlyGoal.error = "Please enter the monthly goal!"  // Show error if goal field is empty
            totalMonthlyGoal.requestFocus()  // Focus on the field to prompt the user to enter data
            return  // Stop execution if validation fails
        }

        // Hide the keyboard after entering data
        // This improves the user experience by dismissing the on-screen keyboard after the user is done entering data
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(saveButton.windowToken, 0)  // Hide the keyboard when saving data

        // Prepare the data to be saved to Firebase
        // The data is structured as a map, containing the month, total donations, and the goal
        val data = mapOf(
            "month" to month,
            "monthly_donations" to monthlyDonations,
            "monthly_goal" to monthlyGoal
        )

        // Save the donation data under the selected month in Firebase
        database.child("donation_goals").child(month).setValue(data)
            .addOnSuccessListener {
                // If saving is successful, show a success message and clear the input fields
                Toast.makeText(applicationContext, "Goals uploaded successfully!", Toast.LENGTH_SHORT).show()
                totalMonthlyDonations.text.clear()  // Clear the donations input field
                totalMonthlyGoal.text.clear()  // Clear the goal input field
            }
            .addOnFailureListener {
                // If saving fails, show a failure message to the admin
                Toast.makeText(applicationContext, "Failed to upload goals!", Toast.LENGTH_SHORT).show()
            }
    }
}
