package com.capstone.Capstone2Project.ui.screen.interesting.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.Topic
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val repository: NetworkRepository
): ViewModel() {

    private val _defaultTopicsFlow: MutableStateFlow<Resource<List<Topic>>?> = MutableStateFlow(null)
    val defaultTopicsFlow: StateFlow<Resource<List<Topic>>?> = _defaultTopicsFlow


    init {
        fetchDefaultTopics()
    }


    private fun fetchDefaultTopics() = viewModelScope.launch {
        _defaultTopicsFlow.value = Resource.Loading
        val result = repository.getDefaultTopics()
        _defaultTopicsFlow.value = result
    }

}