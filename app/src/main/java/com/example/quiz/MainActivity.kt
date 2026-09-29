package com.example.quiz

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var questionText: TextView
    private lateinit var scoreText: TextView
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button

    private var questions: List<TriviaQuestion> = emptyList()
    private var currentIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        questionText = findViewById(R.id.questionText)
        scoreText = findViewById(R.id.scoreText)
        trueButton = findViewById(R.id.trueButton)
        falseButton = findViewById(R.id.falseButton)

        trueButton.setOnClickListener { checkAnswer(true) }
        falseButton.setOnClickListener { checkAnswer(false) }

        loadQuestions()
    }

    private fun loadQuestions() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getQuestions()
                if (response.responseCode == 0 && response.results.isNotEmpty()) {
                    questions = response.results
                    currentIndex = 0
                    score = 0
                    setButtonsEnabled(true)
                    showQuestion()
                } else {
                    showError()
                }
            } catch (e: Exception) {
                showError()
            }
        }
    }

    private fun showQuestion() {
        val question = questions[currentIndex]
        questionText.text = HtmlCompat.fromHtml(question.question, HtmlCompat.FROM_HTML_MODE_LEGACY)
        updateScore()
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = questions[currentIndex].correctAnswer.equals("True", ignoreCase = true)

        if (userAnswer == correctAnswer) {
            score++
            Toast.makeText(this, R.string.correct, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.incorrect, Toast.LENGTH_SHORT).show()
        }

        currentIndex++
        if (currentIndex < questions.size) {
            showQuestion()
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        setButtonsEnabled(false)
        updateScore()
        lifecycleScope.launch {
            for (i in 5 downTo 1) {
                questionText.text = getString(R.string.final_score_with_restart, score, questions.size, i)
                delay(1000)
            }
            loadQuestions()
        }
    }

    private fun updateScore() {
        scoreText.text = getString(R.string.score_format, score, questions.size)
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        trueButton.isEnabled = enabled
        falseButton.isEnabled = enabled
    }

    private fun showError() {
        questionText.setText(R.string.error_loading)
    }
}