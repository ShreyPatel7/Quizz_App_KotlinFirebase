package com.shrey.project

import android.os.Bundle // Importing Bundle class for handling saved instance state
import android.util.Log // Importing Log class for logging messages
import androidx.appcompat.app.AppCompatActivity // Importing AppCompatActivity class for creating activity
import com.shrey.project.databinding.ActivityUserProfileBinding // Importing ActivityUserProfileBinding class for view binding
import com.shrey.project.models.User // Importing User class for user model
import com.google.firebase.auth.FirebaseAuth // Importing FirebaseAuth class for Firebase authentication
import com.google.firebase.database.* // Importing classes for Firebase Realtime Database
import com.squareup.picasso.Picasso // Importing Picasso class for image loading

// Activity to display user profile information
class UserProfile : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth // Firebase authentication instance
    private lateinit var binding: ActivityUserProfileBinding // Binding variable for view binding
    private lateinit var database: DatabaseReference // Firebase database reference

    // Initialize Firebase authentication instance and database reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater) // Inflating the layout using view binding
        setContentView(binding.root) // Setting the content view to the root of the inflated layout

        mAuth = FirebaseAuth.getInstance() // Getting FirebaseAuth instance
        database = FirebaseDatabase.getInstance().reference // Getting FirebaseDatabase instance

        // Retrieve user data from Firebase Realtime Database
        database.child("Users").child(mAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener { // Adding ValueEventListener to retrieve user data
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java) // Getting user object from DataSnapshot
                    // Set user profile information in UI elements
                    if (user != null) {
                        binding.tvUsername.text = user.username // Setting username in TextView
                        Picasso.get().load(user.profilePic) // Loading profile picture using Picasso
                            .placeholder(R.drawable.ic_baseline_account_circle_24) // Placeholder image
                            .into(binding.profileImage) // Setting profile image in ImageView
                        binding.tvBestScore.text = user.bestScore.toString() // Setting best score in TextView
                        binding.tvTotalScore.text = user.totalScore.toString() // Setting total score in TextView
                        binding.tvTotalQuizzes.text = user.totalQuizzes.toString() // Setting total quizzes count in TextView
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
