package com.capstone.Capstone2Project.data.model

data class QuestionItem(
    val uuid: String,
    val question: String,
    val questionType: Int = 0 //0이면 Common 1이면 individual
)
