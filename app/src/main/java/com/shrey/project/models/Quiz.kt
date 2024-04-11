package com.shrey.project.models

import java.util.ArrayList

// This class represents a Quiz, which contains a name (title).
// It provides a method to create a list of quizzes with predefined names.
class Quiz(
    // The name (title) of the quiz.
    val quizName: String = "Title",
) {
    // Function to create a list of quizzes with predefined names.
    fun createQuizList(): ArrayList<Quiz> {
        // Array containing predefined quiz names.
        val listOfQuizNames = arrayOf(
            "Advanced Maths",
            "Science and Nature",
            "Computers",
            "Gadgets",
            "History",
            "Sports",
            "Vehicle",
            "Anime",
            "Video Games",
            "Board Games",
            "Mythology",
            "Geography"
        )
        // Create a new ArrayList to store Quiz objects.
        val list: ArrayList<Quiz> = ArrayList()
        // Loop through each predefined quiz name.
        listOfQuizNames.forEach {
            // Add a new Quiz object with the current quiz name to the list.
            list.add(Quiz(quizName = it))
        }
        // Return the list of quizzes.
        return list
    }
}
