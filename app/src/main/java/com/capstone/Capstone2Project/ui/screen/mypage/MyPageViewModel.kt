package com.capstone.Capstone2Project.ui.screen.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.InspiringKeyword
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.data.model.InterviewScore
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.inapp.TodayQuestionMemo
import com.capstone.Capstone2Project.data.resource.DataState
//import com.capstone.Capstone2Project.data.model.TodayQuestion
import com.capstone.Capstone2Project.repository.AppDatabaseRepository
import com.capstone.Capstone2Project.repository.NetworkRepository
import com.capstone.Capstone2Project.ui.screen.home.HomeViewModel
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
class MyPageViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val appDatabaseRepository: AppDatabaseRepository
) : ViewModel() {

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private var _effect: MutableSharedFlow<Effect> = MutableSharedFlow()
    val effect: SharedFlow<Effect> = _effect

    fun fetchMyScripts(hostUUID: String) = viewModelScope.launch {

        _state.update {
            it.copy(
                dataState = DataState.Loading()
            )
        }

        val result = repository.getMyScriptList(hostUUID)

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
                dataState = DataState.Loading()
            )
        }

        val result = repository.getMyInterviewResultList(hostUUID)

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
                dataState = DataState.Loading()
            )
        }

        val result = repository.getInterviewScore(hostUUID)

        if(result.isSuccess) {
            _state.update {
                it.copy(
                    myRankRecords = result.getOrNull(),
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
                dataState = DataState.Loading()
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
                dataState = DataState.Loading()
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

    fun scriptClicked(script: Script) = viewModelScope.launch {
        _state.update {
            it.copy(
                dialogState = DialogState.ScriptDialog(script)
            )
        }
    }

    fun deleteScript(script: Script) = viewModelScope.launch {

        /*
        삭제 제대로 됐으면 다이얼로그 닫고 메시지 띄우고, state.myScripts에서 제거
        삭제 제대로 안 됐으면 다이얼로그 유지하고 메시지 띄우기
         */
        val result = repository.deleteScript(script.uuid, script.hostUUID)

        if(result.isFailure) {
            _effect.emit(
                Effect.ShowMessage(result.exceptionOrNull()?.message ?:"잠시 후 다시 시도해주세요")
            )
        } else {

            val newScripts = state.value.myScripts.filter {
                it.uuid != script.uuid
            }

            _effect.emit(
                Effect.ShowMessage("삭제 되었습니다")
            )
            _state.update {
                it.copy(
                    dialogState = DialogState.Nothing,
                    myScripts = newScripts
                )
            }
        }
    }

    data class State(
        var dialogState: DialogState = DialogState.Nothing,
        var dataState: DataState = DataState.Loading(),
        var inspiringKeywords: List<InspiringKeyword> = emptyList(),
        var todayQuestionsMemo: List<TodayQuestionMemo> = emptyList(),
        var myScripts: List<Script> = emptyList(),
        var myRankRecords: InterviewScore? = null,
        var myInterviewRecords: List<InterviewResult> = emptyList()
    )

    sealed class Effect {
        data class ShowMessage(val message: String) : Effect()
    }


    sealed class DialogState {
        object MemoDialog : DialogState()
        object Nothing : DialogState()

        object KeywordAddingDialog : DialogState()

        object GitLinkDialog : DialogState()

        data class ScriptDialog(val script: Script): DialogState()

    }
}