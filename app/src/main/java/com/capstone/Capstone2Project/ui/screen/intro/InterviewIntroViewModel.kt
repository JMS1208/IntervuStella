package com.capstone.Capstone2Project.ui.screen.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.repository.AuthRepository
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterviewIntroViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _scriptsFlow: MutableStateFlow<Resource<List<Script>>?> = MutableStateFlow(null)
    val scriptsFlow: StateFlow<Resource<List<Script>>?> = _scriptsFlow

    init {
        fetchScripts()
    }

    fun fetchScripts() = viewModelScope.launch {
        _scriptsFlow.value = Resource.Loading

        val currentUUID = authRepository.currentUser?.uid

        try {
            val result: Result<List<Script>> = repository.getMyScriptList(currentUUID!!)

            if(result.isSuccess) {
                _scriptsFlow.value = Resource.Success(result.getOrNull()?: emptyList())
            } else {
                throw Exception(result.exceptionOrNull())
            }
        } catch(e: Exception) {
            e.printStackTrace()
            _scriptsFlow.value = Resource.Error(e)
        }

    }

}