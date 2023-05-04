package com.capstone.Capstone2Project.data.model.fornetwork

import com.google.gson.annotations.SerializedName

// 얘는 댓글용으로 바꿔야함

data class MemoForQuestion(
    @SerializedName("memo")
    var memo: String,
    @SerializedName("question")
    val question: String,
    @SerializedName("question_uuid")
    val questionUUID: String,
    @SerializedName("saved_date")
    val writeDate: Long
)