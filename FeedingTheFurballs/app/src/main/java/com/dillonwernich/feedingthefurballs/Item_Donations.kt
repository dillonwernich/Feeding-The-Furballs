package com.dillonwernich.feedingthefurballs

// Import necessary Android components and Firebase libraries
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import java.util.regex.Pattern

// This class handles the "Item Donations" screen where users can select an item to donate and provide their contact details.
class Item_Donations : AppCompatActivity() {

    // Firebase database reference to store the donation requests
    private lateinit var database: DatabaseReference

    // Declare UI elements for the activity
    // These are the fields and buttons that allow users to interact with the app
    private lateinit var generalItemsSpinner: Spinner  // Spinner for general donation items (dropdown)
    private lateinit var otherItemsSpinner: Spinner    // Spinner for other donation items (dropdown)
    private lateinit var nameSurnameField: EditText    // Input field for the user's name and surname
    private lateinit var selectedItemField: EditText   // Field displaying the selected item (non-editable)
    private lateinit var contactNumberField: EditText  // Input field for the user's contact number
    private lateinit var emailAddressField: EditText   // Input field for the user's email address
    private lateinit var sendRequestButton: Button     // Button to submit the donation request

    // The onCreate method is called when the activity is first created
    // This is where we initialize the UI components, define their behavior, and set up interactions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force light mode to maintain consistent visual design
        // The app disables dark mode to ensure the interface remains clear and readable for all users
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view to the XML layout associated with this activity
        setContentView(R.layout.activity_item_donations)

        // Initialize Firebase database reference
        // This will allow saving donation data to the "donations" node in Firebase
        database = FirebaseDatabase.getInstance().reference

        // Initialize UI components by finding their respective views in the layout
        generalItemsSpinner = findViewById(R.id.general_items_spinner)
        otherItemsSpinner = findViewById(R.id.other_items_spinner)
        nameSurnameField = findViewById(R.id.name_and_surname)
        selectedItemField = findViewById(R.id.your_selected_item)
        contactNumberField = findViewById(R.id.contact_number)
        emailAddressField = findViewById(R.id.email_address)
        sendRequestButton = findViewById(R.id.send_request_button)

        // Disable manual input for the selected item field
        // The selected item will be determined by the spinners, so the field is not manually editable
        selectedItemField.isFocusable = false
        selectedItemField.isClickable = false

        // Set up adapters for the general items and other items spinners
        // These adapters will populate the dropdowns with the available options from string resources
        setupSpinnerAdapters()

        // Set up listeners for item selection in the spinners
        // These listeners will detect when the user selects an item and update the selectedItemField
        setupSpinnerListeners()

