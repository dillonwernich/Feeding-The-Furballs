package com.dillonwernich.feedingthefurballs

// Import necessary Android components and Firebase libraries
import android.app.Activity               // Represents the base class for all activities in the app
import android.app.ProgressDialog         // Used to show a loading dialog during actions like uploading or deleting
import android.content.Intent             // Used for launching the gallery for image selection
import android.net.Uri                    // Represents a uniform resource identifier (e.g., pointing to the selected image)
import android.os.Bundle                  // Used to pass data between activities
import android.provider.MediaStore        // Provides access to the device's media store (for selecting images)
import android.view.View                  // Represents the base class for all UI components
import android.widget.*                   // Includes UI components like Button, ImageView, Spinner, Toast
import androidx.appcompat.app.AppCompatActivity  // Base class for activities using modern Android features
import androidx.appcompat.app.AppCompatDelegate  // Allows controlling day/night mode for the activity
import com.bumptech.glide.Glide           // External library to efficiently load images from URLs
import com.google.firebase.storage.FirebaseStorage  // Firebase storage service to upload, download, and delete images
import com.google.firebase.storage.StorageReference // Reference to Firebase storage for image handling
import java.util.*                        // Provides utilities like UUID for generating unique image names

// This class represents the admin gallery screen where the admin can upload, view, and delete images in the gallery
class Admin_Gallery : AppCompatActivity() {

    // Declare UI elements: Button for image selection, ImageView to display selected image, Spinner for image selection from Firebase, and a Button to delete images
    private lateinit var chooseImageBtn: Button      // Button to choose an image from the device gallery
    private lateinit var imageView: ImageView        // ImageView to display the selected or uploaded image
    private lateinit var deleteBtn: Button           // Button to delete the selected image from Firebase
    private lateinit var imageSpinner: Spinner       // Spinner to list the names of images stored in Firebase for selection

    // Firebase storage reference to interact with Firebase storage (for uploading, downloading, and deleting images)
    private lateinit var storageReference: StorageReference

    // Variables for storing the selected image's URI and name (used for uploading and displaying)
    private var selectedImageUri: Uri? = null        // Stores the URI of the selected image from the gallery
    private var selectedImageName: String? = null    // Stores the name of the selected image (extracted from URI or Firebase)

    // Request code for selecting an image from the gallery (used to track the result of the gallery intent)
    private val GALLERY_REQUEST_CODE = 200           // Arbitrary number to identify the gallery selection request

    // ProgressDialog to indicate progress during actions like uploading or deleting images
    private lateinit var progressDialog: ProgressDialog

    // onCreate method to initialize the activity, UI components, and Firebase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force light mode for consistent visual design across the app
        // Ensures the app uses a light theme regardless of the device's theme settings
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view to the corresponding XML layout (activity_admin_gallery.xml)
        setContentView(R.layout.activity_admin_gallery)

        // Initialize UI elements by finding their respective views in the layout file
        chooseImageBtn = findViewById(R.id.add_image_button)      // Button for choosing an image from the gallery
        imageView = findViewById(R.id.admin_display_imageView)    // ImageView for displaying the selected or uploaded image
        deleteBtn = findViewById(R.id.delete_image_button)        // Button for deleting the selected image from Firebase
        imageSpinner = findViewById(R.id.image_name_spinner)      // Spinner for displaying image names fetched from Firebase

        // Initialize Firebase storage reference to interact with the cloud storage (e.g., upload, delete, and retrieve images)
        storageReference = FirebaseStorage.getInstance().reference

        // Initialize the ProgressDialog for showing progress while images are being uploaded, deleted, or fetched
        progressDialog = ProgressDialog(this).apply {
            setMessage("Processing...")  // Set the message for the dialog
            setCancelable(false)         // Prevent the user from canceling the operation
        }

        // Set up click listener for the button that opens the gallery for image selection
        chooseImageBtn.setOnClickListener {
            choosePhotoFromGallery()  // Opens the device's gallery to choose an image
        }

        // Set up click listener for the delete button
        // Deletes the currently selected image (from the spinner) if an image is selected
        deleteBtn.setOnClickListener {
            selectedImageName?.let {
                showProgressDialog("Deleting image...")  // Show a progress dialog while the image is being deleted
                deleteImageFromFirebase(it)              // Call function to delete the image from Firebase
            } ?: Toast.makeText(this, "No image selected to delete!", Toast.LENGTH_SHORT).show()  // Show error if no image is selected
        }

        // Fetch available image names from Firebase storage and populate the spinner
        fetchImageNamesFromFirebase()

