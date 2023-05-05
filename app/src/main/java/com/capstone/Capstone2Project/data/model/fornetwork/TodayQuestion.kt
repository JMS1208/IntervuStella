package com.capstone.Capstone2Project.data.model.fornetwork

import com.google.gson.annotations.SerializedName

data class TodayQuestion(
    @SerializedName("question")
    val question: String,
    @SerializedName("ques_uuid")
    val questionUUID: String,
    @SerializedName("ques_type")
    val field: String? = null
)