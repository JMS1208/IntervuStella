package com.capstone.Capstone2Project.repository

import com.capstone.Capstone2Project.data.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun logIn(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }


    override suspend fun signUp(name: String, email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result =  firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
            Resource.Success(result.user!!)
        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override fun logOut() {
        firebaseAuth.signOut()
    }
}