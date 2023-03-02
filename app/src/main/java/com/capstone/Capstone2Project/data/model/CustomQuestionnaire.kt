package com.capstone.Capstone2Project.data.model

data class CustomQuestionnaire(
    val date: Long,
    val questions: List<QuestionItem>,
    val scriptUUID: String
)
