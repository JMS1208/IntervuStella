package com.capstone.Capstone2Project.ui.screen.interesting.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.capstone.Capstone2Project.data.model.Topic
import com.capstone.Capstone2Project.data.model.fornetwork.Topics
import com.capstone.Capstone2Project.data.resource.DataState
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.repository.NetworkRepository
import com.capstone.Capstone2Project.ui.screen.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {

//    private val _userTopicsFlow: MutableStateFlow<Resource<List<Topic>>?> = MutableStateFlow(null)
//    val userTopicsFlow: StateFlow<Resource<List<Topic>>?> = _userTopicsFlow


    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _effect: MutableSharedFlow<Effect> = MutableSharedFlow()
    val effect: SharedFlow<Effect> = _effect


    fun fetchUserTopics(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        _state.update {
            it.copy(
                dataState = DataState.Loading()
            )
        }
        val result = repository.getUserTopics(hostUUID)

        if(result.isFailure || result.getOrNull() == null) {
            _effect.emit(
                Effect.ShowMessage(result.exceptionOrNull()?.message ?: "관심주제를 불러오지 못했어요")
            )
            return@launch
        }

        _state.update {
            it.copy(
                topics = result.getOrNull()!!,
                dataState = DataState.Normal
            )
        }

    }


    fun postUserTopics(hostUUID: String, topics: List<Topic>) =
        viewModelScope.launch(Dispatchers.IO) {

            val topicNameList = topics
                .filter { it.selected }
                .map { it.name }

            val result = repository.postTopics(
                hostUUID,
                Topics(topicNameList)
            )

            if(result.isFailure) {
                _effect.emit(
                    Effect.ShowMessage(result.exceptionOrNull()?.message ?: "잠시 후 다시 시도해주세요")
                )
                return@launch
            }

            _effect.emit(
                Effect.NavigateTo(ROUTE_HOME) {
                    popUpTo(ROUTE_HOME) {
                        inclusive = true
                    }
                }
            )

        }

    fun changeSelectedTopic(topic: Topic) = viewModelScope.launch {

        val newTopics = state.value.topics.map {
            if(it.name == topic.name) {
                it.copy(
                    selected = !it.selected
                )
            } else {
                it
            }
        }

        if(newTopics.isNotEmpty()) {
            _state.update {
                it.copy(
                    topics = newTopics
                )
            }
        }
    }


    data class State (
        val topics: List<Topic> = emptyList(),
        val dataState: DataState = DataState.Loading()
    )

    sealed class Effect {

        data class ShowMessage(val message: String): Effect()
        data class NavigateTo(val route: String, val builder: NavOptionsBuilder.()->Unit = {}): Effect()
    }
}