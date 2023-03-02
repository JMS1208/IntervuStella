package com.capstone.Capstone2Project.di


import com.capstone.Capstone2Project.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository


    @Singleton
    @Binds
    abstract fun bindNetworkRepository(
        networkRepositoryImpl: NetworkRepositoryImpl
    ): NetworkRepository

    @Singleton
    @Binds
    abstract fun bindAppDatabaseRepository(
        appDatabaseRepositoryImpl: AppDatabaseRepositoryImpl
    ): AppDatabaseRepository
}