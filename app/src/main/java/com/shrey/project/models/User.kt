package com.shrey.project.models

// This class represents a user.
class User(
    // The user ID.
    val userid: String = "0",
    // The username.
    val username: String = "username",
    // The email address.
    val email: String = "email",
    // The password.
    val password: String = "password",
    // The total score of the user.
    val totalScore: Long = 0,
    // The best score of the user.
    val bestScore: Long = 0,
    // The profile picture URL of the user.
    val profilePic: String = "https://logodix.com/logo/1984369.png",
    // The total number of quizzes taken by the user.
    val totalQuizzes: Long = 0
) {
    // Empty constructor
    fun User() {}
}
