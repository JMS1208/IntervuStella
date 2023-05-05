package com.capstone.Capstone2Project.ui.screen.comment

import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestion
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestionComment
import com.capstone.Capstone2Project.data.resource.DataState
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OthersAnswersViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    fun changeCommentLike(hostUUID: String, questionUUID: String, commentUUID: String) = viewModelScope.launch(Dispatchers.IO) {

        val result = repository.updateCommentLike(hostUUID, commentUUID)

        if(result.isSuccess) {

            fetchOthersComment(questionUUID)

        } else {
            _state.update {
                it.copy(
                    dataState = DataState.Error(result.exceptionOrNull())
                )
            }
        }

    }

    fun fetchOthersComment(questionUUID: String) = viewModelScope.launch(Dispatchers.IO) {

        _state.update {
            it.copy(
                dataState = DataState.Loading
            )
        }

        val result = repository.getTodayQuestionCommentList(questionUUID)

        _state.update {
            it.copy(
                isRefreshing = false,
                totalComments = result,
                dataState = DataState.Normal
            )
        }

    }

    fun fetchMyComment(questionUUID: String, hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {

        _state.update {
            it.copy(
                dataState = DataState.Loading
            )
        }

        val result = repository.getMyTodayQuestionComment(questionUUID, hostUUID)

        if(result.isSuccess) {
            _state.update {
                it.copy(
                    dataState = DataState.Normal,
                    myComment = result.getOrNull()
                )
            }
        } else {
            _state.update {
                it.copy(
                    dataState = DataState.Error(result.exceptionOrNull())
                )
            }
        }



    }


    fun refreshOthersAnswersData(questionUUID: String) = viewModelScope.launch(Dispatchers.IO) {

        _state.update {
            it.copy(
                isRefreshing = true
            )
        }

        fetchOthersComment(questionUUID)

    }

    fun fetchTodayQuestion(questionUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        _state.update {
            it.copy(
                dataState = DataState.Loading
            )
        }

        val result = repository.getTodayQuestionInfo(questionUUID)

        if(result.isSuccess) {
            _state.update {
                it.copy(
                    dataState = DataState.Normal,
                    todayQuestion = result.getOrNull()
                )
            }

        } else {
            _state.update {
                it.copy(
                    dataState = DataState.Error(result.exceptionOrNull())
                )
            }
        }
    }

    data class State(
        val totalComments: Pager<Int, TodayQuestionComment>? = null,
        val dataState: DataState = DataState.Loading,
        val isRefreshing: Boolean = false,
        val myComment: TodayQuestionComment? = null,
        val todayQuestion: TodayQuestion? = null
    )


}
