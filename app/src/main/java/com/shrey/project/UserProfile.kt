package com.shrey.project

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.shrey.project.databinding.ActivityUserProfileBinding
import com.shrey.project.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

// Activity to display user profile information
class UserProfile : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var database: DatabaseReference

    // Initialize Firebase authentication instance and database reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Retrieve user data from Firebase Realtime Database
        database.child("Users").child(mAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    // Set user profile information in UI elements
                    if (user != null) {
                        binding.tvUsername.text = user.username
                        Picasso.get().load(user.profilePic)
                            .placeholder(R.drawable.ic_baseline_account_circle_24)
                            .into(binding.profileImage)
                        binding.tvBestScore.text = user.bestScore.toString()
                        binding.tvTotalScore.text = user.totalScore.toString()
                        binding.tvTotalQuizzes.text = user.totalQuizzes.toString()
                    } else {
                        // Log an error if user data is null
                        Log.e("UserProfile", "User data is null")
                    }
                }

                // Log an error if database operation is cancelled
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("UserProfile", "loadPost:onCancelled", databaseError.toException())
                }
            })
    }
}
