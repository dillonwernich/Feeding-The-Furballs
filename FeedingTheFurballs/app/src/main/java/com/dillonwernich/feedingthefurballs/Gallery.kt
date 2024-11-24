package com.dillonwernich.feedingthefurballs

// Import necessary Android components and Firebase libraries
import android.app.Dialog                // Used to display images in fullscreen mode in a dialog
import android.app.ProgressDialog        // Used to show a loading dialog while images are being fetched
import android.os.Bundle                 // Bundle is used to pass data between activities
import android.view.ViewGroup            // Used to set the fullscreen image dialog size to match the screen
import android.widget.ImageView          // ImageView widget for displaying images in the gallery
import androidx.appcompat.app.AppCompatActivity  // Base class for activities using modern Android features
import androidx.appcompat.app.AppCompatDelegate  // Allows controlling day/night mode for the activity
import com.bumptech.glide.Glide           // External library to efficiently load images from URLs
import com.google.firebase.storage.FirebaseStorage  // Firebase storage service to retrieve images from cloud
import com.google.firebase.storage.StorageReference // Reference to Firebase storage for accessing image data

// This class represents the gallery screen in the app where images are loaded from Firebase storage and displayed
class Gallery : AppCompatActivity() {

    // Declare an array of ImageViews to display the gallery images
    // These ImageViews are placeholders for the images fetched from Firebase storage
    private lateinit var imageViews: Array<ImageView>

    // Firebase storage reference to the "images" folder in Firebase
    // This is where all images are stored and accessed from. The "images" folder is set up in Firebase storage.
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("images")

    // ProgressDialog to show the user that images are being loaded
    // Used to prevent user interaction while images are being fetched and loaded, enhancing UX
    private lateinit var progressDialog: ProgressDialog

    // The onCreate method is the entry point when this activity is created
    // This method is responsible for setting up the UI, loading images, and configuring the gallery's image views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force light mode for the app to maintain visual consistency across all users
        // This is done to ensure the gallery and other parts of the app always look consistent, regardless of device settings
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view for this activity to the corresponding XML layout (activity_gallery.xml)
        setContentView(R.layout.activity_gallery)

        // Initialize the ProgressDialog to show a loading indicator while images are being fetched
        // This prevents the user from interacting with the screen while data is being loaded
        progressDialog = ProgressDialog(this).apply {
            setMessage("Loading images...")  // Message to inform the user that images are loading
            setCancelable(false)             // Prevents the user from canceling the loading process
        }

        // Initialize the ImageViews by referencing them from the layout
        // These ImageViews will hold and display the images loaded from Firebase storage
        imageViews = arrayOf(
            findViewById(R.id.gallery1),
            findViewById(R.id.gallery2),
            findViewById(R.id.gallery3),
            findViewById(R.id.gallery4),
            findViewById(R.id.gallery5),
            findViewById(R.id.gallery6),
            findViewById(R.id.gallery7),
            findViewById(R.id.gallery8),
            findViewById(R.id.gallery9),
            findViewById(R.id.gallery10),
            findViewById(R.id.gallery11),
            findViewById(R.id.gallery12),
            findViewById(R.id.gallery13),
            findViewById(R.id.gallery14),
            findViewById(R.id.gallery15),
            findViewById(R.id.gallery16),
            findViewById(R.id.gallery17),
            findViewById(R.id.gallery18),
            findViewById(R.id.gallery19),
            findViewById(R.id.gallery20),
            findViewById(R.id.gallery21),
            findViewById(R.id.gallery22),
            findViewById(R.id.gallery23),
            findViewById(R.id.gallery24),
            findViewById(R.id.gallery25),
            findViewById(R.id.gallery26),
            findViewById(R.id.gallery27),
            findViewById(R.id.gallery28),
            findViewById(R.id.gallery29),
            findViewById(R.id.gallery30),
            findViewById(R.id.gallery31),
            findViewById(R.id.gallery32),
            findViewById(R.id.gallery33),
            findViewById(R.id.gallery34),
            findViewById(R.id.gallery35),
            findViewById(R.id.gallery36),
            findViewById(R.id.gallery37),
            findViewById(R.id.gallery38),
            findViewById(R.id.gallery39),
            findViewById(R.id.gallery40),
            findViewById(R.id.gallery41),
            findViewById(R.id.gallery42),
            findViewById(R.id.gallery43),
            findViewById(R.id.gallery44),
            findViewById(R.id.gallery45),
            findViewById(R.id.gallery46),
            findViewById(R.id.gallery47),
            findViewById(R.id.gallery48)
        )

