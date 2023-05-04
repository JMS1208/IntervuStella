package com.capstone.Capstone2Project.ui.screen.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.InspiringKeyword
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.data.model.InterviewScore
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.inapp.TodayQuestionMemo
//import com.capstone.Capstone2Project.data.model.TodayQuestion
import com.capstone.Capstone2Project.repository.AppDatabaseRepository
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModel() {

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    fun fetchMyScripts(hostUUID: String) = viewModelScope.launch {

        _state.update {
            it.copy(
                dataState = DataState.Loading
            )
        }

        val result = repository.getScripts(hostUUID)

        if (result.isSuccess) {
            _state.update {
                it.copy(
                    myScripts = result.getOrNull() ?: emptyList(),
                    dataState = DataState.Normal
                )
            }
        } else {
            _state.update {
                it.copy(
                    dataState = DataState.Error(
                        result.exceptionOrNull(),
                        message = "자기소개서 불러오기 실패"
                    ),
                )
            }
        }
    }

    fun fetchMyInterviewRecords(hostUUID: String) = viewModelScope.launch {
        _state.update {
            it.copy(
                dataState = DataState.Loading
            )
        }

        val result = repository.getInterviewRecords(hostUUID)

        if(result.isSuccess) {
            _state.update {
                it.copy(
                    myInterviewRecords = result.getOrNull() ?: emptyList(),
                    dataState = DataState.Normal
                )
            }
        } else {
            _state.update {
                it.copy(
                    dataState = DataState.Error(
                        result.exceptionOrNull(),
                        message = "면접 기록 불러오기 실패"
                    ),
                )
            }
        }
    }

    fun fetchMyInterviewScores(hostUUID: String) = viewModelScope.launch {
        _state.update {
            it.copy(
                dataState = DataState.Loading
            )
        }

        val result = repository.getInterviewScores(hostUUID)

        if(result.isSuccess) {
            _state.update {
                it.copy(
                    myInterviewScores = result.getOrNull() ?: emptyList(),
                    dataState = DataState.Normal
                )
            }
        } else {
            _state.update {
                it.copy(
                    dataState = DataState.Error(
                        result.exceptionOrNull(),
                        message = "면접 점수 기록 불러오기 실패"
                    )
                )
            }
        }

    }

    fun fetchMyTodayQuestionsMemo(hostUUID: String) = viewModelScope.launch {

        _state.update {
            it.copy(
                dataState = DataState.Loading
            )
        }

        val result = repository.getMyTodayQuestionsMemo(hostUUID)

        if (result.isSuccess) {

            _state.update {
                it.copy(
                    todayQuestionsMemo = result.getOrNull() ?: emptyList(),
                    dataState = DataState.Normal
                )
            }
        } else {
            _state.update {
                it.copy(
                    dataState = DataState.Error(
                        result.exceptionOrNull(),
                        message = "메모 리스트 불러오기 실패"
                    )
                )
            }
        }

    }


    fun fetchMyInspiringKeywords(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {

        _state.update {
            it.copy(
                dataState = DataState.Loading
            )
        }

        val result = appDatabaseRepository.getInspiringKeywords(hostUUID)

        _state.update {
            it.copy(
                inspiringKeywords = result,
                dataState = DataState.Normal
            )
        }

    }

    fun insertInspiringKeyword(inspiringKeyword: InspiringKeyword) =
        viewModelScope.launch(Dispatchers.IO) {
            appDatabaseRepository.insertInspiringKeyword(inspiringKeyword)
            fetchMyInspiringKeywords(inspiringKeyword.hostUUID)
        }

    fun deleteAllInspiringKeywords(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        appDatabaseRepository.deleteAllInspiringKeywords(hostUUID)
        fetchMyInspiringKeywords(hostUUID)
    }

    fun deleteInspiringKeyword(inspiringKeyword: InspiringKeyword) =
        viewModelScope.launch(Dispatchers.IO) {
            appDatabaseRepository.deleteInspiringKeyword(inspiringKeyword)
            fetchMyInspiringKeywords(inspiringKeyword.hostUUID)
        }

    fun closeDialog() {
        _state.update {
            it.copy(
                dialogState = DialogState.Nothing
            )
        }
    }

    fun showGitLinkDialog() {
        _state.update {
            it.copy(
                dialogState = DialogState.GitLinkDialog
            )
        }
    }

    fun showKeywordAddingDialog() {
        _state.update {
            it.copy(
                dialogState = DialogState.KeywordAddingDialog
            )
        }
    }

    data class State(
        var dialogState: DialogState = DialogState.Nothing,
        var dataState: DataState = DataState.Loading,
        var inspiringKeywords: List<InspiringKeyword> = emptyList(),
        var todayQuestionsMemo: List<TodayQuestionMemo> = emptyList(),
        var myScripts: List<Script> = emptyList(),
        var myInterviewScores: List<InterviewScore> = emptyList(),
        var myInterviewRecords: List<InterviewResult> = emptyList()
    )


    sealed class DataState {
        object Loading : DataState()
        data class Error(val e: Throwable?, val message: String? = null) : DataState()
        object Normal : DataState()
    }

    sealed class DialogState {
        object MemoDialog : DialogState()
        object Nothing : DialogState()

        object KeywordAddingDialog : DialogState()

        object GitLinkDialog : DialogState()

    }
}