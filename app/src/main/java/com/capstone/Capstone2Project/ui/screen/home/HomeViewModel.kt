package com.capstone.Capstone2Project.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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

    private var _state: MutableStateFlow<State?> = MutableStateFlow(null)
    val state: StateFlow<State?> = _state

    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    private val _event = MutableSharedFlow<Event>()
    val event: SharedFlow<Event> = _event



    fun checkAttendance(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = repository.checkAttendance(hostUUID)
        when (result) {
            is Resource.Error -> {
                _effect.emit(
                    Effect.ShowMessage(
                        result.error?.message ?: "알 수 없는 오류가 발생했습니다",
                        type = Effect.ShowMessage.MessageType.Error
                    )
                )
            }
            Resource.Loading -> {
                _effect.emit(
                    Effect.ShowMessage(
                        "알 수 없는 오류가 발생했습니다",
                        type = Effect.ShowMessage.MessageType.Error
                    )
                )
            }
            is Resource.Success -> {
                _state.update {

                    it?.copy(
                        todayAttendanceQuiz = it.todayAttendanceQuiz.successOrNull()?.let { taq ->
                            Resource.Success(
                                taq.copy(
                                    isPresentToday = result.data
                                )
                            )
                        } ?: Resource.Error(Exception("오류 발생"))
                    )


                }
            }
        }

    }


    fun getWeekAttendanceInfo(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        _state.update {
            val result = repository.getWeekAttendanceInfo(hostUUID)
            Log.e("TAG", "getWeekAttendanceInfo: $result", )
            it?.copy(
                weekAttendanceInfo = result
            )
        }
    }


    fun fetchAllHomeData(hostUUID: String) {
        _state.update {
            State()
        }
        getTodayQuestionAttendance(hostUUID, null)
        getUserTopics(hostUUID)
        getWeekAttendanceInfo(hostUUID)
    }

    fun getUserTopics(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        _state.update {
            val result = repository.getUserTopics(hostUUID)
            it?.copy(
                topics = result
            )
        }
    }

    fun getTodayQuestionAttendance(hostUUID: String, currentQuestionUUID: String?) =
        viewModelScope.launch(Dispatchers.IO) {

            _state.update {

                val result = repository.getTodayQuestionAttendance(hostUUID, currentQuestionUUID)

                it?.copy(
                    todayAttendanceQuiz = result
                )

            }

        }


//    fun getTodayQuestionMemo(hostUUID: String, questionUUID: String, question: String) =
//        viewModelScope.launch(Dispatchers.IO) {
//
//            _state.update {
//
//                val result = repository.getTodayQuestionMemo(hostUUID, questionUUID, question)
//
//                it?.copy(
//                    todayQuestionMemo = result
//                )
//            }
//        }

//

    data class State(
        var topics: Resource<List<Topic>> = Resource.Loading,
        var todayAttendanceQuiz: Resource<TodayAttendanceQuiz> = Resource.Loading,
        var weekAttendanceInfo: Resource<WeekAttendanceInfo> = Resource.Loading,
//        var todayQuestionMemo: Resource<TodayQuestionMemo> = Resource.Loading
    )
    /*
    필요한 정보가
    1. 깃허브 사용언어
    2. 사용자 관심주제
    3. 오늘의 질문
    4. 출석 정보 (연속 출석일, 일주일 현황)
     */

    sealed class Effect {
        data class ShowMessage(val message: String, val type: MessageType) : Effect() {
            enum class MessageType {
                Loading,
                Normal,
                Error
            }
        }

    }

    sealed class Event {
        data class ShowMemoDialog(val todayQuestionMemo: TodayQuestionMemo) : Event()
    }
}