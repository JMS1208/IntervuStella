package com.capstone.Capstone2Project.repository

import com.capstone.Capstone2Project.data.model.TestUser
import com.capstone.Capstone2Project.data.resource.Resource


interface AuthRepository {
    suspend fun logIn(email: String, password: String): Resource<TestUser>
    suspend fun signUp(name: String, email: String, password: String): Resource<TestUser>
    fun logOut()
}