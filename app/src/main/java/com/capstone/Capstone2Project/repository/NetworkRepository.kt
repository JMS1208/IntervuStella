package com.capstone.Capstone2Project.repository

import com.capstone.Capstone2Project.data.model.*
import com.capstone.Capstone2Project.data.model.fornetwork.Topics
import com.capstone.Capstone2Project.data.model.inapp.TodayAttendanceQuiz
import com.capstone.Capstone2Project.data.model.inapp.TodayQuestionMemo
import com.capstone.Capstone2Project.data.model.inapp.WeekAttendanceInfo
import com.capstone.Capstone2Project.data.model.response.InterviewDataResponse
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.ui.screen.othersanswers.OthersAnswersData
import retrofit2.Response

interface NetworkRepository {
    //suspend fun getDefaultTopics(): Resource<List<Topic>>
    suspend fun getScripts(hostUUID: String): Result<List<Script>>
    suspend fun getCustomQuestionnaire(script: Script): Resource<CustomQuestionnaire>

    //    suspend fun getScriptPaper(): Resource<ScriptPaper>
    suspend fun createEmptyScript(hostUUID: String): Resource<Script>
    suspend fun getInterviewRecords(hostUUID: String): Result<List<InterviewResult>>
    suspend fun getInterviewScores(hostUUID: String): Result<List<InterviewScore>>
    suspend fun getMyTodayQuestionsMemo(hostUUID: String): Result<List<TodayQuestionMemo>>

    suspend fun sendInterviewData(interviewData: InterviewData): InterviewDataResponse

    suspend fun writeMemo(interviewUUID: String, memo: String): Resource<String> //성공여부 알려줘야함

    suspend fun getInterviewResult(interviewUUID: String): Resource<InterviewResult>

    suspend fun getOthersAnswersData(questionUUID: String): Resource<OthersAnswersData>

    suspend fun updateLikeForAnswerData(answerUUID: String, like: Boolean): Resource<String> //성공여부


//    suspend fun isPresentToday(hostUUID: String): Response<Int>

    suspend fun checkAttendance(hostUUID: String): Resource<Boolean>

    suspend fun getUserTopics(hostUUID: String): Resource<List<Topic>>

    suspend fun postUserInfo(
        hostUUID: String,
        userInfo: UserInfo
    ): Response<Int>

    suspend fun postTopics(
        hostUUID: String,
        topics: Topics
    ): Response<Int>

    suspend fun getTodayQuestionAttendance(
        hostUUID: String,
        currentQuestionUUID: String?
    ): Resource<TodayAttendanceQuiz>

    suspend fun getWeekAttendanceInfo(
        hostUUID: String
    ): Resource<WeekAttendanceInfo>

    suspend fun getTodayQuestionMemo(
        hostUUID: String,
        questionUUID: String,
        question: String
    ): Resource<TodayQuestionMemo>

//    suspend fun postTodayQuestionMemo(
//        hostUUID: String,
//        questionUUID: String,
//        memo: String
//    ): Boolean

    suspend fun updateTodayQuestionMemo(
        hostUUID: String,
        questionUUID: String,
        memo: String
    ): Boolean
}