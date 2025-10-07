package com.arcadia.trivora

data class Question(
    val questionId: String,
    val category: String,
    val questionText: String,
    val choices: List<String>,
    val answer: String,
    val difficulty: String
)
