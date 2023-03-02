package com.capstone.Capstone2Project.repository

import com.capstone.Capstone2Project.data.resource.Resource
import com.google.firebase.auth.FirebaseUser


interface AuthRepository {

    val currentUser: FirebaseUser?
    suspend fun logIn(email: String, password: String): Resource<FirebaseUser>
    suspend fun signUp(name: String, email: String, password: String): Resource<FirebaseUser>
    fun logOut()
}