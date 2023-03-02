package com.capstone.Capstone2Project.data.model


data class InterviewLog(
    val hostUUID: String,
    val scriptUUID: String,
    val scriptName: String, //마이 페이지에서 쓰기 위함
    val interviewUUID: String,
    val logLines: List<InterviewLogLine>,
    val date: Long
)


data class InterviewLogLine(
    val date: Long = System.currentTimeMillis(),
    val progress: Int,
    val questionItem: QuestionItem,
    val logLine: LogLine
) {
    fun progressToString(): String {
        val sec = progress % 60

        val min = progress / 60

        return "%d:%02d".format(min, sec)
    }

}

data class LogLine(
    val type: Type,
    val message: String
) {
    enum class Type {
        Error,
        Face,
        Pose,
        Voice
    }
}
