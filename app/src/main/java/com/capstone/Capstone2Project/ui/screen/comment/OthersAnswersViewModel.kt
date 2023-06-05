package com.capstone.Capstone2Project.ui.screen.comment

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.filter
import androidx.paging.insertSeparators
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestion
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestionComment
import com.capstone.Capstone2Project.data.resource.DataState
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OthersAnswersViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private var _effect: MutableStateFlow<Effect> = MutableStateFlow(Effect())
    val effect: StateFlow<Effect> = _effect

    private fun updateMyComment(commentUUID: String, questionUUID: String, hostUUID: String, comment: String) = viewModelScope.launch(Dispatchers.IO) {

        val result = repository.updateMyComment(commentUUID, questionUUID, hostUUID, comment)

        if(result.isSuccess) {
             _state.update {
                 it.copy(
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

    private fun createMyComment(questionUUID: String, hostUUID: String, comment: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = repository.createMyComment(questionUUID, hostUUID, comment)

        if(result.isSuccess) {
            _state.update {
                it.copy(
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

    fun sendMyComment(comment: String, questionUUID: String, hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {

        val myComment = state.value.myComment

        if(myComment == null) { //새로 생성하기
            createMyComment(questionUUID, hostUUID, comment)
        } else {
            updateMyComment(myComment.commentUUID, questionUUID, hostUUID, comment)
        }
    }

    fun deleteMyComment(commentUUID: String, hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = repository.deleteMyComment(commentUUID, hostUUID)

        if(result.isSuccess) {
            _state.update {
                it.copy(
                    myComment = null
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

    fun changeCommentLike(hostUUID: String, questionUUID: String, commentUUID: String) = viewModelScope.launch(Dispatchers.IO) {

        val result = repository.changeCommentLikeCount(commentUUID, hostUUID)

        if(result.isSuccess) {

            fetchMyComment(questionUUID, hostUUID)
            fetchOthersComment(questionUUID, hostUUID, needLoading = false)

        } else {
            _state.update {
                it.copy(
                    dataState = DataState.Error(result.exceptionOrNull())
                )
            }
        }

    }

    fun fetchOthersComment(questionUUID: String, hostUUID: String, needLoading: Boolean = true) = viewModelScope.launch(Dispatchers.IO) {

        if(needLoading) {
            _state.update {
                it.copy(
                    dataState = DataState.Loading()
                )
            }
        }


        val result = repository.getTodayQuestionCommentList(questionUUID, hostUUID)

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
                dataState = DataState.Loading()
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


    fun refreshOthersAnswersData(questionUUID: String, hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {

        _state.update {
            it.copy(
                isRefreshing = true
            )
        }

        fetchOthersComment(questionUUID, hostUUID)

    }

    fun fetchTodayQuestion(questionUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        _state.update {
            it.copy(
                dataState = DataState.Loading()
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

    fun openMyCommentDialog(myComment: TodayQuestionComment?) = viewModelScope.launch {
        _effect.update {
            it.copy(
                dialogState = DialogState.MyCommentDialog(myComment = myComment)
            )
        }
    }

    fun closeMyCommentDialog() = viewModelScope.launch {
        _effect.update {
            it.copy(
                dialogState = DialogState.Nothing
            )
        }
    }



    data class State(
        val totalComments: Pager<Int, TodayQuestionComment>? = null,
        val dataState: DataState = DataState.Loading(),
        val isRefreshing: Boolean = false,
        val myComment: TodayQuestionComment? = null,
        val todayQuestion: TodayQuestion? = null,
        val scrollState: LazyListState? = null
    )

    data class Effect(
        var dialogState: DialogState = DialogState.Nothing
    )

    sealed class DialogState {
        data class MyCommentDialog(
            var myComment: TodayQuestionComment?
        ) : DialogState()
        object Nothing : DialogState()

    }


}
