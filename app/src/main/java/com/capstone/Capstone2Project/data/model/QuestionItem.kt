package com.capstone.Capstone2Project.data.model

import com.google.gson.annotations.SerializedName

data class QuestionItem(
    @SerializedName("question_uuid")
    val uuid: String,
    @SerializedName("question")
    val question: String,
    @SerializedName("ques_type")
    val questionType: Int = 0 //0이면 Common 1이면 individual
)