        // Set listener for spinner item selection
        // When a user selects an image from the spinner, it loads and displays that image
        imageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedImageName = parent.getItemAtPosition(position).toString()  // Get the selected image name from the spinner
                displayImageFromFirebase(selectedImageName!!)                     // Display the selected image
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action is needed when nothing is selected
            }
        }
    }

    // Function to open the device's gallery and allow the user to select an image
    // This function creates an intent to open the gallery and filter for image files only
    private fun choosePhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"  // Set the intent to only show images in the gallery
        }
        startActivityForResult(intent, GALLERY_REQUEST_CODE)  // Start the activity and wait for the user to select an image
    }

    // Handle the result of the gallery selection when the user selects an image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the user successfully selected an image and the result is OK
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {
            data?.data?.let { imageUri ->  // Get the URI of the selected image
                selectedImageUri = imageUri  // Store the URI of the selected image
                imageView.setImageURI(imageUri)  // Display the selected image in the ImageView

                // Extract the filename of the selected image from the URI
                val cursor = contentResolver.query(imageUri, null, null, null, null)  // Query the media store for image metadata
                cursor?.let {
                    if (it.moveToFirst()) {
                        val displayNameIndex = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)  // Get the index of the display name column
                        selectedImageName = it.getString(displayNameIndex)  // Retrieve the actual image name
                    }
                    it.close()  // Close the cursor to free resources
                }

                // Automatically upload the selected image to Firebase storage
                showProgressDialog("Uploading image...")  // Show a progress dialog while the image is being uploaded
                uploadImageToFirebase(imageUri, selectedImageName ?: UUID.randomUUID().toString())  // Upload the image with the selected or generated filename
            } ?: Toast.makeText(this, "Failed to get image from gallery!", Toast.LENGTH_SHORT).show()  // Show error if no image was selected
        }
    }

    // Function to upload the selected image to Firebase storage
    // Takes the image URI and file name, and uploads the image to Firebase
    private fun uploadImageToFirebase(imageUri: Uri, fileName: String) {
        val ref = storageReference.child("images/$fileName")  // Create a reference to the image file in Firebase storage

        ref.putFile(imageUri)
            .addOnSuccessListener {
                hideProgressDialog()  // Hide the progress dialog after the upload is complete
                Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()  // Show success message
                fetchImageNamesFromFirebase()  // Refresh the image list in the spinner
            }
            .addOnFailureListener {
                hideProgressDialog()  // Hide the progress dialog even if the upload fails
                Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show()  // Show error message
            }
    }

    // Function to fetch the names of images stored in Firebase and populate the spinner with those names
    private fun fetchImageNamesFromFirebase() {
        val imagesRef = storageReference.child("images")  // Reference to the "images" folder in Firebase storage
        imagesRef.listAll()
            .addOnSuccessListener { listResult ->
                val imageNames = listResult.items.map { it.name }  // Get the name of each image in the folder
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, imageNames)  // Create an adapter for the spinner
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)  // Set the dropdown style for the spinner
                imageSpinner.adapter = adapter  // Set the adapter for the spinner to display image names
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch image names!", Toast.LENGTH_SHORT).show()  // Show error message if the image names couldn't be fetched
            }
    }

    // Function to display the selected image from Firebase storage in the ImageView
    private fun displayImageFromFirebase(imageName: String) {
        val imageRef = storageReference.child("images/$imageName")  // Reference to the selected image file in Firebase storage
        imageRef.downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(this).load(uri).into(imageView)  // Use Glide to load the image into the ImageView
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load image!", Toast.LENGTH_SHORT).show()  // Show error message if the image couldn't be loaded
            }
    }

    // Function to delete the selected image from Firebase storage
    private fun deleteImageFromFirebase(imageName: String) {
        val imageRef = storageReference.child("images/$imageName")  // Reference to the image file to be deleted in Firebase storage
        imageRef.delete()
            .addOnSuccessListener {
                hideProgressDialog()  // Hide the progress dialog after the image is deleted
                Toast.makeText(this, "Image deleted successfully!", Toast.LENGTH_SHORT).show()  // Show success message
                fetchImageNamesFromFirebase()  // Refresh the image list in the spinner
                imageView.setImageResource(R.drawable.placeholder)  // Reset the ImageView to a placeholder image after deletion
            }
            .addOnFailureListener {
                hideProgressDialog()  // Hide the progress dialog even if the deletion fails
                Toast.makeText(this, "Failed to delete image!", Toast.LENGTH_SHORT).show()  // Show error message
            }
    }

    // Helper function to show the ProgressDialog with a custom message
    private fun showProgressDialog(message: String) {
        progressDialog.setMessage(message)  // Set the message for the ProgressDialog
        progressDialog.show()  // Display the ProgressDialog
    }

    // Helper function to hide the ProgressDialog when an action is completed
    private fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()  // Dismiss the ProgressDialog
        }
    }
}