        // Handle the click event on the "Send Request" button
        // When clicked, the form is validated before submitting the request to Firebase
        sendRequestButton.setOnClickListener {
            // If the form is valid, save the data to Firebase
            if (validateForm()) {
                saveDataToFirebase()  // Save data only if all inputs are valid
            }
        }
    }

    // Set up adapters for the spinners to display available donation items
    // Adapters connect the spinner to the array of items defined in resources
    private fun setupSpinnerAdapters() {
        // Adapter for the general items spinner (populated from general_items string array)
        val generalAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.general_items,  // Resource array of general items
            android.R.layout.simple_spinner_item  // Layout for spinner items
        )
        generalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        generalItemsSpinner.adapter = generalAdapter  // Attach adapter to spinner

        // Adapter for the other items spinner (populated from other_items string array)
        val otherAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.other_items,  // Resource array of other items
            android.R.layout.simple_spinner_item  // Layout for spinner items
        )
        otherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        otherItemsSpinner.adapter = otherAdapter  // Attach adapter to spinner
    }

    // Set up listeners for item selection in both spinners
    // When an item is selected, the selectedItemField is updated and the other spinner is reset
    private fun setupSpinnerListeners() {
        // Listener for general items spinner
        generalItemsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Called when an item is selected from the general items spinner
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {  // Ignore if no valid item is selected
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    selectedItemField.setText(selectedItem)  // Update the selected item field

                    // Reset the other items spinner to its default position
                    otherItemsSpinner.setSelection(0)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed when nothing is selected
            }
        }

        // Listener for other items spinner
        otherItemsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Called when an item is selected from the other items spinner
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {  // Ignore if no valid item is selected
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    selectedItemField.setText(selectedItem)  // Update the selected item field

                    // Reset the general items spinner to its default position
                    generalItemsSpinner.setSelection(0)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed when nothing is selected
            }
        }
    }

    // Validate the form fields to ensure all required inputs are correct
    // If validation fails, error messages will be displayed next to the invalid fields
    private fun validateForm(): Boolean {
        val nameSurname = nameSurnameField.text.toString().trim()  // Get and trim the name field
        val selectedItem = selectedItemField.text.toString().trim()  // Get the selected item
        val contactNumber = contactNumberField.text.toString().trim()  // Get and trim contact number
        val emailAddress = emailAddressField.text.toString().trim()  // Get and trim email address

        var isValid = true  // Track whether the form is valid

        // Validate the name and surname field (must not be empty)
        if (nameSurname.isEmpty()) {
            nameSurnameField.error = "Name and surname are required"  // Display error message
            isValid = false
        }

        // Validate that an item has been selected
        if (selectedItem.isEmpty() || selectedItem == "Please Select an Item") {
            selectedItemField.error = "Please select an item"  // Display error message
            isValid = false
        }

        // Validate the contact number field (must not be empty and must be exactly 10 digits)
        if (contactNumber.isEmpty()) {
            contactNumberField.error = "Contact number is required"  // Display error message
            isValid = false
        } else if (contactNumber.length != 10) {
            contactNumberField.error = "Invalid contact number"  // Display error message for incorrect length
            isValid = false
        }

        // Validate the email field (must not be empty and must match a valid email pattern)
        if (emailAddress.isEmpty()) {
            emailAddressField.error = "Email address is required"  // Display error message
            isValid = false
        } else {
            // Basic regex pattern to check email validity
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            if (!Pattern.matches(emailPattern, emailAddress)) {
                emailAddressField.error = "Invalid email address"  // Display error message for invalid email
                isValid = false
            }
        }

        return isValid  // Return whether the form is valid or not
    }

    // Save the validated data to Firebase
    // This method stores the user's donation request in the Firebase database
    private fun saveDataToFirebase() {
        val nameSurname = nameSurnameField.text.toString().trim()  // Retrieve user inputs
        val selectedItem = selectedItemField.text.toString().trim()
        val contactNumber = contactNumberField.text.toString().trim()
        val emailAddress = emailAddressField.text.toString().trim()

        // Create a DonationRequest object with the user's input data
        val donationRequest = DonationRequest(nameSurname, selectedItem, contactNumber, emailAddress)

        // Generate a unique ID for the donation request
        val requestId = database.child("donations").push().key
        if (requestId != null) {
            // Save the donation request data under the generated ID
            database.child("donations").child(requestId).setValue(donationRequest)
                .addOnSuccessListener {
                    // Show success message after data is saved successfully
                    Toast.makeText(this, "Request sent successfully!", Toast.LENGTH_SHORT).show()
                    showConfirmationDialog()  // Show confirmation dialog to the user
                    resetFields()  // Clear all fields after successful submission
                }
                .addOnFailureListener {
                    // Handle any failure in saving data (e.g., network issues)
                    Toast.makeText(this, "Failed to send request!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Show a confirmation dialog to the user after a successful request submission
    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)  // Create an AlertDialog builder
        builder.setTitle("Confirmation Message")  // Set the title of the dialog
        builder.setMessage("Farrah from Feeding The Furballs will be in contact with you shortly.")  // Set message
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }  // Add OK button to dismiss the dialog

        val dialog = builder.create()  // Create and show the dialog
        dialog.show()
    }

    // Reset all input fields and spinners after the request is sent successfully
    private fun resetFields() {
        nameSurnameField.text.clear()  // Clear name and surname field
        selectedItemField.text.clear()  // Clear selected item field
        contactNumberField.text.clear()  // Clear contact number field
        emailAddressField.text.clear()  // Clear email field
        generalItemsSpinner.setSelection(0)  // Reset general items spinner to default
        otherItemsSpinner.setSelection(0)  // Reset other items spinner to default
    }

    // Data class to represent a donation request
    // This is used to structure the donation data before saving it to Firebase
    data class DonationRequest(
        val name: String,   // Name of the donor
        val item: String,   // Item being donated
        val contact: String,  // Donor's contact number
        val email: String   // Donor's email address
    )
}
