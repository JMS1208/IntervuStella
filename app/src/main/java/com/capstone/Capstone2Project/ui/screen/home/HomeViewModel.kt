package com.capstone.Capstone2Project.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel(){

    private var _isPresentFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPresentFlow: StateFlow<Boolean> = _isPresentFlow


    fun fetchHomeInformation(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {

        fetchAttendanceTodayInfo(hostUUID)
        fetchAttendanceWeekInfo(hostUUID)
    }

    fun checkAttendance(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.checkAttendance(hostUUID)
        _isPresentFlow.value = true
    }

    private suspend fun fetchAttendanceTodayInfo(hostUUID: String) {
        val attendanceResult = repository.isUserPresent(hostUUID)
        _isPresentFlow.value = attendanceResult
    }

    private suspend fun fetchAttendanceWeekInfo(hostUUID: String) {
        //TODO(나와봐야 앎)
    }




}