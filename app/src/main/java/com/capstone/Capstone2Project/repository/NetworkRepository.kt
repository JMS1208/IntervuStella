package com.capstone.Capstone2Project.repository

import com.capstone.Capstone2Project.data.model.*
import com.capstone.Capstone2Project.data.model.TodayQuestion
import com.capstone.Capstone2Project.data.model.response.InterviewDataResponse
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.ui.screen.othersanswers.OthersAnswersData

interface NetworkRepository {
    //suspend fun getDefaultTopics(): Resource<List<Topic>>
    suspend fun getScripts(hostUUID: String): Resource<List<Script>>
    suspend fun getCustomQuestionnaire(script: Script): Resource<CustomQuestionnaire>
//    suspend fun getScriptPaper(): Resource<ScriptPaper>
    suspend fun createEmptyScript(hostUUID: String): Resource<Script>
    suspend fun getInterviewLogs(hostUUID: String): Resource<List<InterviewLog>>
    suspend fun getInterviewScores(hostUUID: String): Resource<List<InterviewScore>>
    suspend fun getMyTodayQuestions(hostUUID: String): Resource<List<TodayQuestion>>

    suspend fun sendInterviewData(interviewData: InterviewData): InterviewDataResponse

    suspend fun writeMemo(interviewUUID: String, memo: String): Resource<String> //성공여부 알려줘야함

    suspend fun getInterviewResult(interviewUUID: String): Resource<InterviewResult>

    suspend fun getOthersAnswersData(questionUUID: String): Resource<OthersAnswersData>

    suspend fun updateLikeForAnswerData(answerUUID: String, like: Boolean): Resource<String> //성공여부

    suspend fun isUserPresent(hostUUID: String): Boolean

    suspend fun checkAttendance(hostUUID: String)

    suspend fun getUserTopics(hostUUID: String): Resource<List<Topic>>

}