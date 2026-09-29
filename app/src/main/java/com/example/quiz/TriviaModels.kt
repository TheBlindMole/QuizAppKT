package com.example.quiz

import com.google.gson.annotations.SerializedName

data class TriviaResponse(
    @SerializedName("response_code") val responseCode: Int,
    val results: List<TriviaQuestion>
)

data class TriviaQuestion(
    val question: String,
    @SerializedName("correct_answer") val correctAnswer: String // "true" or "false"
)