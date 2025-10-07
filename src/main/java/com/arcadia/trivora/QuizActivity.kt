package com.arcadia.trivora

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.arcadia.trivora.databinding.ActivityQuizBinding
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private val viewModel: QuizViewModel by viewModels()
    private var currentCategory: String = ""
    private var quizMode: String = "CATEGORY" // Can be "CATEGORY", "RANDOM_SINGLE", "RANDOM_QUIZ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Determine quiz mode from intent
        quizMode = intent.getStringExtra("QUIZ_MODE") ?: "CATEGORY"

        if (quizMode == "CATEGORY") {
            // Category quiz - load questions by category
            currentCategory = intent.getStringExtra("SELECTED_CATEGORY") ?: "Unknown"
            binding.categoryTitle.text = "$currentCategory Quiz"
            viewModel.loadQuestions(currentCategory)
        } else {
            // Random quiz - use pre-loaded questions from intent
            val questions = intent.getSerializableExtra("QUESTIONS") as? ArrayList<Question>
            if (questions != null && questions.isNotEmpty()) {
                binding.categoryTitle.text = when (quizMode) {
                    "RANDOM_SINGLE" -> "Random Question"
                    "RANDOM_QUIZ" -> "Random Quiz"
                    else -> "Random Quiz"
                }
                viewModel.setQuestions(questions)
            } else {
                Toast.makeText(this, "Failed to load questions", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
        }

        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.submitButton.setOnClickListener {
            viewModel.submitAnswer()
        }

        binding.nextButton.setOnClickListener {
            viewModel.nextQuestion()
        }

        binding.errorText.setOnClickListener {
            when (quizMode) {
                "CATEGORY" -> viewModel.loadQuestions(currentCategory)
                else -> {
                    finish()
                }
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.quizState.collect { state ->
                updateUI(state)
            }
        }
    }

    private fun updateUI(state: QuizState) {
        // Handle loading state
        binding.loadingProgress.isVisible = state.isLoading
        binding.errorText.isVisible = state.error != null && state.questions.isEmpty()

        if (state.error != null && state.questions.isEmpty()) {
            binding.errorText.text = state.error
        }

        // Show quiz content only when we have questions
        val showQuizContent = !state.isLoading && state.error == null && state.questions.isNotEmpty()
        binding.categoryTitle.isVisible = showQuizContent
        binding.progressText.isVisible = showQuizContent
        binding.scoreText.isVisible = showQuizContent
        binding.questionText.isVisible = showQuizContent
        binding.choicesRadioGroup.isVisible = showQuizContent
        binding.submitButton.isVisible = showQuizContent
        binding.feedbackText.isVisible = showQuizContent

        if (showQuizContent) {
            displayCurrentQuestion(state)
        }

        // Handle quiz completion
        if (state.quizCompleted) {
            showQuizCompletion(state)
        }
    }

    private fun displayCurrentQuestion(state: QuizState) {
        val currentQuestion = state.questions[state.currentQuestionIndex]

        // Update progress and score
        binding.progressText.text = "Question ${state.currentQuestionIndex + 1}/${state.questions.size}"
        binding.scoreText.text = "Score: ${state.score}"

        // Set question text
        binding.questionText.text = currentQuestion.questionText

        // Setup choices
        setupChoices(currentQuestion, state)

        // Update button states
        binding.submitButton.isEnabled = state.selectedAnswer != null && !state.isAnswerSubmitted
        binding.nextButton.isVisible = state.isAnswerSubmitted

        // Show feedback if answer submitted
        if (state.isAnswerSubmitted) {
            showAnswerFeedback(state, currentQuestion)
        } else {
            binding.feedbackText.visibility = View.GONE
        }
    }

    private fun setupChoices(question: Question, state: QuizState) {
        binding.choicesRadioGroup.removeAllViews()

        question.choices.forEachIndexed { index, choice ->
            val radioButton = RadioButton(this).apply {
                text = choice
                setTextColor(Color.parseColor("#333333"))
                textSize = 16f
                setPadding(16, 20, 16, 20)
                setBackgroundResource(R.drawable.custom_radio_button)
                buttonDrawable = null // Removes default circular button
                layoutParams = RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }

                // Check if this is the selected answer
                isChecked = choice == state.selectedAnswer
            }

            radioButton.setOnClickListener {
                viewModel.selectAnswer(choice)
                binding.submitButton.isEnabled = true
            }

            binding.choicesRadioGroup.addView(radioButton)
        }
    }

    private fun showAnswerFeedback(state: QuizState, question: Question) {
        val isCorrect = state.selectedAnswer == question.answer

        binding.feedbackText.visibility = View.VISIBLE
        if (isCorrect) {
            binding.feedbackText.text = "Correct!"
            binding.feedbackText.setBackgroundColor(Color.parseColor("#4CAF50"))
            binding.feedbackText.setTextColor(Color.WHITE)
        } else {
            binding.feedbackText.text = "Wrong answer! The correct answer was: ${question.answer}"
            binding.feedbackText.setBackgroundColor(Color.parseColor("#D32F2F"))
            binding.feedbackText.setTextColor(Color.WHITE)
        }

        // Disable all radio buttons after submission
        for (i in 0 until binding.choicesRadioGroup.childCount) {
            val radioButton = binding.choicesRadioGroup.getChildAt(i) as RadioButton
            radioButton.isEnabled = false
        }

        binding.submitButton.isEnabled = false
    }

    private fun showQuizCompletion(state: QuizState) {
        val percentage = (state.score.toDouble() / state.questions.size * 100).toInt()

        val dialogTitle = when (quizMode) {
            "RANDOM_SINGLE" -> "Question Completed!"
            else -> "Quiz Completed!"
        }

        val playAgainText = when (quizMode) {
            "RANDOM_SINGLE" -> "Another Question"
            else -> "Play Again"
        }

        AlertDialog.Builder(this)
            .setTitle(dialogTitle)
            .setMessage("You scored ${state.score}/${state.questions.size} ($percentage%)\n\nWell done!")
            .setPositiveButton(playAgainText) { _, _ ->
                when (quizMode) {
                    "CATEGORY" -> viewModel.retryQuiz()
                    "RANDOM_SINGLE", "RANDOM_QUIZ" -> {
                        finish()
                    }
                }
            }
            .setNegativeButton("Back") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }
}