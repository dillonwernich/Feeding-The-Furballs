package com.dillonwernich.feedingthefurballs

// Import statements for necessary Android components
import android.content.Intent           // Used to create and launch a new activity
import android.os.Bundle               // Bundle is used to pass data between activities
import android.os.Handler              // Handler allows scheduling of delayed tasks
import android.os.Looper               // Looper is used to run a message loop for the main thread
import androidx.appcompat.app.AppCompatActivity  // Base class for activities that use the support library ActionBar features
import androidx.appcompat.app.AppCompatDelegate  // Allows us to control night/day mode in the app

// This is the splash screen class, which extends AppCompatActivity
// It is the first screen that users see when the app is launched, typically used for branding
class Splash_Screen : AppCompatActivity() {

    // Declare a constant for the duration of the splash screen (5 seconds in this case, 5000ms)
    // The splash screen will display for a total of 5 seconds before transitioning to the main activity
    private val splashScreenDuration: Long = 5000

    // Override the onCreate method, which is called when the activity is first created
    // This method is where we initialize the UI components and the behavior of the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force the app to use light mode, disabling any dark mode settings
        // This ensures the splash screen maintains a consistent visual style regardless of user preferences
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the content view of this activity to the splash screen layout (XML)
        // This binds the activity to the splash screen UI defined in XML
        setContentView(R.layout.activity_splash_screen)

        // Use Handler to post a delayed task to the main thread's message queue
        // This task will be executed after 'splashScreenDuration' milliseconds (5 seconds)
        // The handler allows us to delay the transition to the next activity, giving users time to see the splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Create an Intent to start MainActivity once the splash screen duration ends
            // Intents in Android are used to move from one activity to another
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)  // Start the MainActivity

            // Finish the Splash_Screen activity so that when the user presses back,
            // they won't return to the splash screen
            // This is done to ensure that the splash screen is shown only once during app startup
            finish()
        }, splashScreenDuration)  // The delay is specified here (5 seconds)
    }
}
