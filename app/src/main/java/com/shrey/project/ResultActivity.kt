package com.shrey.project

import android.content.Intent // Importing Intent class for launching activities
import androidx.appcompat.app.AppCompatActivity // Importing AppCompatActivity class for creating activity
import android.os.Bundle // Importing Bundle class for handling saved instance state
import android.util.Log // Importing Log class for logging messages
import com.shrey.project.databinding.ActivityResultBinding // Importing ActivityResultBinding class for view binding
import com.google.firebase.auth.FirebaseAuth // Importing FirebaseAuth class for Firebase authentication
import com.google.firebase.database.* // Importing FirebaseDatabase and DatabaseReference classes for Firebase Realtime Database

// Activity to display quiz result and upload user result to Firebase
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding // Initializing binding variable for view binding
    private var correctResponses = 0 // Variable to store number of correct responses
    private var noResponse = 0 // Variable to store number of unanswered questions
    private lateinit var mAuth: FirebaseAuth // Firebase authentication instance
    private lateinit var mDatabase: DatabaseReference // Firebase database reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater) // Inflating the layout using view binding
        setContentView(binding.root) // Setting the content view to the root of the inflated layout

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance() // Getting FirebaseAuth instance
        mDatabase = FirebaseDatabase.getInstance().reference // Getting FirebaseDatabase reference

        // Retrieve data from intent
        correctResponses = intent.getIntExtra(QuestionsActivity.CORRECT_RESPONSE, 0) // Getting correct responses from intent
        noResponse = intent.getIntExtra(QuestionsActivity.NO_RESPONSE, 0) // Getting unanswered questions from intent

        // Upload user result to Firebase database
        val currentUser = mAuth.currentUser // Getting current user from FirebaseAuth
        if (currentUser != null) { // Checking if current user is not null
            uploadUserResult(currentUser.uid, correctResponses) // Uploading user result to Firebase database
        }

        // Update UI with quiz result
        binding.progressBarResult.progress = correctResponses * 10 // Setting progress bar progress
        binding.tvCorrect.text = correctResponses.toString() // Setting correct responses text
        binding.tvIncorrect.text = (10 - correctResponses - noResponse).toString() // Setting incorrect responses text
        binding.tvUnattempted.text = noResponse.toString() // Setting unanswered questions text

        // Button click listener to play quiz again
        binding.btnPlayAgain.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)) // Starting MainActivity
            finish() // Finishing current activity
        }
    }

    // Function to upload user result to Firebase database
    private fun uploadUserResult(userId: String, score: Int) {
        // Update the user's score in the database
        mDatabase.child("Users").child(userId).child("totalScore").setValue(score) // Setting user's total score in database
            .addOnSuccessListener {
                Log.d("ResultActivity", "User score uploaded successfully") // Logging success message
            }
            .addOnFailureListener { e ->
                Log.e("ResultActivity", "Error uploading user score", e) // Logging error message if upload fails
            }

        // Increment the total quizzes count in the database
        mDatabase.child("Users").child(userId).child("totalQuizzes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val totalQuizzes = snapshot.getValue(Int::class.java) ?: 0 // Getting total quizzes from database
                mDatabase.child("Users").child(userId).child("totalQuizzes").setValue(totalQuizzes + 1) // Incrementing total quizzes count
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ResultActivity", "Error updating total quizzes count", error.toException()) // Logging error if update fails
            }
        })

        // Update the best score if the current score is better
        mDatabase.child("Users").child(userId).child("bestScore").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bestScore = snapshot.getValue(Int::class.java) ?: 0 // Getting best score from database
                if (score > bestScore) { // Checking if current score is better than best score
                    mDatabase.child("Users").child(userId).child("bestScore").setValue(score) // Setting new best score in database
                        .addOnSuccessListener {
                            Log.d("ResultActivity", "Best score updated successfully") // Logging success message
                        }
                        .addOnFailureListener { e ->
                            Log.e("ResultActivity", "Error updating best score", e) // Logging error message if update fails
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ResultActivity", "Error retrieving best score", error.toException()) // Logging error if retrieval fails
            }
        })
    }
}
