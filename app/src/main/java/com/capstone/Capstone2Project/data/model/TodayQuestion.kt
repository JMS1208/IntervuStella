package com.capstone.Capstone2Project.data.model

import com.capstone.Capstone2Project.data.model.Topic

data class TodayQuestion(
    val topic: Topic,
    val question: String,
    var answer: String = ""
)
