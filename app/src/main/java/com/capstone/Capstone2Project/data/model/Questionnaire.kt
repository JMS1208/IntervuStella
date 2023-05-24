package com.capstone.Capstone2Project.data.model

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Questionnaire(
    @SerializedName("questionnaire_uuid")
    val uuid: String,
    @SerializedName("questions")
    val questions: List<QuestionItem>
) {
    fun toJsonString(): String {
        val gson = GsonBuilder().setLenient().create()

        return gson.toJson(this, Questionnaire::class.java)
    }

    companion object {
        fun jsonToObject(jsonString: String): Questionnaire? {
            return try {
                val gson = GsonBuilder().setLenient().create()

                gson.fromJson(jsonString, Questionnaire::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun createTestQuestionnaire(): Questionnaire {

            val questions = mutableListOf<QuestionItem>()

            for ( i in 0 until 5) {
                val questionItem = QuestionItem(
                    uuid = UUID.randomUUID().toString(),
                    question = "$i 번째 질문 예시",
                )
                questions.add(questionItem)
            }

            return Questionnaire(
                uuid = UUID.randomUUID().toString(),
                questions = questions
            )
        }
    }
}
