package com.dillonwernich.feedingthefurballs

// Import necessary Android components and Firebase libraries
import android.app.ProgressDialog        // Used to show a loading dialog during the login process
import android.content.Intent            // Used for navigation between activities (switching screens)
import androidx.appcompat.app.AppCompatActivity  // Base class for activities using modern Android components
import android.os.Bundle                 // Bundle is used to pass data between activities
import android.widget.Button             // Button widget for user interaction (login button)
import android.widget.EditText           // EditText widget to accept text input from the user (email and password)
import android.widget.Toast              // Toast is used to show short pop-up messages to the user
import androidx.appcompat.app.AppCompatDelegate  // Used to control whether the app uses night or day mode
import com.google.firebase.auth.FirebaseAuth  // Firebase Authentication library for login and authentication services

// This class represents the admin login screen for the app
class Admin_Login : AppCompatActivity() {

    // UI elements for the login form: an email field, password field, and login button
    private lateinit var employeeEmail: EditText  // Text field where admin enters their email address
    private lateinit var password: EditText       // Text field where admin enters their password
    private lateinit var loginBtn: Button         // Button to trigger the login process

    // Firebase Authentication instance for handling the login process
    private lateinit var auth: FirebaseAuth

    // ProgressDialog to show a loading indicator during the login process
    private lateinit var progressDialog: ProgressDialog

    // The onCreate method is called when the activity is first created
    // This is where we initialize the UI components and set up Firebase authentication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force light mode to ensure consistent design
        // The app enforces light mode to maintain a consistent appearance across the application.
        // This is especially important if users have different device settings for night mode, which can affect the look and feel of the app.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view to the layout for this activity (activity_admin_login.xml)
        setContentView(R.layout.activity_admin_login)

        // Initialize UI elements by finding their respective views in the layout
        // These views (EditTexts and Button) are used to capture user input and handle interaction
        employeeEmail = findViewById(R.id.admin_email_address)  // Email input field
        password = findViewById(R.id.admin_password)            // Password input field
        loginBtn = findViewById(R.id.login_button)              // Button that will initiate the login process when clicked

        // Initialize Firebase Authentication instance
        // Firebase is used to handle secure authentication without building custom login logic.
        // Firebase provides built-in user management and session handling, reducing the complexity of managing credentials.
        auth = FirebaseAuth.getInstance()

        // Initialize ProgressDialog to show a loading message during the login process
        // This dialog is used to give feedback to the user while their login request is being processed.
        // The dialog prevents users from interacting with the app while waiting for the login attempt to complete, ensuring that they don't accidentally try to log in multiple times.
        progressDialog = ProgressDialog(this).apply {
            setMessage("Logging in...")  // Message displayed in the dialog
            setCancelable(false)         // Prevent user from closing the dialog while login is in progress
        }

        // Set up the login button's click listener
        // When the button is clicked, it will trigger the login process using the input provided by the user (email and password).
        loginBtn.setOnClickListener {
            // Get the email and password entered by the user
            // Both fields are trimmed of any leading or trailing spaces to avoid unnecessary login failures due to accidental spaces.
            val email = employeeEmail.text.toString().trim()
            val userPassword = password.text.toString().trim()

            // Validate email input to ensure the field is not empty
            // If the email field is empty, an error message will be displayed to the user, and the login attempt will be canceled.
            // This prevents unnecessary requests to Firebase with invalid data, improving performance and user experience.
            if (email.isEmpty()) {
                employeeEmail.error = "Please enter an email"  // Display error if no email is entered
                employeeEmail.requestFocus()  // Automatically focus on the email field so the user can correct the mistake
                return@setOnClickListener  // Stop further execution of the login logic
            }

            // Validate password input to ensure the field is not empty
            // Similar to email validation, the password field is checked for empty input. An error message is shown if the field is empty.
            // This ensures that only valid data is sent to Firebase, preventing errors or unnecessary requests.
            if (userPassword.isEmpty()) {
                password.error = "Please enter a password"  // Display error if no password is entered
                password.requestFocus()  // Automatically focus on the password field so the user can correct the mistake
                return@setOnClickListener  // Stop further execution of the login logic
            }

            // Show the ProgressDialog to indicate that the login process is underway
            // This gives the user visual feedback, letting them know that the app is processing their request.
            progressDialog.show()

            // Attempt to sign in with Firebase Authentication using the provided email and password
            // Firebase handles the entire authentication process, including verifying the email and password.
            // The result of the login attempt (successful or failed) will be handled in the `addOnCompleteListener`.
            auth.signInWithEmailAndPassword(email, userPassword)
                .addOnCompleteListener(this) { task ->
                    // Dismiss the progress dialog once the login process is complete
                    progressDialog.dismiss()

                    // If the login attempt was successful, navigate the user to the Admin Dashboard
                    // Successful login allows the admin to access the protected admin section of the app (Admin Dashboard).
                    // Firebase handles user session management, so after login, the user remains authenticated.
                    if (task.isSuccessful) {
                        // Show a success message to the user indicating that the login was successful
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                        // Navigate to the Admin Dashboard activity
                        // This is the next screen where admin-related tasks are managed after a successful login.
                        val intent = Intent(this@Admin_Login, Admin_Dashboard::class.java)
                        startActivity(intent)  // Start the Admin Dashboard activity
                        finish()  // Close the login activity to prevent returning to it after login
                    } else {
                        // If the login attempt failed (wrong credentials, no internet, etc.), display an error message
                        // Failed login is handled gracefully by showing an error message, informing the user of the issue.
                        Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
