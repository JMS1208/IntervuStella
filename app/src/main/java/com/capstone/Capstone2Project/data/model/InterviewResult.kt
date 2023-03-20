package com.capstone.Capstone2Project.data.model

//인터뷰 결과를 조회하는 용
data class InterviewResult(
    val uuid: String,
    val scriptUUID: String,
//    val memo: String?,
//    val memo_date: Long?,
    val interview_date: Long,
    val score: Int,
    val feedBack: String,
    val duration: Int,
    val newAchievement: List<Achievement>
) {
    fun durationToString(): String {
        val minute = duration / 60
        val second = duration % 60
        return "${minute}분 ${second}초"
    }
}
