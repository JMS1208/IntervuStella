package com.capstone.Capstone2Project.ui.screen.interview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.data.model.InterviewScore
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterviewResultViewModel @Inject constructor(
    private val repository: NetworkRepository
): ViewModel() {

    private var _writingMemoResultFlow: MutableStateFlow<Resource<String>?> = MutableStateFlow(null)
    val writingMemoResultFlow: StateFlow<Resource<String>?> = _writingMemoResultFlow

    private var _interviewResultFlow: MutableStateFlow<Resource<InterviewResult>?> = MutableStateFlow(null)
    val interviewResultFlow: StateFlow<Resource<InterviewResult>?> = _interviewResultFlow

    fun fetchInterviewResult(interviewUUID: String) = viewModelScope.launch {
        _interviewResultFlow.value = Resource.Loading
        val result = repository.getInterviewResult(interviewUUID)
        _interviewResultFlow.value = result
    }

    fun writeMemo(interviewUUID: String, memo: String) = viewModelScope.launch {
        _writingMemoResultFlow.value = Resource.Loading
        delay(1000)
        val result = repository.writeMemo(interviewUUID, memo)
        _writingMemoResultFlow.value = result
    }

    fun initMemoState() = viewModelScope.launch {
        _writingMemoResultFlow.value = null
    }



}