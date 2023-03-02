package com.capstone.Capstone2Project.ui.screen.script

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScriptWritingViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {

//    private var _scriptPaper = MutableStateFlow<Resource<ScriptPaper>?>(null)
//    val scriptPaper: StateFlow<Resource<ScriptPaper>?> = _scriptPaper
//
//    fun getScriptPaper() = viewModelScope.launch {
//        _scriptPaper.value = Resource.Loading
//
//        val result = repository.getScriptPaper()
//
//        _scriptPaper.value = result
//    }


    private var _scriptFlow = MutableStateFlow<Resource<Script>?>(null)
    val scriptFlow: StateFlow<Resource<Script>?> = _scriptFlow

    fun createEmptyScript(hostUUID: String?) = viewModelScope.launch {
        _scriptFlow.value = Resource.Loading

        if (hostUUID == null) {
            _scriptFlow.value = Resource.Error(Exception("유효하지 않은 유저입니다. 다시 로그인 해주세요"))
            return@launch
        }

        val result = repository.createEmptyScript(hostUUID)

        _scriptFlow.value = result
    }

    fun updateScript(script: Script) = viewModelScope.launch {
        _scriptFlow.value = Resource.Success(script)
        Log.d("TAG", "변경: 완료 $script")
    }

}