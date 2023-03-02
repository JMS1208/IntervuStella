package com.capstone.Capstone2Project.repository

import com.capstone.Capstone2Project.data.model.InspiringKeyword
import kotlinx.coroutines.flow.Flow

interface AppDatabaseRepository {

    suspend fun getInspiringKeywords(hostUUID: String): List<InspiringKeyword>

    suspend fun insertInspiringKeyword(inspiringKeyword: InspiringKeyword)

    suspend fun deleteAllInspiringKeywords(hostUUID: String)

    suspend fun deleteInspiringKeyword(inspiringKeyword: InspiringKeyword)

}