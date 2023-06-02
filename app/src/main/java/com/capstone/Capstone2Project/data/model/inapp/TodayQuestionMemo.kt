package com.capstone.Capstone2Project.data.model.inapp

import com.google.gson.annotations.SerializedName
import java.util.*

data class TodayQuestionMemo(
    @SerializedName("memo")
    var memo: String,
    @SerializedName("question")
    val question: String,
    @SerializedName("question_uuid")
    val questionUUID: String,
    @SerializedName("saved_date")
    var savedDate: Long
) {
    companion object {
        fun createTestTodayQuestionMemo(): TodayQuestionMemo {

            return TodayQuestionMemo(
                memo = "메모 예시 ${(1..100).random()}",
                question = "질문 예시 ${(1..100).random()}",
                questionUUID = UUID.randomUUID().toString(),
                savedDate = System.currentTimeMillis(),
//                isNew = false
            )
        }

        fun createNewTodayQuestionMemo(
            questionUUID: String,
            question: String
        ): TodayQuestionMemo {
            return TodayQuestionMemo(
                memo = "",
                question = question,
                questionUUID = questionUUID,
                savedDate = System.currentTimeMillis(),
//                isNew = true
            )
        }


    }
}