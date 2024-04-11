package com.shrey.project

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shrey.project.databinding.ActivityMainBinding
import com.shrey.project.models.Quiz
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.system.exitProcess

// Main activity displaying a list of quizzes and providing options menu
class MainActivity : AppCompatActivity() {

    // View binding instance
    private lateinit var binding: ActivityMainBinding

    // Firebase authentication instance
    private lateinit var mAuth: FirebaseAuth

    // List of quizzes
    private lateinit var quizList: ArrayList<Quiz>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance()

        // Check if user is logged in, if not, redirect to SignInActivity
        val firebaseUser: FirebaseUser? = mAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        // Create list of quizzes
        quizList = Quiz().createQuizList()

        // Initialize RecyclerView adapter
        val adapter = QuizAdapter(quizList, this)
        binding.rvQuizzes.adapter = adapter

        // Set layout manager for RecyclerView
        val gridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvQuizzes.layoutManager = gridLayoutManager
    }

    // Create options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Handle options menu item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        return when (item.itemId) {
            R.id.view_profile -> {
                // Navigate to UserProfile activity if user is logged in
                if (mAuth.currentUser != null) {
                    startActivity(Intent(this@MainActivity, UserProfile::class.java))
                } else {
                    // Display toast message if user is not logged in
                    Toast.makeText(this@MainActivity, "Please login first", Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.exit -> {
                // Display exit confirmation dialog
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.exit))
                builder.setMessage(getString(R.string.exit_confirm_msg))
                builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    run {
                        // Exit the application
                        moveTaskToBack(true)
                        android.os.Process.killProcess(android.os.Process.myPid())
                        exitProcess(1)
                    }
                }
                builder.setNegativeButton(getString(R.string.no)) { dialogInterface, _ ->
                    // Cancel exit operation
                    dialogInterface.cancel()
                }
                val alertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
                true
            }
            R.id.logout -> {
                // Sign out user and navigate to SignInActivity
                mAuth.signOut()
                startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
