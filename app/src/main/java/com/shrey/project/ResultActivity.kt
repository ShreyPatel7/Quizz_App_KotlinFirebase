package com.shrey.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.shrey.project.databinding.ActivityResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// Activity to display quiz result and upload user result to Firebase
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private var correctResponses = 0
    private var noResponse = 0
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        // Retrieve data from intent
        correctResponses = intent.getIntExtra(QuestionsActivity.CORRECT_RESPONSE, 0)
        noResponse = intent.getIntExtra(QuestionsActivity.NO_RESPONSE, 0)

        // Upload user result to Firebase database
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            uploadUserResult(currentUser.uid, correctResponses)
        }

        // Update UI with quiz result
        binding.progressBarResult.progress = correctResponses * 10
        binding.tvCorrect.text = correctResponses.toString()
        binding.tvIncorrect.text = (10 - correctResponses - noResponse).toString()
        binding.tvUnattempted.text = noResponse.toString()

        // Button click listener to play quiz again
        binding.btnPlayAgain.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // Function to upload user result to Firebase database
    private fun uploadUserResult(userId: String, score: Int) {
        // Update the user's score in the database
        mDatabase.child("Users").child(userId).child("totalScore").setValue(score)
            .addOnSuccessListener {
                Log.d("ResultActivity", "User score uploaded successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ResultActivity", "Error uploading user score", e)
            }

        // Increment the total quizzes count in the database
        mDatabase.child("Users").child(userId).child("totalQuizzes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val totalQuizzes = snapshot.getValue(Int::class.java) ?: 0
                mDatabase.child("Users").child(userId).child("totalQuizzes").setValue(totalQuizzes + 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ResultActivity", "Error updating total quizzes count", error.toException())
            }
        })

        // Update the best score if the current score is better
        mDatabase.child("Users").child(userId).child("bestScore").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bestScore = snapshot.getValue(Int::class.java) ?: 0
                if (score > bestScore) {
                    mDatabase.child("Users").child(userId).child("bestScore").setValue(score)
                        .addOnSuccessListener {
                            Log.d("ResultActivity", "Best score updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("ResultActivity", "Error updating best score", e)
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ResultActivity", "Error retrieving best score", error.toException())
            }
        })
    }
}
