package com.dillonwernich.feedingthefurballs

// Import necessary Android components and Firebase libraries
import android.app.ProgressDialog             // Used to show a loading dialog during long-running operations
import android.os.Bundle                       // Bundle is used to pass data between activities
import android.view.View                       // Represents the base class for all UI components
import android.widget.*                        // Includes UI components like Spinner, Button, EditText, and Toast
import androidx.appcompat.app.AppCompatActivity  // Base class for activities using modern Android features
import androidx.appcompat.app.AppCompatDelegate  // Used to control the day/night mode
import com.google.firebase.database.*           // Firebase Realtime Database to store and retrieve donation data

// This class represents the admin panel for managing item donations
class Admin_Item_Donations : AppCompatActivity() {

    // Declare UI elements: Spinner to display user names, EditText fields for donation data, and a delete button
    private lateinit var userNameSpinner: Spinner     // Spinner to select the user's name
    private lateinit var itemDonation: EditText       // EditText to display and update the donation item
    private lateinit var contactNumber: EditText      // EditText to display and update the user's contact number
    private lateinit var emailAddress: EditText       // EditText to display and update the user's email
    private lateinit var deleteRequest: Button        // Button to trigger the deletion of a donation request

    // Firebase database reference to interact with the cloud database
    private lateinit var database: DatabaseReference  // Reference to Firebase Realtime Database

    // ProgressDialog to display a loading indicator during operations
    private lateinit var progressDialog: ProgressDialog  // Dialog to prevent user interaction during long-running operations

    // The onCreate method is called when the activity is first created
    // This is where we initialize the UI components, configure Firebase, and set up event listeners
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force light mode for consistent visual design across the app
        // Light mode ensures that the app looks consistent across devices, regardless of the user's theme settings
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view to the corresponding XML layout (activity_admin_item_donations.xml)
        setContentView(R.layout.activity_admin_item_donations)

        // Initialize UI elements by finding them in the layout using their respective IDs
        userNameSpinner = findViewById(R.id.name_spinner)   // Spinner for selecting a user's name
        itemDonation = findViewById(R.id.item)              // EditText for displaying the donation item
        contactNumber = findViewById(R.id.contact_number)   // EditText for displaying the user's contact number
        emailAddress = findViewById(R.id.email_address)     // EditText for displaying the user's email address
        deleteRequest = findViewById(R.id.delete_item_donation_button)  // Button to delete the selected donation request

        // Initialize Firebase database reference
        // Firebase Realtime Database is used to store and retrieve user donation requests
        database = FirebaseDatabase.getInstance().reference

        // Initialize ProgressDialog to show loading indicators during operations
        // This dialog prevents the user from interacting with the app while an operation is being performed (like fetching or deleting data)
        progressDialog = ProgressDialog(this).apply {
            setCancelable(false)  // Prevents user from dismissing the dialog while an operation is in progress
        }

        // Fetch user names from Firebase and populate the spinner
        fetchUserNamesFromFirebase()  // Populate the Spinner with user names retrieved from Firebase

        // Set up the delete button click listener
        // When clicked, the app will delete the donation request for the selected user
        deleteRequest.setOnClickListener {
            val selectedUserName = userNameSpinner.selectedItem?.toString()  // Get the currently selected user name from the spinner
            if (selectedUserName != null) {
                showProgressDialog("Deleting request...")  // Show a loading dialog while the request is being processed
                deleteRequestFromFirebase(selectedUserName)  // Delete the request for the selected user from Firebase
            } else {
                Toast.makeText(this, "Please select a user first", Toast.LENGTH_SHORT).show()  // Show error message if no user is selected
            }
        }

