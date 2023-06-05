package com.capstone.Capstone2Project.data.model

import android.os.Parcelable
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Questionnaire(
    @SerializedName("questionnaire_uuid")
    val uuid: String,
    @SerializedName("questions")
    val questions: List<QuestionItem>
): Parcelable {
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


            questions.add(QuestionItem(
                uuid = UUID.randomUUID().toString(),
                question = "사용해보신 프레임워크와 언어는 무엇이 있나요?",
            ))

            questions.add(QuestionItem(
                uuid = UUID.randomUUID().toString(),
                question = "프로젝트 활동 중 힘들었던 점과 극복했던 방법에 대해서 알려주세요",
            ))

            questions.add(QuestionItem(
                uuid = UUID.randomUUID().toString(),
                question = "Compose와 View의 차이는 무엇인가요?",
            ))

            return Questionnaire(
                uuid = UUID.randomUUID().toString(),
                questions = questions
            )
        }
    }
}
