package com.capstone.Capstone2Project.data.database

import androidx.room.*
import com.capstone.Capstone2Project.data.model.InspiringKeyword
import kotlinx.coroutines.flow.Flow

@Dao
interface InspiringKeywordDao {

    @Query("SELECT * FROM keywords WHERE host_uuid = :hostUUID ORDER BY date DESC")
    fun getInspiringKeywords(hostUUID: String): List<InspiringKeyword>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspiringKeyword(inspiringKeyword: InspiringKeyword)

    @Query("DELETE FROM keywords WHERE host_uuid = :hostUUID")
    suspend fun deleteAllInspiringKeywords(hostUUID: String)

    @Delete
    suspend fun deleteInspiringKeyword(inspiringKeyword: InspiringKeyword)

}