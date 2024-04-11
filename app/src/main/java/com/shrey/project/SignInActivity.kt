package com.shrey.project

import android.content.Intent // Importing Intent class for launching activities
import android.os.Bundle // Importing Bundle class for handling saved instance state
import android.util.Log // Importing Log class for logging messages
import android.view.View // Importing View class for accessing UI elements
import android.widget.Toast // Importing Toast class for displaying toast messages
import androidx.appcompat.app.AppCompatActivity // Importing AppCompatActivity class for creating activity
import com.google.android.gms.auth.api.signin.GoogleSignIn // Importing GoogleSignIn class for Google sign-in
import com.google.android.gms.auth.api.signin.GoogleSignInClient // Importing GoogleSignInClient class for Google sign-in
import com.google.android.gms.auth.api.signin.GoogleSignInOptions // Importing GoogleSignInOptions class for Google sign-in
import com.google.android.gms.common.api.ApiException // Importing ApiException class for handling exceptions
import com.google.android.material.snackbar.Snackbar // Importing Snackbar class for displaying snack bar messages
import com.google.firebase.auth.FirebaseAuth // Importing FirebaseAuth class for Firebase authentication
import com.google.firebase.auth.GoogleAuthProvider // Importing GoogleAuthProvider class for Firebase authentication with Google
import com.google.firebase.database.FirebaseDatabase // Importing FirebaseDatabase class for Firebase Realtime Database
import com.shrey.project.databinding.ActivitySignInBinding // Importing ActivitySignInBinding class for view binding
import com.shrey.project.models.User // Importing User class for user model

// Activity for user sign-in
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding // Initializing binding variable for view binding
    private lateinit var mAuth: FirebaseAuth // Firebase authentication instance
    private lateinit var mDatabase: FirebaseDatabase // Firebase database instance
    private lateinit var mGoogleSignInClient: GoogleSignInClient // Google sign-in client instance

    companion object {
        const val RC_SIGN_IN = 65 // Request code for Google sign-in
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater) // Inflating the layout using view binding
        setContentView(binding.root) // Setting the content view to the root of the inflated layout

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance() // Getting FirebaseAuth instance
        mDatabase = FirebaseDatabase.getInstance() // Getting FirebaseDatabase instance

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Requesting ID token
            .requestEmail() // Requesting email
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso) // Getting GoogleSignInClient instance

        // Handle click on "Create New Account" text
        binding.tvCreateNewAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java) // Creating intent to navigate to SignUpActivity
            startActivity(intent) // Starting SignUpActivity
        }

        // Handle click on "Sign In" button
        binding.btnSignin.setOnClickListener { view ->
            val email = binding.etEmail.text.toString() // Getting email from input field
            val password = binding.etPassword.text.toString() // Getting password from input field

            if (email.isNotEmpty() && password.isNotEmpty()) { // Checking if email and password are not empty
                binding.progressBar.visibility = View.VISIBLE // Showing progress bar
                // Sign in with email and password
                mAuth.signInWithEmailAndPassword(email, password) // Signing in with email and password
                    .addOnCompleteListener { task ->
                        binding.progressBar.visibility = View.INVISIBLE // Hiding progress bar
                        if (task.isSuccessful) { // Checking if sign-in is successful
                            // If sign-in is successful, start MainActivity
                            startActivity(Intent(this@SignInActivity, MainActivity::class.java)) // Starting MainActivity
                            finish() // Finishing current activity
                        } else {
                            // If sign-in fails, show error message
                            Snackbar.make(
                                view,
                                task.exception?.localizedMessage ?: "Unknown error",
                                Snackbar.LENGTH_LONG
                            ).show() // Displaying snackbar with error message
                        }
                    }
            } else {
                // Show toast if email or password is empty
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show() // Displaying toast message
            }
        }

        // Handle click on "Sign in with Google" button
        binding.btnGoogle.setOnClickListener {
            signIn() // Initiating Google sign-in
        }
    }

    // Start the sign-in flow with Google
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent // Getting sign-in intent from GoogleSignInClient
        startActivityForResult(signInIntent, RC_SIGN_IN) // Starting sign-in activity
    }

    // Handle the result of Google sign-in
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) { // Checking request code
            val task = GoogleSignIn.getSignedInAccountFromIntent(data) // Getting task result from Google sign-in
            try {
                // Google Sign-In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!! // Getting account information
                firebaseAuthWithGoogle(account.idToken!!) // Authenticating with Firebase using Google credentials
            } catch (e: ApiException) {
                // Google Sign-In failed, show error message
                Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show() // Displaying toast message
            }
        }
    }

    // Authenticate with Firebase using Google credentials
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null) // Creating Google auth credential
        mAuth.signInWithCredential(credential) // Signing in with Google credential
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { // Checking if sign-in is successful
                    // Sign in success, update UI with the signed-in user's information
                    val firebaseUser = mAuth.currentUser // Getting current user
                    if (firebaseUser != null) { // Checking if user is not null
                        // Create user object and store in Firebase database
                        val user = User(
                            userid = firebaseUser.uid,
                            username = firebaseUser.displayName ?: "", // Getting username
                            email = firebaseUser.email ?: "", // Getting email
                            profilePic = firebaseUser.photoUrl.toString() // Getting profile picture URL
                        )
                        mDatabase.reference.child("Users").child(user.userid).setValue(user) // Storing user data in Firebase database
                    }
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java)) // Starting MainActivity
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("signInWithCredential", "signInWithCredential:failure", task.exception) // Logging error message
                    Toast.makeText(
                        this,
                        getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show() // Displaying toast message
                }
            }
    }
}
