package com.capstone.Capstone2Project.data.model

import com.google.gson.annotations.SerializedName

//인터뷰 끝나고 보내는 용도 메모제외
data class InterviewData(
    @SerializedName("questionnaireUUID")
    val questionnaireUUID: String,
    @SerializedName("badExpressions")
    val badExpressions: List<Int>,
    @SerializedName("badPose")
    val badPose: List<Int>,
    @SerializedName("progress")
    val progress: Long,
    @SerializedName("answers")
    val answers: List<AnswerItem>,
    @SerializedName("durations")
    val durations: List<Long>
)

data class AnswerItem(
    @SerializedName("answerUUID")
    val answerUUID: String,
    @SerializedName("questionUUID")
    val questionUUID: String,
    @SerializedName("answer")
    var answer: String = ""
)
