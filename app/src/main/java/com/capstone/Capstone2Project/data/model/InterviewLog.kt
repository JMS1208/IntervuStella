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
    val progress: Long,
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
    val message: String,
    val index: Int? = null,
    //표정은 Angry, Disgust, Fear, Sad 순 0,1,2,3
    //포즈는 어깨높이, 얼굴만지기 순 0,1
) {
    enum class Type {
        Error,
        Camera,
        Pose,
        Voice,
        Expression
    }
}
