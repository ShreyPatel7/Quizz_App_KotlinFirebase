package com.shrey.project

import android.content.Intent // Importing Intent class for launching activities
import androidx.appcompat.app.AppCompatActivity // Importing AppCompatActivity class for creating activity
import android.os.Bundle // Importing Bundle class for handling saved instance state
import android.view.View // Importing View class for accessing UI elements
import com.shrey.project.databinding.ActivitySignUpBinding // Importing ActivitySignUpBinding class for view binding
import com.google.android.material.snackbar.Snackbar // Importing Snackbar class for displaying snack bar messages
import com.google.firebase.auth.FirebaseAuth // Importing FirebaseAuth class for Firebase authentication
import com.google.firebase.database.* // Importing classes for Firebase Realtime Database

// Activity for user sign-up
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding // Initializing binding variable for view binding
    private lateinit var mAuth: FirebaseAuth // Firebase authentication instance
    private lateinit var database: DatabaseReference // Firebase database instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater) // Inflating the layout using view binding
        setContentView(binding.root) // Setting the content view to the root of the inflated layout

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance() // Getting FirebaseAuth instance
        database = FirebaseDatabase.getInstance().reference // Getting FirebaseDatabase instance

        // Handle click on "Already Have Account?" text
        binding.tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java)) // Starting SignInActivity when "Already Have Account?" text is clicked
            finish() // Finishing current activity
        }

        // Handle click on "Sign Up" button
        binding.btnSignup.setOnClickListener { view ->
            val username = binding.etUsername.text.toString() // Getting username from input field
            val password = binding.etPassword.text.toString() // Getting password from input field
            val email = binding.etEmail.text.toString() // Getting email from input field
            val confirmPassword = binding.etConfirmPassword.text.toString() // Getting confirm password from input field

            // Validate user input data
            if (isDataValid(view, username, password, email, confirmPassword)) { // Checking if user input data is valid
                binding.progressBar.visibility = View.VISIBLE // Showing progress bar

                // Create user with email and password
                mAuth.createUserWithEmailAndPassword(email, password) // Creating user with email and password
                    .addOnCompleteListener { task ->
                        binding.progressBar.visibility = View.INVISIBLE // Hiding progress bar
                        if (task.isSuccessful) { // Checking if sign-up is successful
                            val userId = mAuth.currentUser?.uid // Getting user ID
                            // Save user data in Firebase database
                            saveUserData(userId, username, email) // Saving user data in Firebase database
                            // Start MainActivity after successful sign-up
                            startActivity(Intent(this, MainActivity::class.java)) // Starting MainActivity
                            finish() // Finishing current activity
                        } else {
                            // Show error message if sign-up fails
                            Snackbar.make(
                                view,
                                task.exception?.localizedMessage ?: "Sign up failed",
                                Snackbar.LENGTH_LONG
                            ).show() // Displaying snackbar with error message
                        }
                    }
            }
        }
    }

    // Validate user input data
    private fun isDataValid(view: View, username: String, password: String, email: String, confirmPassword: String): Boolean {
        val errorMsg: String = when { // Checking various conditions for data validation
            (username.length < 3 || username.length > 30) -> getString(R.string.username_error_invalid_length)
            (username.contains(" ")) -> getString(R.string.username_whitespaces_not_allowed)
            (!username.matches(Regex("^[a-zA-Z0-9_]+\$"))) -> getString(R.string.username_error_invalid_format)
            (!email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) -> getString(R.string.invalid_email)
            (password != confirmPassword) -> getString(R.string.password_confirm_password_not_same)
            (password.length < 6 || password.length > 30) -> getString(R.string.password_error_invalid_length)
            (password.contains(" ")) -> getString(R.string.password_whitespaces_not_allowed)
            (!isPasswordValid(password)) -> getString(R.string.password_error_invalid_format)
            else -> "" // Empty string if no error
        }
        if (errorMsg.isNotEmpty()) {
            // Show error message if any data validation fails
            Snackbar.make(
                view,
                errorMsg,
                Snackbar.LENGTH_LONG
            ).show() // Displaying snackbar with error message
            return false // Returning false if data validation fails
        }
        return true // Returning true if data validation succeeds
    }

    // Validate password format
    private fun isPasswordValid(password: String): Boolean {
        var hasLetter = false
        var hasDigit = false
        var hasSpecialSymbol = false
        for (c in password) {
            if (hasDigit && hasLetter && hasSpecialSymbol) return true // Returning true if password meets all criteria
            if (c in 'a'..'z' || c in 'A'..'Z') hasLetter = true // Checking if password contains letter
            else if (c in '0'..'9') hasDigit = true // Checking if password contains digit
            else hasSpecialSymbol = true // Checking if password contains special symbol
        }
        return hasDigit && hasLetter && hasSpecialSymbol // Returning true if password meets all criteria
    }

    // Save user data in Firebase database
    private fun saveUserData(userId: String?, username: String, email: String) {
        val userMap = mapOf( // Creating map with user data
            "username" to username,
            "email" to email
        )
        userId?.let { // Checking if userId is not null
            database.child("Users").child(userId).setValue(userMap) // Saving user data in Firebase database
        }
    }
}
