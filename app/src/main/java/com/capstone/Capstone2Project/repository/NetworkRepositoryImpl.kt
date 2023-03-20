package com.capstone.Capstone2Project.repository

import com.capstone.Capstone2Project.data.model.*
import com.capstone.Capstone2Project.data.model.TodayQuestion
import com.capstone.Capstone2Project.data.model.response.InterviewDataResponse
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.ui.screen.interesting.topic.TopicViewModel
import com.capstone.Capstone2Project.ui.screen.othersanswers.AnswerData
import com.capstone.Capstone2Project.ui.screen.othersanswers.OthersAnswersData
import com.capstone.Capstone2Project.ui.screen.othersanswers.QuestionData
import com.capstone.Capstone2Project.utils.extensions.generateRandomText
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(
//    private val mainService: MainService
) : NetworkRepository {
    /*
    override suspend fun getDefaultTopics(): Resource<List<Topic>> {
        //TODO(mainService로 부터 받아오기)
        val topics = listOf(
            Topic(name = "운영체제"),
            Topic(name = "네트워크"),
            Topic(name = "데이터베이스"),
            Topic(name = "알고리즘"),
            Topic(name = "자료구조"),
            Topic(name = "프로그래밍 기초"),
            Topic(name = "JAVA"),
            Topic(name = "전산 기본")
        )
        return Resource.Success(topics)
    }

     */

    override suspend fun getScripts(hostUUID: String): Resource<List<Script>> {
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

        return Resource.Success(scripts)

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
            scriptUUID = UUID.randomUUID().toString()
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

    override suspend fun getInterviewLogs(hostUUID: String): Resource<List<InterviewLog>> {

        val interviewLogList = mutableListOf<InterviewLog>()

        for (i in 0 until 10) {
            val scriptUUID = UUID.randomUUID().toString()

            val interviewLogLines = mutableListOf<InterviewLogLine>()

            for (j in 0 until 100) {
                val interviewLogLine = InterviewLogLine(
                    date = System.currentTimeMillis(),
                    progress = j,
                    questionItem = QuestionItem(uuid = UUID.randomUUID().toString(), "질문 예시"),
                    logLine = LogLine(
                        type = LogLine.Type.Error,
                        message = "로그 메시지 예시 $j",
                    )
                )
                interviewLogLines.add(interviewLogLine)
            }

            val interviewLog = InterviewLog(
                hostUUID = hostUUID,
                scriptUUID = scriptUUID,
                interviewUUID = UUID.randomUUID().toString(),
                logLines = interviewLogLines,
                scriptName = "스크립트 제목 예시 $i",
                date = System.currentTimeMillis()
            )


            interviewLogList.add(interviewLog)
        }


        return Resource.Success(interviewLogList)


    }

    override suspend fun getInterviewScores(hostUUID: String): Resource<List<InterviewScore>> {

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

        return Resource.Success(interviewScores)

    }

    override suspend fun getMyTodayQuestions(hostUUID: String): Resource<List<TodayQuestion>> {

        val myTodayQuestions = mutableListOf<TodayQuestion>()

        for (i in 0 until 10) {
            val topic = Topic(
                //uuid = UUID.randomUUID().toString(),
                name = "관심주제 예시",
                selected = false
            )

            val todayQuestion = TodayQuestion(
                topic = topic,
                question = "오늘의 질문 예시"
            )

            myTodayQuestions.add(todayQuestion)
        }

        return Resource.Success(myTodayQuestions)

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

        val interviewResult = InterviewResult(
            uuid = interviewUUID,
            scriptUUID = UUID.randomUUID().toString(),
            interview_date = System.currentTimeMillis(),
            score = 678,
            feedBack = generateRandomText(500),
            duration = 678,
            newAchievement = newAchievements

        )

        return Resource.Success(interviewResult)
    }

    override suspend fun getOthersAnswersData(questionUUID: String): Resource<OthersAnswersData> {
        val myAnswer = AnswerData(
            uuid = UUID.randomUUID().toString(),
            nickName = "닉네임",
            email = "ad***@ad***.com",
            content = "프로세스와 스레드의 차이는",
            like = false,
            likeCount = 50
        )

        val questionData = QuestionData(
            field = "운영체제",
            question = "프로세스와 스레드의 차이는 무엇인가요?"
        )

        val othersAnswers = mutableListOf<AnswerData>()

        for (i in 0 until 16) {

            val othersAnswer = AnswerData(
                uuid = UUID.randomUUID().toString(),
                nickName = "닉네임$i",
                email = "ad$i***@ad***.com",
                content = "프로세스와 스레드의 차이는",
                like = false,
                likeCount = (0..100).random()
            )

            othersAnswers.add(othersAnswer)
        }

        return Resource.Success(
            OthersAnswersData(
                myAnswer, questionData, othersAnswers
            )
        )
    }


    override suspend fun updateLikeForAnswerData(
        answerUUID: String,
        like: Boolean
    ): Resource<String> {
        //TODO()
        return Resource.Success("성공")
    }

    override suspend fun isUserPresent(hostUUID: String): Boolean {
        return false
    }

    override suspend fun checkAttendance(hostUUID: String) {

    }

    override suspend fun getUserTopics(hostUUID: String): Resource<List<Topic>> {
        return Resource.Success(listOf(
            Topic(name = "운영체제", selected = false),
            Topic(name = "네트워크", selected = true),
            Topic(name = "데이터베이스", selected = true),
            Topic(name = "알고리즘", selected = true),
            Topic(name = "자료구조", selected = false),
            Topic(name = "프로그래밍 기초", selected = false),
            Topic(name = "JAVA", selected = false),
            Topic(name = "전산 기본", selected = false)
        ))
    }

}