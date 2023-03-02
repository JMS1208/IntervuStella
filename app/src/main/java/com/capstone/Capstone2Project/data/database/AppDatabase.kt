package com.capstone.Capstone2Project.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.capstone.Capstone2Project.data.model.InspiringKeyword

const val APP_DATABASE_NAME = "app_database"

@Database(
    entities = [InspiringKeyword::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun keywordDao(): InspiringKeywordDao
}