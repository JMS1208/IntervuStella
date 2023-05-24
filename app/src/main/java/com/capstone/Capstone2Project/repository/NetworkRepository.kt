package com.capstone.Capstone2Project.repository

import androidx.paging.Pager
import com.capstone.Capstone2Project.data.model.*
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestion
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestionComment
import com.capstone.Capstone2Project.data.model.fornetwork.Topics
import com.capstone.Capstone2Project.data.model.inapp.TodayAttendanceQuiz
import com.capstone.Capstone2Project.data.model.inapp.TodayQuestionMemo
import com.capstone.Capstone2Project.data.model.inapp.WeekAttendanceInfo
import com.capstone.Capstone2Project.data.model.response.InterviewDataResponse
import com.capstone.Capstone2Project.data.resource.Resource
import retrofit2.Response

interface NetworkRepository {
    //    suspend fun getScripts(hostUUID: String): Result<List<Script>>
    suspend fun getQuestionnaire(
        hostUUID: String,
        scriptUUID: String,
        jobRole: String,
        reuse: Boolean
    ): Result<Questionnaire>

    //    suspend fun createEmptyScript(hostUUID: String): Resource<Script>
//    suspend fun getInterviewRecords(hostUUID: String): Result<List<InterviewResult>>
    suspend fun getInterviewScore(hostUUID: String): Result<InterviewScore>
    suspend fun getMyTodayQuestionsMemo(hostUUID: String): Result<List<TodayQuestionMemo>>

    suspend fun sendInterviewData(interviewData: InterviewData): InterviewDataResponse

    suspend fun writeMemo(interviewUUID: String, memo: String): Resource<String> //성공여부 알려줘야함

    suspend fun getInterviewResult(interviewUUID: String): Resource<InterviewResult>


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

    suspend fun updateTodayQuestionMemo(
        hostUUID: String,
        questionUUID: String,
        memo: String
    ): Boolean

    suspend fun getTodayQuestionCommentList(
        questionUUID: String,
        hostUUID: String
    ): Pager<Int, TodayQuestionComment>

    suspend fun getMyTodayQuestionComment(
        questionUUID: String,
        hostUUID: String
    ): Result<TodayQuestionComment?>

    suspend fun getTodayQuestionInfo(
        questionUUID: String
    ): Result<TodayQuestion>

    suspend fun changeCommentLikeCount(
        commentUUID: String,
        hostUUID: String
    ): Result<Int>

    suspend fun createMyComment(
        questionUUID: String,
        hostUUID: String,
        comment: String
    ): Result<TodayQuestionComment>

    suspend fun updateMyComment(
        commentUUID: String,
        questionUUID: String,
        hostUUID: String,
        comment: String
    ): Result<TodayQuestionComment>

    suspend fun deleteMyComment(
        commentUUID: String,
        hostUUID: String
    ): Result<Boolean>

    suspend fun getMyScriptList(
        hostUUID: String
    ): Result<List<Script>>

    suspend fun getJobRoleList(
    ): Result<List<String>>

    suspend fun getScriptItemList(
    ): Result<List<ScriptItem>>

    suspend fun createNewScript(
        script: Script
    ): Result<Boolean>

    suspend fun updateScript(
        script: Script
    ): Result<Boolean>

    suspend fun getMyInterviewResultList(
        hostUUID: String
    ): Result<List<InterviewResult>>

    suspend fun createScript(
        hostUUID: String,
        script: Script
    ): Result<Boolean>
}