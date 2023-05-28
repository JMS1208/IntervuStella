package com.capstone.Capstone2Project.network.service

import com.capstone.Capstone2Project.data.model.Questionnaire
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.ScriptItem
import com.capstone.Capstone2Project.data.model.Topic
import com.capstone.Capstone2Project.data.model.fornetwork.Topics
import com.capstone.Capstone2Project.data.model.UserInfo
import com.capstone.Capstone2Project.data.model.fornetwork.Comment
import com.capstone.Capstone2Project.data.model.fornetwork.MemoForQuestion
import com.capstone.Capstone2Project.data.model.fornetwork.Memo
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestion
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestionComment
import com.capstone.Capstone2Project.data.model.inapp.TodayQuestionMemo
import retrofit2.Response
import retrofit2.http.*

interface MainService {

    /*
    유저 정보 등록하기
     */
    @POST("membership/{user_uuid}")
    suspend fun postUserInfo(
        @Path("user_uuid") hostUUID: String,
        @Body userInfo: UserInfo
    ): Response<Int> //0:실패, 1:성공

    /*
    유저 관심주제 등록하기
     */
    @PUT("user/interesting_field/{user_uuid}")
    suspend fun postInterestingField(
        @Path("user_uuid") hostUUID: String,
        @Body topics: Topics
    ): Response<Int> //0:실패, 1:성공


    /*
    유저 관심주제 가져오기
     */
    @GET("user/interesting_field/{user_uuid}")
    suspend fun getUserInterestingField(
        @Path("user_uuid") hostUUID: String
    ): Response<List<Topic>>


    /*
    유저 출석여부 확인하기
     */
    @GET("user/today_attendance/{user_uuid}")
    suspend fun isPresentToday(
        @Path("user_uuid") hostUUID: String
    ): Response<Int> //0:실패, 1:성공

    /*
    유저 출석체크 하기
     */
    @POST("user/today_attendance/{user_uuid}")
    suspend fun requestAttendanceToday(
        @Path("user_uuid") hostUUID: String
    ): Response<Int> //0:실패, 1:성공
    /*
    오늘의 질문 처음 받아올 때
     */
    @GET("user/first_today_question/")
    suspend fun getTodayQuestionFirst(
        @Query("user_uuid") hostUUID: String
    ): Response<TodayQuestion>

    /*
    오늘의 질문 바꾸기 요청
     */
    @GET("user/change_today_question")
    suspend fun changeTodayQuestion(
        @Query("user_uuid") hostUUID: String,
        @Query("current_ques_uuid") currentQuestionUUID: String
    ): Response<TodayQuestion>


    /*
    오늘의 질문 자신이 단 댓글 보기
    null 이면 단 댓글이 없는 거
     */
    @GET("user/common_ques_memo/")
    suspend fun getMyMemoAboutQuestion(
        @Query("user_uuid") hostUUID: String,
        @Query("common_ques_uuid") questionUUID: String
    ): Response<MemoForQuestion?>

//    /*
//    오늘의 질문에 자신의 메모 달기
//    성공시 1 실패시 0 반환
//     */
//    @POST("user/common_ques_memo/")
//    suspend fun postMyMemoAboutQuestion(
//        @Query("user_uuid") hostUUID: String,
//        @Query("common_ques_uuid") questionUUID: String,
//        @Body memo: Memo
//    ): Response<Int>

    /*
    오늘의 질문 자신의 메모 수정하기
    성공시 1 실패시 0 반환
     */
    @PUT("user/common_ques_memo/")
    suspend fun updateMyMemoAboutQuestion(
        @Query("user_uuid") hostUUID: String,
        @Query("common_ques_uuid") questionUUID: String,
        @Body memo: Memo
    ): Response<Int>
    /*
    오늘의 질문에 자신의 메모 삭제하기
    성공시 1 실패시 0 반환
     */
    @DELETE("user/common_ques_memo/")
    suspend fun deleteMyMemoAboutQuestion(
        @Query("user_uuid") hostUUID: String,
    ):Response<Int>

    /*
    내가쓴 오늘의 질문 댓글들 리스트 보기
    없으면 빈 리스트 반환
     */
    @GET("user/memo_list/{user_uuid}")
    suspend fun getMyMemoList(
        @Path("user_uuid") hostUUID: String
    ): Response<List<MemoForQuestion>>

