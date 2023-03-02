package com.capstone.Capstone2Project.repository

import com.capstone.Capstone2Project.data.database.AppDatabase
import com.capstone.Capstone2Project.data.model.InspiringKeyword
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDatabaseRepositoryImpl @Inject constructor(
    private val db: AppDatabase
): AppDatabaseRepository {

    override suspend fun getInspiringKeywords(hostUUID: String): List<InspiringKeyword> {
        return db.keywordDao().getInspiringKeywords(hostUUID)
    }

    override suspend fun insertInspiringKeyword(inspiringKeyword: InspiringKeyword) {
        db.keywordDao().insertInspiringKeyword(inspiringKeyword)
    }

    override suspend fun deleteAllInspiringKeywords(hostUUID: String) {
        db.keywordDao().deleteAllInspiringKeywords(hostUUID)
    }

    override suspend fun deleteInspiringKeyword(inspiringKeyword: InspiringKeyword) {
        db.keywordDao().deleteInspiringKeyword(inspiringKeyword = inspiringKeyword)
    }


}