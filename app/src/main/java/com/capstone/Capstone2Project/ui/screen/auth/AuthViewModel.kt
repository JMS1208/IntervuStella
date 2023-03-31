package com.capstone.Capstone2Project.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.repository.AuthRepository
import com.capstone.Capstone2Project.repository.NetworkRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    private val _signupFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signupFlow: StateFlow<Resource<FirebaseUser>?> = _signupFlow

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    init {
        repository.currentUser?.let {
            _loginFlow.value = Resource.Success(it)
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginFlow.value = Resource.Loading
        val result: Resource<FirebaseUser> = repository.logIn(email, password)
        _loginFlow.value = result
    }

    fun logout() {
        repository.logOut()
        _loginFlow.value = null
        _signupFlow.value = null
    }

    fun signup(name: String, email: String, password: String) = viewModelScope.launch {
        _signupFlow.value = Resource.Loading
        val result: Resource<FirebaseUser> = repository.signUp(name, email, password)
        _signupFlow.value = result
    }


}