        // Show the ProgressDialog to inform the user that the images are loading
        progressDialog.show()

        // Load the images from Firebase storage into the ImageViews
        // This is where the images are fetched from Firebase and displayed in the ImageViews
        loadImages()

        // Set up click listeners for each ImageView to display images in fullscreen mode when clicked
        // This allows the user to see a larger version of the image when they tap on it
        for (imageView in imageViews) {
            imageView.setOnClickListener {
                showFullScreenImage(imageView)  // Function to display the tapped image in fullscreen
            }
        }
    }

    // Function to load images from Firebase storage
    // This function fetches the images from the Firebase storage and displays them in the gallery's ImageViews
    private fun loadImages() {
        // List all items (images) in the "images" folder of Firebase storage
        storageReference.listAll()
            .addOnSuccessListener { listResult ->
                // Sort the images by their name in descending order (newest first)
                val sortedItems = listResult.items.sortedByDescending { it.name }

                // Ensure that the number of images does not exceed the number of available ImageViews
                // This prevents crashes or out-of-bounds errors when the number of images is more than ImageViews
                val itemCount = sortedItems.size.coerceAtMost(imageViews.size)

                // Loop through the sorted list of image references and load them into the corresponding ImageViews
                for (i in 0 until itemCount) {
                    val imageRef = sortedItems[i]
                    val imageView = imageViews[i]

                    // Get the download URL of the image from Firebase storage and load it into the ImageView
                    imageRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            // Store the image URL in the ImageView's tag for use later in fullscreen mode
                            imageView.tag = uri.toString()

                            // Use Glide to efficiently load the image from the URL into the ImageView
                            // Glide handles caching and performance optimization for loading images
                            Glide.with(this@Gallery)
                                .load(uri)
                                .into(imageView)  // Load the image into the corresponding ImageView
                        }
                        .addOnFailureListener {
                            // Handle the case where an image fails to load (e.g., no internet or missing file)
                            // You can display an error image or log the error here
                        }
                }

                // Dismiss the ProgressDialog once all images have been successfully loaded
                progressDialog.dismiss()
            }
            .addOnFailureListener {
                // Handle Firebase storage listing failure
                // If the app fails to retrieve the images, dismiss the progress dialog and show an error message
                progressDialog.dismiss()  // Dismiss the ProgressDialog even in case of failure
            }
    }

    // Function to display an image in fullscreen mode when an ImageView is clicked
    private fun showFullScreenImage(imageView: ImageView) {
        // Create a new Dialog to show the image in fullscreen
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_image_fullscreen)  // Set the dialog layout (fullscreen image view)

        // Find the ImageView in the dialog where the fullscreen image will be displayed
        val fullScreenImageView: ImageView = dialog.findViewById(R.id.fullscreen_image)

        // Retrieve the image URL that was stored in the ImageView's tag
        val imageUrl = imageView.tag as? String

        // If the image URL is available, load it into the fullscreen ImageView using Glide
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)  // Load the image URL into the fullscreen ImageView
                .into(fullScreenImageView)
        } else {
            // Handle the case where the image URL is not available (e.g., if loading failed)
            // Optionally, you can show an error message or a placeholder image here
        }

        // Set the dialog to match the screen size (fullscreen mode)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Show the dialog, displaying the image in fullscreen mode
        dialog.show()
    }
}
