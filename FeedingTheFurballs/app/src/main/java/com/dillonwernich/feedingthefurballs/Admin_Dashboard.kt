package com.dillonwernich.feedingthefurballs

// Import necessary Android components and libraries
import android.Manifest                // Provides access to constant values representing Android permissions
import android.content.Intent          // Used for navigation between activities
import android.content.pm.PackageManager // Allows checking the status of permissions on the device
import android.os.Build                // Used to check the current version of Android (API level)
import android.os.Bundle               // Bundle is used to pass data between activities
import android.widget.Button           // Button widget for user interaction (clicking to manage options)
import android.widget.Toast            // Toast is used to display brief messages on the screen
import androidx.appcompat.app.AppCompatActivity  // Base class for activities using ActionBar features
import androidx.appcompat.app.AppCompatDelegate  // Used to control the day/night mode
import androidx.core.app.ActivityCompat  // Helps in requesting permissions at runtime
import androidx.core.content.ContextCompat  // Provides access to system-level services like permissions

// This class represents the admin dashboard where admins can manage the gallery, donations, and donation goals
class Admin_Dashboard : AppCompatActivity() {

    // Declare buttons for managing gallery, donations, and donation goals
    // These buttons will navigate the admin to different sections of the admin dashboard
    private lateinit var manageGallery: Button
    private lateinit var manageDonations: Button
    private lateinit var manageDonationGoal: Button

    // Request code for storage permission (used to identify permission request result)
    private val READ_MEDIA_IMAGES_REQUEST_CODE = 1001  // Constant to track permission request result

    // The onCreate method is called when the activity is first created
    // This is where we initialize the UI components and define their behavior
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force light mode to ensure consistent visual design
        // Light mode is enforced to maintain visual consistency across the app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view to the layout file for this activity (activity_admin_dashboard.xml)
        setContentView(R.layout.activity_admin_dashboard)

        // Initialize buttons by finding them in the layout using their respective IDs
        // This connects the buttons in the layout to the variables in the code
        manageGallery = findViewById(R.id.manage_gallery_button)
        manageDonations = findViewById(R.id.manage_item_donations_button)
        manageDonationGoal = findViewById(R.id.manage_monthly_goal_button)

        // Set click listener for the "Manage Gallery" button
        // Clicking this button checks if the app has the necessary permission to access storage for managing the gallery
        manageGallery.setOnClickListener {
            checkStoragePermissionAndOpenGallery()  // Check for permission and open the gallery if granted
        }

        // Set click listener for the "Manage Donations" button
        // Clicking this button will navigate the user to the Admin_Item_Donations activity
        manageDonations.setOnClickListener {
            val intent = Intent(this, Admin_Item_Donations::class.java)
            startActivity(intent)  // Start the Admin_Item_Donations activity
        }

        // Set click listener for the "Manage Donation Goal" button
        // Clicking this button will navigate the user to the Admin_Donation_Goal activity
        manageDonationGoal.setOnClickListener {
            val intent = Intent(this, Admin_Donation_Goal::class.java)
            startActivity(intent)  // Start the Admin_Donation_Goal activity
        }
    }

    // Function to check storage permission and open the gallery activity if permission is granted
    // If permission is not granted, it will prompt the user to grant the necessary permission
    private fun checkStoragePermissionAndOpenGallery() {
        if (isStoragePermissionGranted()) {
            // If the storage permission is already granted, proceed to open the gallery
            openGallery()  // Open the gallery if permission is available
        } else {
            // If permission is not granted, request the necessary storage permission from the user
            requestStoragePermission()  // Ask the user for permission to access the gallery
        }
    }

    // Function to check if storage permission is granted
    // Returns true if the permission is granted, false otherwise
    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ (API level 33), the permission used for media access is READ_MEDIA_IMAGES
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For devices below Android 13, the permission used is READ_EXTERNAL_STORAGE
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Function to request storage permission from the user
    // Requests the correct permission based on the Android version the device is running
    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ (API level 33), request the permission to access media images
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),  // Permission for media access
                READ_MEDIA_IMAGES_REQUEST_CODE  // Request code to handle the result
            )
        } else {
            // For devices below Android 13, request the permission to read external storage
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),  // Permission for external storage access
                READ_MEDIA_IMAGES_REQUEST_CODE  // Request code to handle the result
            )
        }
    }

    // Handle the result of the permission request
    // This function is called when the user responds to the permission request dialog
    override fun onRequestPermissionsResult(
        requestCode: Int,  // The request code used to identify the permission request
        permissions: Array<out String>,  // The requested permissions
        grantResults: IntArray  // The result (granted or denied)
    ) {
        // Check if the request code matches the storage permission request
        if (requestCode == READ_MEDIA_IMAGES_REQUEST_CODE) {
            // If the permission was granted, proceed to open the gallery
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()  // Open the gallery if the permission is granted
            } else {
                // If the permission was denied, show a Toast message to inform the user
                Toast.makeText(
                    this,
                    "Permission denied to access the gallery.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // If the request code doesn't match, pass it to the superclass
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // Function to open the Admin_Gallery activity
    // This will allow the admin to manage images in the gallery section
    private fun openGallery() {
        val intent = Intent(this, Admin_Gallery::class.java)
        startActivity(intent)  // Start the Admin_Gallery activity
    }
}
