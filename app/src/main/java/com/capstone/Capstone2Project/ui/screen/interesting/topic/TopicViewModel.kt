package com.capstone.Capstone2Project.ui.screen.interesting.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.Topic
import com.capstone.Capstone2Project.data.model.fornetwork.Topics
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {

    private val _userTopicsFlow: MutableStateFlow<Resource<List<Topic>>?> = MutableStateFlow(null)
    val userTopicsFlow: StateFlow<Resource<List<Topic>>?> = _userTopicsFlow


    fun fetchUserTopics(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        _userTopicsFlow.value = Resource.Loading
        val result = repository.getUserTopics(hostUUID)
        _userTopicsFlow.value = result
    }


    fun postUserTopics(hostUUID: String, topics: List<Topic>) =
        viewModelScope.launch(Dispatchers.IO) {

            _userTopicsFlow.value = Resource.Loading

            val topicNameList = topics
                .filter { it.selected }
                .map { it.name }

            val result = repository.postTopics(
                hostUUID,
                Topics(topicNameList)
            )

            if(!result.isSuccessful) {
                _userTopicsFlow.value = Resource.Error(Exception("네트워크 오류"))
                return@launch
            }

            if(result.body() == null) {
                _userTopicsFlow.value = Resource.Error(Exception("네트워크 오류"))
                return@launch
            }

            _userTopicsFlow.value = Resource.Success(topics)


        }

    fun changeSelectedTopic(topic: Topic) = viewModelScope.launch {
        if (userTopicsFlow.value is Resource.Success) {

            if (userTopicsFlow.value == null) {
                return@launch
            }


            val topics = (userTopicsFlow.value as Resource.Success<List<Topic>>).data.map {
                if (it == topic) {
                    it.copy(
                        selected = !it.selected
                    )
                } else {
                    it
                }
            }

            _userTopicsFlow.value = Resource.Success(topics)


        }
    }


    sealed class Effect {
        object Loading : Effect()

    }
}