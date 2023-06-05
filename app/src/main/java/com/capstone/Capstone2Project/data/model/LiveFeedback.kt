package com.capstone.Capstone2Project.data.model

//data class LiveFeedbackLine(
////    val date: Long = System.currentTimeMillis(),
//    val progress: Long,
////    val questionItem: QuestionItem,
//    val liveFeedbackInfo: LiveFeedbackInfo
//) {
//    fun progressToString(): String {
//        val sec = progress % 60
//
//        val min = progress / 60
//
//        return "%d:%02d".format(min, sec)
//    }
//
//}

data class LiveFeedback(
    val timestamp: Long,
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

    fun progressToString(): String {
        val sec = timestamp % 60

        val min = timestamp / 60

        return "%d:%02d".format(min, sec)
    }
}
