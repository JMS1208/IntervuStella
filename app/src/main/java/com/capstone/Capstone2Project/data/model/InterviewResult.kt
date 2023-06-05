package com.capstone.Capstone2Project.data.model

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

//인터뷰 결과를 조회하는 용
@Parcelize
data class InterviewResult(
    @SerializedName("interviewUUID")
    val interviewUUID: String,
    @SerializedName("interviewDate")
    val interviewDate: Long,
    @SerializedName("rank")
    val rank: String,
    @SerializedName("badPoses")
    val badPoses: List<Int>,
    @SerializedName("badExpressions")
    val badExpressions: List<Int>,
    @SerializedName("totalDuration")
    val totalDuration: Long,
    @SerializedName("feedbackList")
    val feedbackList: List<FeedbackItem>
): Parcelable {

    fun toJsonString(): String {
        return Gson().toJson(this)
    }
    fun badPosesToString(): String {
        val poses = listOf("자세 불안정", "얼굴 만짐")
        var result = ""

        for (i in poses.indices) {
            if (badPoses[i] == 0) {
                continue
            }
            result += "[${poses[i]}]: ${badPoses[i]}"
        }

        return result
    }

    fun badExpressionsToString(): String {
        val expressions = listOf("Angry", "Disgust", "Fear", "Sad")
        var result = ""

        for (i in expressions.indices) {
            if (badExpressions[i] == 0) {
                continue
            }
            result += "[${expressions[i]}]: ${badExpressions[i]} "
        }

        return result
    }

    fun totalDurationToString(): String {
        val minute = this.totalDuration / 60
        val second = this.totalDuration % 60
        return "${minute}분 ${second}초"
    }

    companion object {

        fun badPosesList(): List<String> {
            return listOf("자세 불안정", "얼굴 만짐")
        }

        fun badExpressionsList(): List<String> {
            return listOf("Angry", "Disgust", "Fear", "Sad")
        }

        fun createTestInterviewResult(): InterviewResult {

            val feedbackList = mutableListOf<FeedbackItem>()

            for (i in 0 until 2) {
                val feedbackItem = FeedbackItem(
                    question = "질문예시${i + 1}",
                    answer = "답변예시${i + 1}",
                    feedback = "피드백예시${i + 1}",
                    duration = 100,
                    durationWarning = "시간경고예시${i + 1}"
                )

                feedbackList.add(feedbackItem)
            }

            return InterviewResult(
                interviewUUID = UUID.randomUUID().toString(),
                interviewDate = System.currentTimeMillis(),
                rank = "S",
                badPoses = listOf((0..10).random(), (0..10).random()),
                badExpressions = listOf(
                    (0..10).random(),
                    (0..10).random(),
                    (0..10).random(),
                    (0..10).random()
                ),
                totalDuration = (500..1000).random().toLong(),
                feedbackList = feedbackList
            )
        }

        fun jsonStringToInterviewResult(interviewResultJson: String): InterviewResult? {
            return try {

                val gson = Gson()

                gson.fromJson(interviewResultJson, InterviewResult::class.java)

            } catch (e:Exception) {
                e.printStackTrace()
                null
            }

        }
    }
}

@Parcelize
data class FeedbackItem(
    @SerializedName("question")
    val question: String,
    @SerializedName("answer")
    val answer: String,
    @SerializedName("feedback")
    val feedback: String,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("durationWarning")
    val durationWarning: String
): Parcelable {
    fun durationToString(): String {
        val minute = duration / 60
        val second = duration % 60
        return "${minute}분 ${second}초"
    }

    companion object {
        fun createTestFeedbackItem(): FeedbackItem {
            return FeedbackItem(
                question = "예시 질문 ${(1..100).random()}",
                answer = "예시 답 ${(1..100).random()}",
                feedback = "예시 피드백 ${(1..100).random()}",
                duration = (100..300).random().toLong(),
                durationWarning = "예시 주의 ${(1..100).random()}"
            )
        }
    }
}