    /*
    연속 출석일 수 가져오기
     */
    @GET("user/cont_attendance/")
    suspend fun getContinuousAttendance(
        @Query("user_uuid") hostUUID: String
    ): Response<Int>

    /*
    주간 출석 정보 가져오기
     */
    @GET("user/week_attendance/{user_uuid}")
    suspend fun getWeekAttendanceInfo(
        @Path("user_uuid") hostUUID: String
    ): Response<List<Int>>

    /*
    오늘의 질문에 메모 단 것 가져오기
     */
    @GET("user/common_ques_memo/")
    suspend fun getTodayQuestionMemo(
        @Query("user_uuid") hostUUID: String,
        @Query("common_ques_uuid") questionUUID: String
    ): Response<TodayQuestionMemo?>

    /*
    유저별 메모 리스트 가져오기
     */
    @GET("user/memo_list/{user_uuid}")
    suspend fun getTodayQuestionMemoList(
        @Path("user_uuid") hostUUID: String
    ): Response<List<TodayQuestionMemo>?>

    /*
    오늘의 질문에 달린 댓글 리스트 가져오기
     */
    @GET("community/view_comment/")
    suspend fun getTodayQuestionCommentList(
        @Query("ques_uuid") questionUUID: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("user_uuid") hostUUID: String
    ): Response<List<TodayQuestionComment>?>

    /*
    오늘의 질문에 달린 내 댓글 가져오기
     */
    @GET("community/view_my_comment/")
    suspend fun getMyTodayQuestionComment(
        @Query("ques_uuid") questionUUID: String,
        @Query("user_uuid") hostUUID: String
    ): Response<TodayQuestionComment?>

    /*
    오늘의 질문 정보 가져오기
     */

    @GET("common_question/")
    suspend fun getTodayQuestionInfo(
        @Query("common_ques_uuid") questionUUID: String
    ): Response<TodayQuestion?>

    /*
    좋아요 숫자 바꾸기
     */
    @PUT("community/recommendation/")
    suspend fun changeCommentLikeCount(
        @Query("cc_uuid") commentUUID: String,
        @Query("user_uuid") hostUUID: String
    ): Response<Int>


    /*
    댓글 수정하기
    실패: 0 성공:1
     */
    @PUT("community/comment/")
    suspend fun updateMyComment(
        @Query("cc_uuid") commentUUID: String,
        @Body comment: Comment
    ): Response<TodayQuestionComment?>

    /*
    댓글 생성하기
    실패: 0 성공:1
     */
    @POST("community/comment/")
    suspend fun createMyComment(
        @Body comment: Comment
    ): Response<TodayQuestionComment?>

    /*
    댓글 삭제하기
    실패: 0 성공:1
     */
    @DELETE("community/comment/")
    suspend fun deleteMyComment(
        @Query("cc_uuid") commentUUID: String,
        @Query("user_uuid") hostUUID: String
    ): Response<Int>

    @GET("self_intro/script_list/{user_uuid}")
    suspend fun getMyScriptList(
        @Path("user_uuid") hostUUID: String
    ): Response<List<Script>?>

    /*
    자소서 작성을 위한 직무 리스트 가져오기
     */
    @GET("user/job_object_list/")
    suspend fun getJobRoleList(
    ): Response<List<String>?>

    /*
    자소서 작성을 위한 질문 리스트 가져오기
     */
    @GET("self_intro/all_question/")
    suspend fun getScriptItemList(
    ): Response<List<ScriptItem>?>

    /*
    유저가 그동안 봤던 면접의 리스트 가져오기
     */
    @GET("interview/interview_list/")
    suspend fun getMyInterviewList(
        @Query("user_uuid") hostUUID: String
    ): Response<List<InterviewResult>>

    /*
    자기소개서 생성하기
     */
    @POST("self_intro/script/")
    suspend fun createScript(
//        @Query("user_uuid") hostUUID: String,
        @Body script: Script
    ): Response<Int> //1:성공 0:실패

    /*
    면접 질문지 가져오기
     */
    @GET("interview/interview_questionnaire/")
    suspend fun getQuestionnaire(
        @Query("user_uuid") hostUUID: String,
        @Query("script_uuid") scriptUUID: String,
        @Query("role") jobRole: String,
        @Query("reuse") reuse: Int
    ): Response<Questionnaire>
}