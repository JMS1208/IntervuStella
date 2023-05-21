package com.capstone.Capstone2Project.ui.screen.script

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.ScriptItem
import com.capstone.Capstone2Project.data.resource.DataState
import com.capstone.Capstone2Project.data.resource.Selected
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class  ScriptWritingViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {


    private var _state = MutableStateFlow<State>(State())
    val state: StateFlow<State> = _state

    private suspend fun fetchScriptItems() {
        _state.update {
            it.copy(
                dataState = DataState.Loading
            )
        }

        val scriptItemList = repository.getScriptItemList()

        Log.e("TAG", "fetchScriptItems: $scriptItemList")

        if (scriptItemList.isFailure) {
            _state.update {
                it.copy(
                    dataState = DataState.Error(scriptItemList.exceptionOrNull())
                )
            }

            return
        }

        if (scriptItemList.getOrNull() == null) {
            _state.update {
                it.copy(
                    dataState = DataState.Error(Exception("질문 목록이 빈 리스트입니다"))
                )
            }
            return
        }

        _state.update {
            it.copy(
                scriptItemList = scriptItemList.getOrNull()!!.map {scriptItem->
                    Pair(scriptItem, false)
                },
            )
        }

    }

    private suspend fun fetchJobRoleList() {
        _state.update {
            it.copy(
                dataState = DataState.Loading
            )
        }

        val jobRoleList = repository.getJobRoleList()

        if (jobRoleList.isFailure) {
            _state.update {
                it.copy(
                    dataState = DataState.Error(jobRoleList.exceptionOrNull())
                )
            }

            return
        }

        if (jobRoleList.getOrNull() == null) {
            _state.update {
                it.copy(
                    dataState = DataState.Error(Exception("직무 목록이 빈 리스트입니다"))
                )
            }
            return
        }

        _state.update {
            it.copy(
                jobRoleList = jobRoleList.getOrNull()!!.map { jobRole ->
                    Pair(jobRole, false)
                }
            )
        }

    }

    /*
    자기소개서 이름 변경
     */
    fun updateScriptTitle(title: String) = viewModelScope.launch {
        _state.update {
            it.copy(
                title = title
            )
        }
    }

    /*
    자기소개서 직무 변경
     */
    fun updateJobRole(selectedJobRole: String) = viewModelScope.launch {

        val newJobRoleList = state.value.jobRoleList.map { jobRole ->

            if(jobRole.first == selectedJobRole) {
                jobRole.copy(
                    second = !jobRole.second
                )
            } else {
                jobRole.copy(
                    second = false
                )
            }
        }

        _state.update {
            it.copy(
                jobRoleList = newJobRoleList
            )
        }
    }

    /*
    질문 선택하기 위한 함수
    기존에 입력된 answer가 있다면 다이얼로그를 띄워줌
    없다면 그냥 선택 또는 취소 가능
    */
    fun selectScriptItem(selectedScriptItem: ScriptItem) = viewModelScope.launch {

        Log.e("TAG", "selectScriptItem: $selectedScriptItem, ${state.value}", )

        val scriptItemList = state.value.scriptItemList.map {
            it.copy(
                second = !it.second
            )
        }
        //선택확인용
//        val scriptItemList = state.value.scriptItemList.map { scriptItem->
//            if(scriptItem.first == selectedScriptItem) {
//                scriptItem.copy(
//                    second = !scriptItem.second
//                )
//            } else {
//                scriptItem
//            }
//        }
//        val scriptItemList = mutableListOf<Pair<ScriptItem, Boolean>>()
//
//        for (scriptItem in state.value.scriptItemList) {
//            Log.e("TAG", "ScriptItem: $scriptItem", )
//            if(scriptItem.first.itemUUID == selectedScriptItem.itemUUID) {
////                if(selectedScriptItem.answer.isNotBlank()) {
////                    showCheckRemoveDialog(scriptItem.first)
////                    break
////                }
//                scriptItemList.add(
//                    scriptItem.copy(
//                        second = !scriptItem.second
//                    )
//                )
//            } else {
//                scriptItemList.add(scriptItem)
//            }
//        }

        _state.update {
            it.copy(
                scriptItemList = scriptItemList
            )
        }


    }


    private fun showCheckRemoveDialog(scriptItem: ScriptItem) = viewModelScope.launch {
        _state.update {
            it.copy(
                dialogState = DialogState.CheckRemoveDialog(scriptItem)
            )
        }
    }
    /*
    단순히 다이얼로그를 닫기 위한 함수
     */
    fun closeDialog() = viewModelScope.launch {
        _state.update {
            it.copy(
                dialogState = DialogState.Nothing
            )
        }
    }

    /*
    진짜 삭제하기 위한 함수
    다이얼로그에서 확인버튼을 통해 호출함
     */
    fun removeScriptItem(selectedScriptItem: ScriptItem) = viewModelScope.launch {
        val scriptItemList = state.value.scriptItemList.map { scriptItem->
            if(scriptItem.first == selectedScriptItem) {
                scriptItem.copy(
                    second = !scriptItem.second
                )
            } else {
                scriptItem
            }
        }


        _state.update {
            it.copy(
                scriptItemList = scriptItemList
            )
        }
    }
    /*
    자기소개서 초기화
     */
    fun setInitialScript(script: Script?) = viewModelScope.launch {

        fetchScriptItems()
        fetchJobRoleList()
        Log.e("TAG", "setInitialScript: ${state.value}", )
        if(script != null) {

            val newScriptItems = mutableListOf<Pair<ScriptItem, Boolean>>()

            state.value.scriptItemList.forEach { scriptItem->
                if (scriptItem.first !in script.scriptItems) {
                    newScriptItems.add(Pair(scriptItem.first, false))
                }
            }

            script.scriptItems.forEach {
                newScriptItems.add(Pair(it, true))
            }

            newScriptItems.sortBy { it.first.index }

            val newJobRoles = mutableListOf<Pair<String, Boolean>>()

            state.value.jobRoleList.forEach { jobRole->

                if(jobRole.first != script.jobRole) {
                    newJobRoles.add(Pair(jobRole.first, false))
                } else {
                    newJobRoles.add(Pair(jobRole.first, true))
                }

            }

            _state.update {
                it.copy(
                    scriptItemList = newScriptItems,
                    jobRoleList = newJobRoles,
                    uuid = script.uuid,
                    interviewed = script.interviewed,
                    title = script.title
                )
            }
        }

        _state.update {
            it.copy(
                dataState = DataState.Normal
            )
        }

        Log.e("TAG", "setInitialScript: 호출 횟수 보자")
    }


    data class State(
        var scriptItemList: List<Pair<ScriptItem, Boolean>> = emptyList(), //Boolean 값은 선택된 경우 True
        var jobRoleList: List<Pair<String, Boolean>> = emptyList(),
        var dataState: DataState = DataState.Loading,
        var uuid: String = UUID.randomUUID().toString(),
        var interviewed: Boolean = false,
        var title: String = "",
        var canMakeScript: Boolean = true,
        var dialogState: DialogState = DialogState.Nothing
    )

    sealed class DialogState {
        data class CheckRemoveDialog(
            val scriptItem: ScriptItem
        ): DialogState()

        object Nothing: DialogState()
    }





}