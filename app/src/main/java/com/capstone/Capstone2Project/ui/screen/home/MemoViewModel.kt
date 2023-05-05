package com.capstone.Capstone2Project.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.fornetwork.Memo
import com.capstone.Capstone2Project.data.model.inapp.TodayQuestionMemo
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {

    private var _memoFlow: MutableStateFlow<State> = MutableStateFlow(State())

    val memoFlow: StateFlow<State> = _memoFlow

    fun loadMemo(hostUUID: String, questionUUID: String, question: String) = viewModelScope.launch(Dispatchers.IO) {

        _memoFlow.update {
            it.copy(
                showDialog = false,
                memo = Resource.Loading
            )
        }

        val result = repository.getTodayQuestionMemo(
            hostUUID,
            questionUUID,
            question
        )

        Log.e("TAG", "loadMemo: $result", )

        val showDialog = result !is Resource.Error

        _memoFlow.update {
            it.copy(
                showDialog = showDialog,
                memo = result
            )
        }

    }

    fun saveMemo(hostUUID: String, questionUUID: String, memo: String) = viewModelScope.launch(Dispatchers.IO) {

        val result = repository.updateTodayQuestionMemo(
            hostUUID, questionUUID, memo
        )


        val showDialog = !result


        _memoFlow.update {
            it.copy(
                showDialog = showDialog
            )
        }

        if(!result) {
            _memoFlow.update {
                it.copy(
                    showDialog = false,
                    memo = Resource.Error(Exception("메모 저장에 실패하였습니다"))
                )
            }
        }


    }

    fun closeMemoDialog() = viewModelScope.launch {
        _memoFlow.update {
            it.copy(
                showDialog = false
            )
        }
    }


    data class State(
        var showDialog: Boolean = false,
        var memo: Resource<TodayQuestionMemo>? = null
    )

}