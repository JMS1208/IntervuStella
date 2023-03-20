package com.capstone.Capstone2Project.ui.screen.interesting.topic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.Topic
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
): ViewModel() {

    private val _userTopicsFlow: MutableStateFlow<Resource<List<Topic>>?> = MutableStateFlow(null)
    val userTopicsFlow: StateFlow<Resource<List<Topic>>?> = _userTopicsFlow


    fun fetchUserTopics(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
//        _userTopicsFlow.value = Resource.Loading
        val result = repository.getUserTopics(hostUUID)
        _userTopicsFlow.value = result
    }


    fun selectUserTopics(hostUUID: String, topics: List<Topic>) = viewModelScope.launch(Dispatchers.IO) {
        Log.e("TAG", "selectUserTopics: $topics", )
    }

    fun changeSelectedTopic(topic: Topic) = viewModelScope.launch {
        if (userTopicsFlow.value is Resource.Success) {

            if (userTopicsFlow.value == null) {
                return@launch
            }

            val topics = mutableListOf<Topic>()

            topics.addAll((userTopicsFlow.value as Resource.Success<List<Topic>>).data)

            val _topics =topics.map {
                if (it.name == topic.name) {
                    it.copy(
                        name = it.name,
                        selected = !it.selected
                    )
                } else {
                    it
                }
            }

            _userTopicsFlow.value = Resource.Success(_topics)

            Log.e("TAG", "changeSelectedTopic: $_topics", )

        }
    }


}