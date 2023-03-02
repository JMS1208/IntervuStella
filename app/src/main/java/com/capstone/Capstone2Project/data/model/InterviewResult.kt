package com.capstone.Capstone2Project.data.model

data class InterviewResult(
    val uuid: String,
    val scriptUUID: String,
    val memo: String?,
    val memo_date: Long?,
    val interview_date: Long,
    val score: Int?,
    val logs: List<QuestionItem>,
    val answers: List<AnswerItem>
)
