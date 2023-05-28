package com.capstone.Capstone2Project.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionItem(
    @SerializedName("question_uuid")
    val uuid: String,
    @SerializedName("question")
    val question: String,
    @SerializedName("ques_type")
    val questionType: Int = 0 //0이면 Common 1이면 individual
): Parcelable
