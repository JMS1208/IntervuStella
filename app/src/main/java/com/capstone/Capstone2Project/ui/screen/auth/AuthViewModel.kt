package com.capstone.Capstone2Project.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.TestUser
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    private val _loginFlow = MutableStateFlow<Resource<TestUser>?> (null)
    val loginFlow: StateFlow<Resource<TestUser>?> = _loginFlow

    private val _signupFlow = MutableStateFlow<Resource<TestUser>?>(null)
    val signupFlow: StateFlow<Resource<TestUser>?> = _signupFlow


    fun login(email: String, password: String) = viewModelScope.launch {
        _loginFlow.value = Resource.Loading
        val result: Resource<TestUser> = repository.logIn(email, password)
        _loginFlow.value = result
    }

}