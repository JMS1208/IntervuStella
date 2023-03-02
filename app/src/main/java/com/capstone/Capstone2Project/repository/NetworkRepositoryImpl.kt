package com.capstone.Capstone2Project.repository

import com.capstone.Capstone2Project.data.model.*
import com.capstone.Capstone2Project.data.model.TodayQuestion
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.utils.extensions.generateRandomText
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(
//    private val mainService: MainService
) : NetworkRepository {
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

            val questionItem = QuestionItem(UUID.randomUUID().toString(),"예시 질문 $it")

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
                uuid = UUID.randomUUID().toString(),
                name = "관심주제 예시"
            )

            val todayQuestion = TodayQuestion(
                topic = topic,
                question = "오늘의 질문 예시"
            )

            myTodayQuestions.add(todayQuestion)
        }

        return Resource.Success(myTodayQuestions)

    }


}