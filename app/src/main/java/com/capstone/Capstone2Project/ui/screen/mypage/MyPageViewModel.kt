package com.capstone.Capstone2Project.ui.screen.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.InspiringKeyword
import com.capstone.Capstone2Project.data.model.InterviewLog
import com.capstone.Capstone2Project.data.model.InterviewScore
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.TodayQuestion
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.repository.AppDatabaseRepository
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val appDatabaseRepository: AppDatabaseRepository
): ViewModel() {

    private var _myScriptsFlow: MutableStateFlow<Resource<List<Script>>?> = MutableStateFlow(null)
    val myScriptsFlow: StateFlow<Resource<List<Script>>?> = _myScriptsFlow

    private var _myInterviewLogsFlow: MutableStateFlow<Resource<List<InterviewLog>>?> = MutableStateFlow(null)
    val myInterviewLogsFlow: StateFlow<Resource<List<InterviewLog>>?> = _myInterviewLogsFlow

    private var _myInterviewScoresFlow: MutableStateFlow<Resource<List<InterviewScore>>?> = MutableStateFlow(null)
    val myInterviewScoresFlow: StateFlow<Resource<List<InterviewScore>>?> = _myInterviewScoresFlow

    private var _myInspiringKeywords: MutableStateFlow<Resource<List<InspiringKeyword>>?> = MutableStateFlow(null)
    val myInspiringKeywords: StateFlow<Resource<List<InspiringKeyword>>?> = _myInspiringKeywords


    private var _myTodayQuestionsFlow: MutableStateFlow<Resource<List<TodayQuestion>>?> = MutableStateFlow(null)
    val myTodayQuestionsFlow: StateFlow<Resource<List<TodayQuestion>>?> = _myTodayQuestionsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    fun fetchMyScripts(hostUUID: String) = viewModelScope.launch {
        _myScriptsFlow.value = Resource.Loading


        val result = repository.getScripts(hostUUID)

        _myScriptsFlow.value = result
    }

    fun fetchMyInterviewLogs(hostUUID: String) = viewModelScope.launch {
        _myInterviewLogsFlow.value = Resource.Loading

        val result = repository.getInterviewLogs(hostUUID)

        _myInterviewLogsFlow.value = result
    }

    fun fetchMyInterviewScores(hostUUID: String) = viewModelScope.launch {
        _myInterviewScoresFlow.value = Resource.Loading

        val result = repository.getInterviewScores(hostUUID)

        _myInterviewScoresFlow.value = result
    }

    fun fetchMyTodayQuestions(hostUUID: String) = viewModelScope.launch {
        _myTodayQuestionsFlow.value = Resource.Loading


        val result = repository.getMyTodayQuestions(hostUUID)

        _myTodayQuestionsFlow.value = result
    }

    fun fetchMyInspiringKeywords(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        _myInspiringKeywords.value = Resource.Loading

        val result = appDatabaseRepository.getInspiringKeywords(hostUUID)

        _myInspiringKeywords.value = Resource.Success(result)
    }

    fun insertInspiringKeyword(inspiringKeyword: InspiringKeyword) = viewModelScope.launch(Dispatchers.IO) {
        appDatabaseRepository.insertInspiringKeyword(inspiringKeyword)
        fetchMyInspiringKeywords(inspiringKeyword.hostUUID)
    }

    fun deleteAllInspiringKeywords(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        appDatabaseRepository.deleteAllInspiringKeywords(hostUUID)
        fetchMyInspiringKeywords(hostUUID)
    }

    fun deleteInspiringKeyword(inspiringKeyword: InspiringKeyword) = viewModelScope.launch(Dispatchers.IO) {
        appDatabaseRepository.deleteInspiringKeyword(inspiringKeyword)
        fetchMyInspiringKeywords(inspiringKeyword.hostUUID)
    }
}