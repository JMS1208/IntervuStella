package com.capstone.Capstone2Project.data.model

//인터뷰 끝나고 보내는 용도 메모제외
data class InterviewData(
    val interviewUUID: String,
    val interviewDate: Long,
//    val memo: String,
    val logs: List<InterviewLogLine>,
    val scriptUUID: String,
    val durations: List<Int>
)
