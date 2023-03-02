package com.capstone.Capstone2Project.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keywords")
data class InspiringKeyword(
    @ColumnInfo(name = "host_uuid")
    val hostUUID: String,
    @ColumnInfo(name = "date")
    val date: Long,
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "keyword_uuid")
    val keywordUUID: String,
    @ColumnInfo(name = "keyword")
    val keyword: String
)
