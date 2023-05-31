package com.capstone.Capstone2Project.ui.screen.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.Questionnaire
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.repository.AuthRepository
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterviewIntroViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _scriptsFlow: MutableStateFlow<Resource<List<Script>>?> = MutableStateFlow(null)
    val scriptsFlow: StateFlow<Resource<List<Script>>?> = _scriptsFlow

    private val _effect: MutableSharedFlow<Effect> = MutableSharedFlow()
    val effect: SharedFlow<Effect> = _effect

    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    init {
        fetchScripts()
    }

    fun fetchScripts() = viewModelScope.launch {
        _scriptsFlow.value = Resource.Loading

        val currentUUID = authRepository.currentUser?.uid

        try {
            val result: Result<List<Script>> = repository.getMyScriptList(currentUUID!!)

            if(result.isSuccess) {
                val scripts = result.getOrNull() ?: emptyList()
                _scriptsFlow.value = Resource.Success(scripts)
                _state.update {
                    it.copy(
                        reuseCheckList = List<Boolean>(scripts.size) {false}
                    )
                }

            } else {
                throw Exception(result.exceptionOrNull())
            }
        } catch(e: Exception) {
            e.printStackTrace()
            _scriptsFlow.value = Resource.Error(e)
        }

    }

    fun fetchQuestionnaire(
        script: Script,
        page: Int
    ) = viewModelScope.launch(Dispatchers.IO) {

        try {
            val reuse = state.value.reuseCheckList[page]

            val result = repository.getQuestionnaire(
                hostUUID = script.hostUUID,
                scriptUUID = script.uuid,
                jobRole = script.jobRole,
                reuse = reuse
            )

            if(result.isFailure) {
                _effect.emit(
                    Effect.ShowMessage("네트워크 오류가 발생했어요:(")
                )
                return@launch
            }

            _state.update {
                it.copy(
                    questionnaire = result.getOrNull()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _effect.emit(
                Effect.ShowMessage(e.message?:"인덱스 오류")
            )
        }



    }

    fun reuseCheck(page: Int, checked: Boolean) = viewModelScope.launch {
        val reuseCheckList = state.value.reuseCheckList.toMutableList()

        reuseCheckList[page] = checked

        _state.update {
            it.copy(
                reuseCheckList = reuseCheckList
            )
        }
    }

    data class State (
        var questionnaire: Questionnaire? = null,
        var reuseCheckList: List<Boolean> = emptyList()
    )

    sealed class Effect {
        data class ShowMessage(val message: String): Effect()

    }

}