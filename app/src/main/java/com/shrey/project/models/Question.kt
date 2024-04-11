package com.shrey.project.models

// This class represents a single question in the quiz.
// It contains properties for the question text, difficulty level,
// index of the correct answer, and four options to choose from.
class Question(
    // The text of the question.
    val question: String,
    // The difficulty level of the question.
    val difficulty: String,
    // The index of the correct answer in the options list (0 to 3).
    val correct_answer: Int,
    // The first option for the question.
    val option1: String,
    // The second option for the question.
    val option2: String,
    // The third option for the question.
    val option3: String,
    // The fourth option for the question.
    val option4: String
)
