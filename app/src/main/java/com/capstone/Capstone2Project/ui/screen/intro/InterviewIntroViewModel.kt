package com.capstone.Capstone2Project.ui.screen.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.Questionnaire
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.navigation.ROUTE_INTERVIEW_GUIDE
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

//    init {
//        fetchScripts()
//    }

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
            _state.update {
                it.copy(
                    networkState = NetworkState.Loading("질문지를 생성하고 있어요 :)")
                )
            }
            val reuse = state.value.reuseCheckList[page]

            val result = repository.getQuestionnaire(
                hostUUID = script.hostUUID,
                scriptUUID = script.uuid,
                reuse = reuse
            )

            if(result.isFailure) {
                _effect.emit(
                    Effect.ShowMessage("네트워크 오류가 발생했어요:(")
                )
                return@launch
            }

            result.getOrNull()?: throw Exception("네트워크 오류가 발생했어요:(")

            if(result.getOrNull()?.questions?.isEmpty() == true) {
                throw Exception("잠시 후 다시 시도해주세요")
            }

            val questionnaireJsonString = result.getOrNull()?.toJsonString() ?: throw Exception("형식이 올바르지 않습니다")

            _state.update {
                it.copy(
                    networkState = NetworkState.Nothing
                )
            }

            val route = "$ROUTE_INTERVIEW_GUIDE?questionnaire={questionnaire}".replace(
                oldValue = "{questionnaire}",
                newValue = questionnaireJsonString
            )

            _effect.emit(
                Effect.NavigateTo(route)
            )

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
        var reuseCheckList: List<Boolean> = emptyList(),
        var networkState: NetworkState = NetworkState.Normal
    )

    sealed class NetworkState {
        //Normal일때는 다이얼로그 띄워줌
        object Normal: NetworkState()

        //Nothing 일때는 다이얼로그 안 띄워줌
        object Nothing: NetworkState()
        data class Loading(val message: String): NetworkState()
        data class Error(val message: String): NetworkState()
    }

    sealed class Effect {
        data class ShowMessage(val message: String): Effect()

        data class NavigateTo(val route: String): Effect()
    }

}