package com.capstone.Capstone2Project.repository

import com.capstone.Capstone2Project.data.model.UserInfo
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.network.service.MainService
import com.capstone.Capstone2Project.utils.etc.Name.REQUEST_SUCCESS
import com.capstone.Capstone2Project.utils.etc.Name.REQUEST_FAILURE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val mainService: MainService
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun logIn(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }


    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            val user = result?.user ?: throw Exception("파이어베이스 오류")

            user.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            )

            val response = mainService.postUserInfo(
                user.uid,
                UserInfo(
                    name = name,
                    email = email
                )
            ) //0:실패, 1:성공


            with(response) {
                if(isSuccessful) {
                    when(body()) {
                        REQUEST_FAILURE-> {
                            throw Exception("이미 존재하는 회원")
                        }
                        REQUEST_SUCCESS-> {
                            Resource.Success(result.user!!)
                        }
                        else -> throw Exception("알 수 없는 오류")
                    }

                } else {
                    throw Exception("서버 오류")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override fun logOut() {
        firebaseAuth.signOut()
    }
}