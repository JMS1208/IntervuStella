package com.capstone.Capstone2Project.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.capstone.Capstone2Project.data.model.*
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
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(
    private val mainService: MainService
) : NetworkRepository {

    override suspend fun getScripts(hostUUID: String): Result<List<Script>> {
        //TODO(uuid로 host 참고해서 자소서 쓴 목록 가져오기)
        val scripts = mutableListOf<Script>()

        repeat(5) {

            val scriptUUID = UUID.randomUUID().toString()

            val scriptItems = mutableListOf<ScriptItem>()

            repeat(4) {

                val tips = listOf("팁 예시1111111111", "팁 예시22222222222", "팁 예시3333333333")

                val scriptItem = ScriptItem(
                    scriptUUID = scriptUUID,
                    itemUUID = UUID.randomUUID().toString(),
                    question = "예시 질문 $it",
                    answer = "예시 답 $it ${generateRandomText(200)}",
                    tips = tips,
                    maxLength = (2..4).random() * 100
                )

                scriptItems.add(scriptItem)
            }

            val script = Script(
                uuid = scriptUUID,
                host = hostUUID,
                date = System.currentTimeMillis(),
                name = "자소서 이름 예시",
                scriptItems = scriptItems,
                questionnaireState = true
            )

            scripts.add(script)
        }

        return Result.success(scripts)

    }

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


    override suspend fun createEmptyScript(hostUUID: String): Resource<Script> {

        val tips = listOf("팁 예시1111111111", "팁 예시22222222222", "팁 예시3333333333")

        val scriptUUID = UUID.randomUUID().toString()

        val items = listOf(
            ScriptItem(question = "예시 질문", tips = tips, maxLength = 300, scriptUUID = scriptUUID),
            ScriptItem(question = "예시 질문", tips = tips, maxLength = 500, scriptUUID = scriptUUID),
            ScriptItem(question = "예시 질문", tips = tips, maxLength = 300, scriptUUID = scriptUUID),
            ScriptItem(question = "예시 질문", tips = tips, maxLength = 500, scriptUUID = scriptUUID),
        )

        val script = Script(
            host = hostUUID,
            date = System.currentTimeMillis(),
            name = "자기소개서",
            questionnaireState = false,
            scriptItems = items
        )

        return Resource.Success(script)
    }

    override suspend fun getInterviewRecords(hostUUID: String): Result<List<InterviewResult>> {

        val interviewRecords = mutableListOf<InterviewResult>()

        for (i in 0 until 10) {

            val interviewRecord = InterviewResult.createTestInterviewResult()

            interviewRecords.add(interviewRecord)

        }

        return Result.success(interviewRecords)

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




    override suspend fun updateCommentLike(hostUUID: String, commentUUID: String): Result<String> {
        //TODO()
        return Result.success("성공")
    }


    override suspend fun checkAttendance(hostUUID: String): Resource<Boolean> {
        return try {
            //TODO 원래 통신해야하는데, 임시용
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

            for (i in weekItemList.size - 1 until 7) {
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
        questionUUID: String
    ): Pager<Int, TodayQuestionComment> {
        val pagingSourceFactory = { CommentPagingSource(mainService, questionUUID) }

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
}