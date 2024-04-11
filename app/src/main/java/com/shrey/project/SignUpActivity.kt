package com.shrey.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.shrey.project.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// Activity for user sign-up
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Handle click on "Already Have Account?" text
        binding.tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        // Handle click on "Sign Up" button
        binding.btnSignup.setOnClickListener { view ->
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            val email = binding.etEmail.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            // Validate user input data
            if (isDataValid(view, username, password, email, confirmPassword)) {
                binding.progressBar.visibility = View.VISIBLE

                // Create user with email and password
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        binding.progressBar.visibility = View.INVISIBLE
                        if (task.isSuccessful) {
                            val userId = mAuth.currentUser?.uid
                            // Save user data in Firebase database
                            saveUserData(userId, username, email)
                            // Start MainActivity after successful sign-up
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            // Show error message if sign-up fails
                            Snackbar.make(
                                view,
                                task.exception?.localizedMessage ?: "Sign up failed",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    // Validate user input data
    private fun isDataValid(view: View, username: String, password: String, email: String, confirmPassword: String): Boolean {
        val errorMsg: String = when {
            (username.length < 3 || username.length > 30) -> getString(R.string.username_error_invalid_length)
            (username.contains(" ")) -> getString(R.string.username_whitespaces_not_allowed)
            (!username.matches(Regex("^[a-zA-Z0-9_]+\$"))) -> getString(R.string.username_error_invalid_format)
            (!email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) -> getString(R.string.invalid_email)
            (password != confirmPassword) -> getString(R.string.password_confirm_password_not_same)
            (password.length < 6 || password.length > 30) -> getString(R.string.password_error_invalid_length)
            (password.contains(" ")) -> getString(R.string.password_whitespaces_not_allowed)
            (!isPasswordValid(password)) -> getString(R.string.password_error_invalid_format)
            else -> ""
        }
        if (errorMsg.isNotEmpty()) {
            // Show error message if any data validation fails
            Snackbar.make(
                view,
                errorMsg,
                Snackbar.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }

    // Validate password format
    private fun isPasswordValid(password: String): Boolean {
        var hasLetter = false
        var hasDigit = false
        var hasSpecialSymbol = false
        for (c in password) {
            if (hasDigit && hasLetter && hasSpecialSymbol) return true
            if (c in 'a'..'z' || c in 'A'..'Z') hasLetter = true
            else if (c in '0'..'9') hasDigit = true
            else hasSpecialSymbol = true
        }
        return hasDigit && hasLetter && hasSpecialSymbol
    }

    // Save user data in Firebase database
    private fun saveUserData(userId: String?, username: String, email: String) {
        val userMap = mapOf(
            "username" to username,
            "email" to email
        )
        userId?.let {
            database.child("Users").child(userId).setValue(userMap)
        }
    }
}
