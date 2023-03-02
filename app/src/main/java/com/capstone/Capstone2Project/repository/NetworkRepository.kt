package com.capstone.Capstone2Project.repository

import com.capstone.Capstone2Project.data.model.*
import com.capstone.Capstone2Project.data.model.TodayQuestion
import com.capstone.Capstone2Project.data.resource.Resource

interface NetworkRepository {
    suspend fun getDefaultTopics(): Resource<List<Topic>>
    suspend fun getScripts(hostUUID: String): Resource<List<Script>>
    suspend fun getCustomQuestionnaire(script: Script): Resource<CustomQuestionnaire>
//    suspend fun getScriptPaper(): Resource<ScriptPaper>
    suspend fun createEmptyScript(hostUUID: String): Resource<Script>
    suspend fun getInterviewLogs(hostUUID: String): Resource<List<InterviewLog>>
    suspend fun getInterviewScores(hostUUID: String): Resource<List<InterviewScore>>
    suspend fun getMyTodayQuestions(hostUUID: String): Resource<List<TodayQuestion>>
}