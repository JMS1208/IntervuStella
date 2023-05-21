package com.capstone.Capstone2Project.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.capstone.Capstone2Project.data.model.*
import com.capstone.Capstone2Project.data.model.fornetwork.Comment
import com.capstone.Capstone2Project.data.model.fornetwork.Memo
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestion
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestionComment
import com.capstone.Capstone2Project.data.model.fornetwork.Topics
import com.capstone.Capstone2Project.data.model.inapp.TodayAttendanceQuiz
import com.capstone.Capstone2Project.data.model.inapp.TodayQuestionMemo
import com.capstone.Capstone2Project.data.model.inapp.WeekAttendanceInfo
import com.capstone.Capstone2Project.data.model.inapp.WeekItem
import com.capstone.Capstone2Project.data.model.response.InterviewDataResponse
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.network.service.MainService
import com.capstone.Capstone2Project.ui.screen.comment.CommentPagingSource
import com.capstone.Capstone2Project.ui.screen.comment.CommentPagingSource.Companion.PAGING_SIZE
import com.capstone.Capstone2Project.utils.extensions.generateRandomText
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(
    private val mainService: MainService
) : NetworkRepository {


    override suspend fun getCustomQuestionnaire(script: Script): Resource<CustomQuestionnaire> {

        val questionItems = mutableListOf<QuestionItem>()

        repeat(5) {

            val questionItem = QuestionItem(UUID.randomUUID().toString(), "예시 질문 $it")

            questionItems.add(
                questionItem
            )
        }

        val customQuestionnaire = CustomQuestionnaire(
            date = System.currentTimeMillis(),
            questions = questionItems,
            scriptUUID = UUID.randomUUID().toString(),
            questionnaireUUID = UUID.randomUUID().toString()
        )

        return Resource.Success(customQuestionnaire)

    }



    override suspend fun getInterviewScores(hostUUID: String): Result<List<InterviewScore>> {

        val interviewScores = mutableListOf<InterviewScore>()

        val interviewScore = InterviewScore(
            scriptUUID = UUID.randomUUID().toString(),
            hostUUID = hostUUID,
            interviewUUID = UUID.randomUUID().toString(),
            date = System.currentTimeMillis() + (0..1000000).random(),
            score = (500..1000).random()
        )

        for (i in 0 until 10) {
            interviewScores.add(interviewScore)
        }

        return Result.success(interviewScores)



    }

    override suspend fun getMyTodayQuestionsMemo(hostUUID: String): Result<List<TodayQuestionMemo>> {

        return try {
            val response = mainService.getTodayQuestionMemoList(hostUUID)

            if(!response.isSuccessful) {
                throw Exception("메모 리스트 네트워크 오류")
            }

            val result = response.body() ?: throw Exception("메모 리스트 null")

            Result.success(result)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }

    }

    override suspend fun sendInterviewData(interviewData: InterviewData): InterviewDataResponse {
        //TODO(interviewData 보내기)

        return InterviewDataResponse("성공")
    }

    override suspend fun writeMemo(interviewUUID: String, memo: String): Resource<String> {
        //TODO(Memo 보내기)
        return Resource.Success(memo)

    }

    override suspend fun getInterviewResult(interviewUUID: String): Resource<InterviewResult> {

        val newAchievements = mutableListOf<Achievement>()

        for (i in 0 until 10) {
            newAchievements.add(
                Achievement(
                    date = System.currentTimeMillis(),
                    text = "업적 예시 $i",
                    type = (0..3).random()
                )
            )
        }

        val interviewResult = InterviewResult.createTestInterviewResult()

        return Resource.Success(interviewResult)
    }





    override suspend fun checkAttendance(hostUUID: String): Resource<Boolean> {
        return try {

            val response = mainService.requestAttendanceToday(hostUUID)

            if (!response.isSuccessful) {
                throw Exception("네트워크 통신 오류-AttendanceCheck")
            }

            val result = response.body() ?: throw Exception("네트워크 통신 오류-AttendanceCheck")

            //1이면 성공, 0이면 실패
            Resource.Success(result == 1)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun getUserTopics(hostUUID: String): Resource<List<Topic>> {

        return try {

            val result = mainService.getUserInterestingField(hostUUID)


            if (!result.isSuccessful) {
                throw Exception("네트워크 오류")
            }

            val topics = result.body() ?: throw Exception("네트워크 오류")

            Resource.Success(topics)

        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }

    }


    override suspend fun postUserInfo(
        hostUUID: String,
        userInfo: UserInfo
    ): Response<Int> {
        return mainService.postUserInfo(
            hostUUID,
            userInfo
        )
    }

    override suspend fun postTopics(
        hostUUID: String,
        topics: Topics
    ): Response<Int> {
        return mainService.postInterestingField(
            hostUUID,
            topics
        )

    }

    override suspend fun getTodayQuestionAttendance(
        hostUUID: String,
        currentQuestionUUID: String?
    ): Resource<TodayAttendanceQuiz> {
        return try {
            val isPresentTodayResponse = mainService.isPresentToday(hostUUID)

            if (!isPresentTodayResponse.isSuccessful) {
                throw Exception("네트워크 오류-attendance")
            }

            val isPresentToday =
                isPresentTodayResponse.body() ?: throw Exception("서버 오류-attendance null")

            val todayQuestionResponse = if (currentQuestionUUID == null) {
                mainService.getTodayQuestionFirst(hostUUID)
            } else {
                mainService.changeTodayQuestion(hostUUID, currentQuestionUUID)
            }

            if (!todayQuestionResponse.isSuccessful) {
                throw Exception("네트워크 오류-today_question")
            }

            val todayQuestion =
                todayQuestionResponse.body() ?: throw Exception("서버 오류-today_question null")


            Resource.Success(
                TodayAttendanceQuiz(
                    isPresentToday = isPresentToday == 1, //1일때 출석함 0일때 출석 안함
                    question = todayQuestion.question,
                    questionUUID = todayQuestion.questionUUID
                )
            )


        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun getWeekAttendanceInfo(hostUUID: String)
            : Resource<WeekAttendanceInfo> {
        return try {
            val continuousCountResponse = mainService.getContinuousAttendance(hostUUID)

            if (!continuousCountResponse.isSuccessful) {
                throw Exception("네트워크 오류-연속 출석일")
            }

            val continuousCount =
                continuousCountResponse.body() ?: throw Exception("네트워크 오류-연속 출석일")

            val weekAttendanceInfoResponse = mainService.getWeekAttendanceInfo(hostUUID)

            if (!weekAttendanceInfoResponse.isSuccessful) {
                throw Exception("네트워크 오류-주간출석 정보")
            }

            val weekItemListOri =
                weekAttendanceInfoResponse.body() ?: throw Exception("네트워크 오류-주간출석 정보")

            /*
            1이면 출석, 0이면 결석
             */
            val weekItemList: MutableList<WeekItem> =
                weekItemListOri.mapIndexed { index, isPresent ->
                    WeekItem.createWeekItem(
                        isPresent = isPresent == 1,
                        index = index
                    ) ?: throw Exception("네트워크 오류-주간출석 정보")
                }.toMutableList()

            for (i in weekItemList.size until 7) {
                WeekItem.createWeekItem(
                    index = i,
                    isPresent = null
                )?.let {
                    weekItemList.add(
                        it
                    )
                }

            }

            val weekAttendanceInfo = WeekAttendanceInfo(
                continuousCount = continuousCount,
                weekAttendance = weekItemList
            )

            Resource.Success(
                weekAttendanceInfo
            )

        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun getTodayQuestionMemo(
        hostUUID: String,
        questionUUID: String,
        question: String
    ): Resource<TodayQuestionMemo> {
        return try {

            val response = mainService.getTodayQuestionMemo(hostUUID, questionUUID)

            if (!response.isSuccessful) {
                throw Exception("네트워크 오류")
            }


            val todayQuestionMemo = response.body()

            if (todayQuestionMemo == null) {

                Resource.Success(
                    TodayQuestionMemo.createNewTodayQuestionMemo(
                        questionUUID,
                        question
                    )
                )
            } else {
                Resource.Success(
                    todayQuestionMemo
                )
            }


        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }


    override suspend fun updateTodayQuestionMemo(
        hostUUID: String,
        questionUUID: String,
        memo: String
    ): Boolean {
        return try {

            val response = mainService.updateMyMemoAboutQuestion(
                hostUUID = hostUUID,
                questionUUID = questionUUID,
                memo = Memo(memo)
            )

            if (!response.isSuccessful) {
                throw Exception("네트워크 오류")
            }

            val result = response.body() ?: throw Exception("네트워크 오류")

            result == 1

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getTodayQuestionCommentList(
        questionUUID: String,
        hostUUID: String
    ): Pager<Int, TodayQuestionComment> {
        val pagingSourceFactory = { CommentPagingSource(mainService, questionUUID, hostUUID) }

        return Pager(
                config = PagingConfig(
                    pageSize = PAGING_SIZE,
                    enablePlaceholders = false,
                    maxSize = PAGING_SIZE * 3
                ),
                pagingSourceFactory = pagingSourceFactory
            )
    }

    override suspend fun getMyTodayQuestionComment(
        questionUUID: String,
        hostUUID: String
    ): Result<TodayQuestionComment?> {

        return try {

            val response = mainService.getMyTodayQuestionComment(
                questionUUID, hostUUID
            )

            if(!response.isSuccessful) {
                throw Exception("내 댓글 조회 네트워크 오류")
            }

            val result = response.body()

            Result.success(result)

        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }

    }

    override suspend fun getTodayQuestionInfo(questionUUID: String): Result<TodayQuestion> {
        return try {

            val response = mainService.getTodayQuestionInfo(questionUUID)

            if(!response.isSuccessful) {
                throw Exception("오늘의 질문 정보 조회 네트워크 오류")
            }

            val result = response.body() ?: throw Exception("오늘의 질문 정보 조회 네트워크 오류")

            Result.success(result)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun changeCommentLikeCount(
        commentUUID: String,
        hostUUID: String
    ): Result<Int> {
        return try {

            val response = mainService.changeCommentLikeCount(commentUUID, hostUUID)

            if(!response.isSuccessful) {
                throw Exception("댓글 좋아요 변경 오류")
            }

            val result = response.body() ?: throw Exception("댓글 좋아요 변경 오류")

            if(result == -1) {
                throw Exception("댓글 좋아요 변경 오류-유저 식별 불가")
            }

            Result.success(result)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun createMyComment(
        questionUUID: String,
        hostUUID: String,
        comment: String
    ): Result<TodayQuestionComment> {
        return try {
            val commentObj = Comment(questionUUID, hostUUID, comment)

            val response = mainService.createMyComment(commentObj)

            if(!response.isSuccessful) {
                throw Exception("코멘트 생성 실패 네트워크 오류")
            }

            val result = response.body() ?: throw Exception("코멘트 생성 실패 네트워크 오류")

            Result.success(result)

        } catch(e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updateMyComment(
        commentUUID: String,
        questionUUID: String,
        hostUUID: String,
        comment: String
    ): Result<TodayQuestionComment> {
        return try {

            val commentObj = Comment(questionUUID, hostUUID, comment)

            val response = mainService.updateMyComment(commentUUID, commentObj)

            if(!response.isSuccessful) {
                throw Exception("코멘트 수정 실패 네트워크 오류")
            }

            val result = response.body() ?: throw Exception("코멘트 수정 실패 네트워크 오류")

            Result.success(result)

        } catch(e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun deleteMyComment(commentUUID: String, hostUUID: String): Result<Boolean> {
        return try {
            val response = mainService.deleteMyComment(commentUUID, hostUUID)

            if(!response.isSuccessful) {
                throw Exception("코멘트 삭제 실패 네트워크 오류")
            }

            val result = response.body() == 1

            if(result) {
                Result.success(true)
            } else{
                throw Exception("코멘트 삭제 실패 네트워크 오류")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getMyScriptList(hostUUID: String): Result<List<Script>> {
        return try {

            val response = mainService.getMyScriptList(hostUUID)

            if(!response.isSuccessful) {
                throw Exception("자기소개서 목록 네트워크 오류")
            }

            val result = response.body() ?: throw Exception("자기소개서 목록 네트워크 오류")



//            val result = mutableListOf<Script>()
//            repeat(5) {
//                result.add(Script.makeTestScript())
//            }

            Result.success(result)

        } catch(e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getJobRoleList(): Result<List<String>> {
        return try {
            val response = mainService.getJobRoleList()

            if(!response.isSuccessful) {
                throw Exception("자기소개서 직무 목록 네트워크 오류")
            }

            val result = response.body() ?: throw Exception("자기소개서 직무 목록 네트워크 오류")

//            val result = listOf("안드로이드 개발자", "자바 개발자", "시스템 엔지니어", "백엔드 개발자", "데이터 분석", "IOS 개발자", "프론트엔드 개발자")


            Result.success(result)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getScriptItemList(): Result<List<ScriptItem>> {
        return try {


            val response = mainService.getScriptItemList()

            if(!response.isSuccessful) {
                throw Exception("자기소개서 질문 목록 네트워크 오류")
            }

            val result = response.body() ?: throw Exception("자기소개서 질문 목록 네트워크 오류")

//            val result = mutableListOf<ScriptItem>()
//
//            for(i in 0 until 10) {
//                result.add(ScriptItem.createTestScriptItem())
//            }


            Result.success(result)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun createNewScript(script: Script): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateScript(script: Script): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyInterviewResultList(hostUUID: String): Result<List<InterviewResult>> {
        return try {

            val response = mainService.getMyInterviewList(hostUUID)

            if(!response.isSuccessful) {
                throw Exception("면접 결과 리스트 가져오기 네트워크 오류")
            }

            val result = response.body() ?: throw Exception("면접 결과 리스트 가져오기 네트워크 오류")

            Result.success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}