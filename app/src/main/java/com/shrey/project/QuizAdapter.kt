package com.shrey.project

import android.app.ProgressDialog // Importing ProgressDialog class to show progress dialog
import android.content.Context // Importing Context class
import android.content.Intent // Importing Intent class for launching activities
import android.view.LayoutInflater // Importing LayoutInflater class for inflating layouts
import android.view.View // Importing View class
import android.view.ViewGroup // Importing ViewGroup class
import android.widget.Button // Importing Button class
import android.widget.Toast // Importing Toast class for displaying messages
import androidx.recyclerview.widget.RecyclerView // Importing RecyclerView class
import com.android.volley.Request // Importing Request class from Volley library
import com.android.volley.toolbox.StringRequest // Importing StringRequest class from Volley library
import com.android.volley.toolbox.Volley // Importing Volley class for network requests
import com.shrey.project.models.Question // Importing Question model class
import com.shrey.project.models.Quiz // Importing Quiz model class
import org.json.JSONObject // Importing JSONObject class for JSON processing

// Adapter for displaying quizzes in a RecyclerView
class QuizAdapter(private val quizList: ArrayList<Quiz>, private val context: Context) :
    RecyclerView.Adapter<QuizAdapter.ViewHolder>() {

    // Map to store quiz names and their corresponding API URLs
    private val urlMap = mapOf(
        "Advanced Maths" to "https://opentdb.com/api.php?amount=10&category=19&type=multiple",
        "Science and Nature" to "https://opentdb.com/api.php?amount=10&category=17&type=multiple",
        "Computers" to "https://opentdb.com/api.php?amount=10&category=18&type=multiple",
        "Gadgets" to "https://opentdb.com/api.php?amount=10&category=30&type=multiple",
        "History" to "https://opentdb.com/api.php?amount=10&category=23&type=multiple",
        "Sports" to "https://opentdb.com/api.php?amount=10&category=21&type=multiple",
        "Vehicle" to "https://opentdb.com/api.php?amount=10&category=28&type=multiple",
        "Anime" to "https://opentdb.com/api.php?amount=10&category=31&type=multiple",
        "Video Games" to "https://opentdb.com/api.php?amount=10&category=15&type=multiple",
        "Board Games" to "https://opentdb.com/api.php?amount=10&category=16&type=multiple",
        "Mythology" to "https://opentdb.com/api.php?amount=10&category=20&type=multiple",
        "Geography" to "https://opentdb.com/api.php?amount=10&category=22&type=multiple"
    )

    // Progress dialog for API request
    private val progressDialog = ProgressDialog(context)

    // Selected quiz name
    private lateinit var selectedQuiz: String

    // ViewHolder class for holding the quiz item views
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.btnSelectedQuiz)
    }

    // Method invoked by RecyclerView when creating new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for quiz item
        val inflater = LayoutInflater.from(parent.context)
        val quizView = inflater.inflate(R.layout.quiz_row, parent, false)
        return ViewHolder(quizView)
    }

    // Method invoked by RecyclerView when it needs to display data at a specific position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = quizList[position]
        val button = holder.button
        button.text = currentObj.quizName
        button.setOnClickListener {
            selectedQuiz = currentObj.quizName

            // Set progress dialog
            progressDialog.setMessage("Please wait...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            // Prepare a list of all questions from API
            FetchData().start()
        }
    }

    // Method invoked by RecyclerView to get the number of items in the dataset
    override fun getItemCount(): Int {
        return quizList.size
    }

    // Thread class for fetching data from API
    inner class FetchData : Thread() {
        override fun run() {
            val selectedQuizURL = urlMap[selectedQuiz].toString()
            val requestQueue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(Request.Method.GET, selectedQuizURL, {
                try {
                    println("Response is $it")
                    val jsonObject = JSONObject(it)
                    val jsonArray = jsonObject.getJSONArray("results")
                    for (i in 0 until jsonArray.length()) {
                        val currentObject = jsonArray.getJSONObject(i)

                        // Shuffle options to set correct option at random position
                        val optionNumbersShuffled = arrayListOf(0, 1, 2, 3)
                        optionNumbersShuffled.shuffle()

                        // Stores corresponding options of optionNumbersShuffled list
                        val optionsList = arrayListOf<String>()
                        var correctOptionNumber = 0
                        for (j in 0 until optionNumbersShuffled.size) {
                            if (optionNumbersShuffled[j] == 3) {
                                optionsList.add(currentObject.getString("correct_answer"))
                                correctOptionNumber = j
                            } else optionsList.add(
                                currentObject.getJSONArray("incorrect_answers").get(optionNumbersShuffled[j]).toString()
                            )
                        }

                        // Create new question and add to list of questions
                        val question = Question(
                            question = currentObject.getString("question"),
                            difficulty = currentObject.getString("difficulty"),
                            correct_answer = correctOptionNumber,
                            option1 = optionsList[0],
                            option2 = optionsList[1],
                            option3 = optionsList[2],
                            option4 = optionsList[3]
                        )
                        Constants.questionsList.add(question)
                    }
                    progressDialog.dismiss()
                    move()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }, {
                Toast.makeText(
                    context,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            })
            requestQueue.add(stringRequest)
        }

        // Move to QuestionsActivity after fetching data
        private fun move() {
            val intent = Intent(context, QuestionsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
