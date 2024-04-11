package com.shrey.project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.shrey.project.databinding.ActivityQuestionsBinding

// Activity for displaying quiz questions and handling user responses
class QuestionsActivity : AppCompatActivity() {

    // Constants for storing response data
    companion object {
        const val CORRECT_RESPONSE = "correct responses"
        const val NO_RESPONSE = "no responses"
    }

    // View binding instance
    private lateinit var binding: ActivityQuestionsBinding

    // List of questions
    private val listOfQuestions = Constants.questionsList

    // Current question index
    private var currentPosition = 0

    // Selected option index
    private var selectedOption = -1

    // Number of correct responses
    private var correctResponses = 0

    // Number of no responses
    private var noResponse = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show first question
        updateQuestion()

        // Set onClickListeners for buttons
        binding.btnSubmit.setOnClickListener {
            if (binding.btnSubmit.text == getString(R.string.submit)) { // In submit mode
                setCorrectOptionBackground()
                // If option was selected, check if it is correct, else consider no response
                if (selectedOption != -1) {
                    if (listOfQuestions[currentPosition].correct_answer != selectedOption)
                        setInCorrectOptionSelectedBackground()
                    else ++correctResponses
                } else ++noResponse
                binding.btnSubmit.text = getString(R.string.next)
            } else { // If already submitted
                currentPosition++
                if (currentPosition < 10) {
                    selectedOption = -1
                    updateQuestion()
                    setOptionsDefaultBackground()
                    binding.btnSubmit.text = getString(R.string.submit)
                    binding.progressBar.progress += 10
                } else {
                    Constants.questionsList.clear()
                    // Start ResultActivity and pass response data
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra(CORRECT_RESPONSE, correctResponses)
                    intent.putExtra(NO_RESPONSE, noResponse)
                    startActivity(intent)
                    finish()
                }
            }
        }

        // Set onClickListeners for option buttons
        binding.tvOption1.setOnClickListener {
            setOptionSelected(binding.tvOption1)
            selectedOption = 0
        }
        binding.tvOption2.setOnClickListener {
            setOptionSelected(binding.tvOption2)
            selectedOption = 1
        }
        binding.tvOption3.setOnClickListener {
            setOptionSelected(binding.tvOption3)
            selectedOption = 2
        }
        binding.tvOption4.setOnClickListener {
            setOptionSelected(binding.tvOption4)
            selectedOption = 3
        }

        // Clear selected option
        binding.tvClearResponse.setOnClickListener {
            setOptionsDefaultBackground()
            selectedOption = -1
        }
    }

    // Update question and options
    private fun updateQuestion() {
        setOptionsDefaultBackground()
        binding.tvQuestion.text = listOfQuestions[currentPosition].question
        binding.tvOption1.text = listOfQuestions[currentPosition].option1
        binding.tvOption2.text = listOfQuestions[currentPosition].option2
        binding.tvOption3.text = listOfQuestions[currentPosition].option3
        binding.tvOption4.text = listOfQuestions[currentPosition].option4
    }

    // Set default background for all options
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setOptionsDefaultBackground() {
        binding.tvOption1.background = getDrawable(R.drawable.custom_card_view)
        binding.tvOption2.background = getDrawable(R.drawable.custom_card_view)
        binding.tvOption3.background = getDrawable(R.drawable.custom_card_view)
        binding.tvOption4.background = getDrawable(R.drawable.custom_card_view)
    }

    // Set selected option background
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setOptionSelected(view: View) {
        setOptionsDefaultBackground()
        view.background = getDrawable(R.drawable.option_selected_background)
    }

    // Set background for correct option
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setCorrectOptionBackground() {
        when (listOfQuestions[currentPosition].correct_answer) {
            0 -> binding.tvOption1.background = getDrawable(R.drawable.correct_option_background)
            1 -> binding.tvOption2.background = getDrawable(R.drawable.correct_option_background)
            2 -> binding.tvOption3.background = getDrawable(R.drawable.correct_option_background)
            3 -> binding.tvOption4.background = getDrawable(R.drawable.correct_option_background)
        }
    }

    // Set background for incorrect option selected
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setInCorrectOptionSelectedBackground() {
        when (selectedOption) {
            0 -> binding.tvOption1.background = getDrawable(R.drawable.wrong_option_background)
            1 -> binding.tvOption2.background = getDrawable(R.drawable.wrong_option_background)
            2 -> binding.tvOption3.background = getDrawable(R.drawable.wrong_option_background)
            else -> binding.tvOption4.background = getDrawable(R.drawable.wrong_option_background)
        }
    }
}
