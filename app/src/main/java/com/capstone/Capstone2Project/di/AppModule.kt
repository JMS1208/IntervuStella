package com.capstone.Capstone2Project.di

import android.content.Context
import androidx.room.Room
import com.capstone.Capstone2Project.data.database.APP_DATABASE_NAME
import com.capstone.Capstone2Project.data.database.AppDatabase
import com.capstone.Capstone2Project.utils.file.FileManager
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()



    //Room Database
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            APP_DATABASE_NAME
        ).build()
    }


}