package com.capstone.Capstone2Project.repository

import com.capstone.Capstone2Project.data.model.TestUser
import com.capstone.Capstone2Project.data.resource.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(

): AuthRepository {
    override suspend fun logIn(email: String, password: String): Resource<TestUser> {
        return try {
            val isAdminUser = TestUser.isAdminUser(email, password)
            if (isAdminUser) {
                Resource.Success(TestUser.adminUser)
            } else {
                //TODO{로그인}
                throw Exception()
            }
        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun signUp(name: String, email: String, password: String): Resource<TestUser> {
        return try {
            val isAdminUser = TestUser.isAdminUser(email, password)

            if(isAdminUser) {
                Resource.Success(TestUser.adminUser)
            } else {
                //TODO{회원가입}
                throw Exception()
            }

        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override fun logOut() {
        //TODO(로그아웃)
    }
}