package com.arcadia.trivora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizState(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String? = null,
    val score: Int = 0,
    val isAnswerSubmitted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val quizCompleted: Boolean = false
)

class QuizViewModel : ViewModel() {
    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private val apiService = RetrofitClient.instance

    fun loadQuestions(category: String) {
        _quizState.value = _quizState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val response = apiService.getQuestionsByCategory(category)
                if (response.isSuccessful && response.body()?.success == true) {
                    val questions = response.body()?.data ?: emptyList()
                    _quizState.value = _quizState.value.copy(
                        questions = questions,
                        isLoading = false,
                        currentQuestionIndex = 0
                    )
                } else {
                    _quizState.value = _quizState.value.copy(
                        isLoading = false,
                        error = response.body()?.message ?: "Failed to load questions"
                    )
                }
            } catch (e: Exception) {
                _quizState.value = _quizState.value.copy(
                    isLoading = false,
                    error = "Network error: ${e.message}"
                )
            }
        }
    }

    fun selectAnswer(answer: String) {
        _quizState.value = _quizState.value.copy(selectedAnswer = answer)
    }

    fun submitAnswer() {
        val currentState = _quizState.value
        val currentQuestion = currentState.questions.getOrNull(currentState.currentQuestionIndex) ?: return

        val isCorrect = currentState.selectedAnswer == currentQuestion.answer
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score

        _quizState.value = currentState.copy(
            isAnswerSubmitted = true,
            score = newScore
        )
    }

    fun nextQuestion() {
        val currentState = _quizState.value
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex >= currentState.questions.size) {
            _quizState.value = currentState.copy(quizCompleted = true)
        } else {
            _quizState.value = currentState.copy(
                currentQuestionIndex = nextIndex,
                selectedAnswer = null,
                isAnswerSubmitted = false
            )
        }
    }

    fun setQuestions(questions: List<Question>) {
        _quizState.value = _quizState.value.copy(
            questions = questions,
            isLoading = false,
            currentQuestionIndex = 0,
            score = 0
        )
    }

    fun retryQuiz() {
        _quizState.value = QuizState(
            questions = _quizState.value.questions,
            currentQuestionIndex = 0,
            score = 0
        )
    }
}