        // Set spinner item selection listener to fetch and display user details
        // When the admin selects a user from the spinner, the corresponding donation details are fetched from Firebase
        userNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Called when a user is selected from the spinner
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedUserName = parent.getItemAtPosition(position).toString()  // Get the selected user name
                showProgressDialog("Fetching user details...")  // Show a loading dialog while user details are being fetched
                fetchUserDetails(selectedUserName)  // Fetch donation details for the selected user
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action is needed when no user is selected
            }
        }
    }

    // Fetch user names from Firebase and populate the spinner
    private fun fetchUserNamesFromFirebase() {
        // Access the "donations" node in Firebase where user donation requests are stored
        database.child("donations")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                // Called when the data is successfully retrieved from Firebase
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userNames = mutableListOf<String>()  // List to store user names
                    // Loop through each child node (each donation request) and extract the "name" field
                    for (snapshot in dataSnapshot.children) {
                        val name = snapshot.child("name").getValue(String::class.java)  // Retrieve the user's name
                        name?.let { userNames.add(it) }  // Add the name to the list if it's not null
                    }
                    // Populate the spinner with the user names
                    val adapter = ArrayAdapter(this@Admin_Item_Donations, android.R.layout.simple_spinner_item, userNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    userNameSpinner.adapter = adapter  // Set the adapter to display the user names in the spinner
                }

                // Called if there is an error while retrieving the data from Firebase
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@Admin_Item_Donations, "Failed to retrieve user names!", Toast.LENGTH_SHORT).show()  // Show error message
                }
            })
    }

    // Fetch user details (item donation, contact, email) based on the selected user name
    private fun fetchUserDetails(userName: String) {
        // Query Firebase for the donation request that matches the selected user name
        database.child("donations")
            .orderByChild("name")
            .equalTo(userName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                // Called when the data for the selected user is successfully retrieved
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {  // Check if there are any matching results for the user name
                        // Loop through each matching snapshot (should only be one) and extract donation details
                        for (snapshot in dataSnapshot.children) {
                            val selectedItem = snapshot.child("item").getValue(String::class.java)  // Get the donation item
                            val contact = snapshot.child("contact").getValue(String::class.java)  // Get the user's contact number
                            val email = snapshot.child("email").getValue(String::class.java)  // Get the user's email address

                            // Populate the EditText fields with the fetched details
                            itemDonation.setText(selectedItem)
                            contactNumber.setText(contact)
                            emailAddress.setText(email)
                        }
                    } else {
                        Toast.makeText(this@Admin_Item_Donations, "No data found for user!", Toast.LENGTH_SHORT).show()  // Show error if no data found
                    }
                    hideProgressDialog()  // Hide the loading dialog after data is retrieved
                }

                // Called if there is an error while fetching the user's details
                override fun onCancelled(databaseError: DatabaseError) {
                    hideProgressDialog()  // Hide the dialog even if an error occurs
                    Toast.makeText(this@Admin_Item_Donations, "Failed to retrieve user details!", Toast.LENGTH_SHORT).show()  // Show error message
                }
            })
    }

    // Delete donation request for the selected user from Firebase
    private fun deleteRequestFromFirebase(userName: String) {
        // Query Firebase for the donation request that matches the selected user name
        database.child("donations")
            .orderByChild("name")
            .equalTo(userName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                // Called when the data for the selected user is successfully retrieved
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Loop through each matching snapshot and remove the donation request from Firebase
                    for (snapshot in dataSnapshot.children) {
                        snapshot.ref.removeValue()
                            .addOnSuccessListener {
                                hideProgressDialog()  // Hide the loading dialog after successful deletion
                                Toast.makeText(this@Admin_Item_Donations, "Request deleted successfully!", Toast.LENGTH_SHORT).show()  // Show success message

                                // Clear the input fields after deletion
                                itemDonation.setText("")
                                contactNumber.setText("")
                                emailAddress.setText("")

                                // Refresh the spinner with the updated user list
                                fetchUserNamesFromFirebase()
                            }
                            .addOnFailureListener { e ->
                                hideProgressDialog()  // Hide the dialog even if an error occurs
                                Toast.makeText(this@Admin_Item_Donations, "Failed to delete request!", Toast.LENGTH_SHORT).show()  // Show error message
                            }
                    }
                }

                // Called if there is an error while trying to delete the donation request
                override fun onCancelled(databaseError: DatabaseError) {
                    hideProgressDialog()  // Hide the dialog even if an error occurs
                    Toast.makeText(this@Admin_Item_Donations, "Failed to delete request!", Toast.LENGTH_SHORT).show()  // Show error message
                }
            })
    }

    // Show progress dialog with a custom message
    // This prevents the user from interacting with the app while an operation is in progress
    private fun showProgressDialog(message: String) {
        progressDialog.setMessage(message)  // Set the message for the dialog
        progressDialog.show()  // Display the dialog to the user
    }

    // Hide the progress dialog
    // This method is called after an operation is complete (success or failure)
    private fun hideProgressDialog() {
        if (progressDialog.isShowing) {  // Check if the dialog is currently visible
            progressDialog.dismiss()  // Dismiss the dialog and allow user interaction again
        }
    }
}
