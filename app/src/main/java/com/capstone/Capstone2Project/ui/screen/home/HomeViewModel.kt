package com.capstone.Capstone2Project.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavOptionsBuilder
import com.capstone.Capstone2Project.data.model.GitLanguage
import com.capstone.Capstone2Project.data.model.Topic
import com.capstone.Capstone2Project.data.model.inapp.TodayAttendanceQuiz
import com.capstone.Capstone2Project.data.model.inapp.TodayQuestionMemo
import com.capstone.Capstone2Project.data.model.inapp.WeekAttendanceInfo
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.data.resource.successOrNull
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect


    fun checkAttendance(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = repository.checkAttendance(hostUUID)

        if(result.isFailure || result.getOrNull() == null) {
            _effect.emit(
                Effect.ShowMessage(result.exceptionOrNull()?.message ?: "잠시 후 다시 시도해주세요")
            )
            return@launch
        }

        _state.update {
            it.copy(
                todayAttendanceQuiz = it.todayAttendanceQuiz.copy(
                    isPresentToday = result.getOrNull()!!
                )
            )
        }

    }


    private fun getWeekAttendanceInfo(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {

        val result = repository.getWeekAttendanceInfo(hostUUID)

        if(result.isFailure) {
            _effect.emit(
                Effect.ShowMessage(result.exceptionOrNull()?.message ?:"주간 출석 정보 가져오기 실패")
            )
            return@launch
        }

        result.getOrNull()?.let { weekAttendanceInfo ->
            _state.update {
                it.copy(
                    weekAttendanceInfo = weekAttendanceInfo
                )
            }
        }


    }


    fun fetchAllHomeData(hostUUID: String) {
        _state.update {
            State()
        }
        getTodayQuestionAttendance(hostUUID, null)
        getUserTopics(hostUUID)
        getWeekAttendanceInfo(hostUUID)
        getGitLanguage(hostUUID)
    }

    private fun getGitLanguage(hostUUID: String) = viewModelScope.launch {

        val result = repository.getGitLanguage(hostUUID)

        if(result.isFailure) {
            _effect.emit(
                Effect.ShowMessage(result.exceptionOrNull()?.message?:"깃허브 사용언어 가져오기 실패")
            )
            return@launch
        }

        _state.update {
            it.copy(
                gitLanguage = result.getOrNull() ?: emptyList()
            )
        }
    }

    private fun getUserTopics(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {

        val result = repository.getUserTopics(hostUUID)

        if(result.isFailure || result.getOrNull() == null) {

            _effect.emit(
                Effect.ShowMessage(result.exceptionOrNull()?.message ?: "관심주제에 대한 정보를 가져오지 못했어요")
            )

            return@launch
        }

        _state.update {
            it.copy(
                topics = result.getOrNull()!!
            )
        }
    }

    fun getTodayQuestionAttendance(hostUUID: String, currentQuestionUUID: String?) =
        viewModelScope.launch(Dispatchers.IO) {

            val result = repository.getTodayQuestionAttendance(hostUUID, currentQuestionUUID)

            if(result.isFailure || result.getOrNull() == null) {
                _effect.emit(
                    Effect.ShowMessage(result.exceptionOrNull()?.message ?: "오늘의 질문에 대한 정보를 가져오지 못했어요")
                )
                return@launch
            }

            _state.update {
                it.copy(
                    todayAttendanceQuiz = result.getOrNull()!!
                )
            }

        }


    data class State(
        var topics: List<Topic> = emptyList(),
        var todayAttendanceQuiz: TodayAttendanceQuiz = TodayAttendanceQuiz(false, "질문을 가져오지 못했어요", ""),
        var weekAttendanceInfo: WeekAttendanceInfo = WeekAttendanceInfo(emptyList(), 0),
        var gitLanguage: List<GitLanguage> = emptyList()
    )
    /*
    필요한 정보가
    1. 깃허브 사용언어
    2. 사용자 관심주제
    3. 오늘의 질문
    4. 출석 정보 (연속 출석일, 일주일 현황)
     */

    sealed class Effect {
        data class ShowMessage(val message: String) : Effect()

        data class NavigateTo(val route: String, val builder: NavOptionsBuilder.() -> Unit = {}): Effect()

    }

}