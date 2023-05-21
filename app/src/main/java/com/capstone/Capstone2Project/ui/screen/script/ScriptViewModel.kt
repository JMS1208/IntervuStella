package com.capstone.Capstone2Project.ui.screen.script

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.ScriptItem
import com.capstone.Capstone2Project.data.resource.DataState
import com.capstone.Capstone2Project.navigation.ROUTE_SCRIPT_WRITING_FINISH
import com.capstone.Capstone2Project.repository.NetworkRepository
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ScriptViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {

    private var _state = MutableStateFlow<State>(State())
    val state: StateFlow<State> = _state

    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect


    fun closeDialog() = viewModelScope.launch {
        _state.update {
            it.copy(
                dialogState = DialogState.Nothing
            )
        }
    }


    private suspend fun fetchScriptItems() {

        val scriptItemList = repository.getScriptItemList()


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
                scriptItemList = scriptItemList.getOrNull()!!.map { scriptItem ->
                    Pair(scriptItem, false)
                },
            )
        }

    }

    private suspend fun fetchJobRoleList() {

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

    fun removeScriptItem(selectedScriptItem: ScriptItem) = viewModelScope.launch {
        selectScriptItem(selectedScriptItem, true)
        _effect.emit(
            Effect.ShowMessage("삭제되었습니다")
        )
        closeDialog()


    }

    /*
    force가 true면 answer값 검사하지 않고 변경
     */
    fun selectScriptItem(selectedScriptItem: ScriptItem, force: Boolean = false) =
        viewModelScope.launch {
            val selected = //이미 선택된 아이템인 경우 (즉 삭제를 위해 클릭한 경우)
                state.value.scriptItemList.first { it.first.itemUUID == selectedScriptItem.itemUUID }.second

            if (!force && selectedScriptItem.answer.isNotBlank() && selected) {
                //answer 값 검사해야하는데, answer가 비어있지 않은 경우 경고 다이얼로그 띄우기

                _state.update {
                    it.copy(
                        dialogState = DialogState.CheckRemoveDialog(
                            selectedScriptItem
                        )
                    )
                }

                return@launch

            }

            val selectedCnt = state.value.scriptItemList.count { it.second }

            if (!selected && selectedCnt >= 5) { //추가하려는데, 5개 초과되면 안 되니까 검사

                _effect.emit(
                    Effect.ShowMessage("질문은 최대 5개까지만 선택할 수 있어요")
                )
                return@launch
            }

            //선택확인용
            val scriptItemList = state.value.scriptItemList.map { scriptItem ->
                if (scriptItem.first == selectedScriptItem) {

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

            if (jobRole.first == selectedJobRole) {
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


    fun setScriptAndFetchBaseData(script: Script?) = viewModelScope.launch(Dispatchers.IO) {
        _state.update {
            it.copy(
                dataState = DataState.Loading
            )
        }

        fetchJobRoleList()
        fetchScriptItems()

        if (script != null) {
            val newScriptItemList = script.scriptItems.map {
                Pair(it, true)
            }.toMutableList()

            for (scriptItem in state.value.scriptItemList) {
                if (scriptItem.first !in script.scriptItems) {
                    newScriptItemList.add(Pair(scriptItem.first, false))
                }
            }

            val newJobRoleList = state.value.jobRoleList.map {
                if (it.first == script.jobRole) {
                    Pair(it.first, true)
                } else {
                    it
                }
            }

            _state.update {
                it.copy(
                    scriptItemList = newScriptItemList,
                    jobRoleList = newJobRoleList,
                    title = script.title
                )
            }


        }

        _state.update {
            it.copy(
                dataState = DataState.Normal
            )
        }


    }

    /*
    자기소개서 만들기 (질문, 직무 선택하고 다음 페이지 넘어가는 상황)
     */
    fun makeScript() = viewModelScope.launch {

        val scriptItemCnt = state.value.scriptItemList.count { it.second }

        if (scriptItemCnt < 2) {
            _effect.emit(
                Effect.ShowMessage("질문을 2개 이상 선택해주세요")
            )
            return@launch
        }

        val jobRoleSelected = state.value.jobRoleList.count { it.second } == 1

        if (!jobRoleSelected) {
            _effect.emit(
                Effect.ShowMessage("직무를 선택해주세요")
            )
            return@launch
        }


        _state.update {
            it.copy(
                curPage = 1
            )
        }
    }

    fun updateScriptItemAnswer(scriptItem: ScriptItem, answer: String) = viewModelScope.launch {

        if (answer.length > scriptItem.maxLength) {
            _effect.emit(
                Effect.ShowMessage("글자 수를 초과하여 작성할 수 없어요")
            )
            return@launch
        }

        val newScriptItemList = state.value.scriptItemList.map {
            if (scriptItem.itemUUID == it.first.itemUUID) {
                it.copy(
                    first = it.first.copy(
                        answer = answer
                    )
                )
            } else {
                it
            }
        }

        _state.update {
            it.copy(
                scriptItemList = newScriptItemList
            )
        }
    }

    fun moveNextPage() = viewModelScope.launch {
        val curPage = state.value.curPage
        val totalPage = state.value.scriptItemList.count { it.second }

        if (curPage < totalPage) {
            _state.update {
                it.copy(
                    curPage = curPage + 1
                )
            }
        } else {

            val script = Script(
                date = System.currentTimeMillis(),
                title = state.value.title,
                scriptItems = state.value.scriptItemList.filter{it.second}.map{it.first},
                jobRole = state.value.jobRoleList.first{it.second}.first
            )
            _state.update {
                it.copy(
                    curPage = curPage + 1
                )
            }
        }

    }

    fun movePrevPage() = viewModelScope.launch {
        val curPage = state.value.curPage

        if (0 < curPage) {
            _state.update {
                it.copy(
                    curPage = curPage - 1
                )
            }
        } else {
            _effect.emit(
                Effect.ShowMessage("첫번째 페이지입니다")
            )
        }
    }


    data class State(
        var scriptItemList: List<Pair<ScriptItem, Boolean>> = emptyList(), //Boolean 값은 선택된 경우 True
        var jobRoleList: List<Pair<String, Boolean>> = emptyList(),
        var dataState: DataState = DataState.Loading,
        var uuid: String = UUID.randomUUID().toString(),
        var interviewed: Boolean = false,
        var title: String = "",
        var dialogState: DialogState = DialogState.Nothing,
        var curPage: Int = 0
    )


    sealed class Effect {
        data class ShowMessage(val message: String) : Effect()
        data class NavigateTo(val script: Script) : Effect()
    }


    sealed class DialogState {
        data class CheckRemoveDialog(
            val scriptItem: ScriptItem
        ) : DialogState()

        object Nothing : DialogState()
    }


}