package com.capstone.Capstone2Project.ui.screen.script

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.capstone.Capstone2Project.data.model.Questionnaire
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.ScriptItem
import com.capstone.Capstone2Project.data.resource.DataState
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.navigation.ROUTE_INTERVIEW_GUIDE
import com.capstone.Capstone2Project.repository.NetworkRepository
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

        if(script == null) {
            /*
            script == null 인 경우는 자기소개서 새로 작성하는 경우임
            뷰모델에 사용자가 선택한 질문, 입력한 제목, 선택된 직무가 있는지 확인하고
            하나라도 있다면 리턴 시켜서 처리하지 않음
             */

            if (state.value.scriptItemList.count { it.second } > 0) {
                return@launch
            }
            if (state.value.title.isNotBlank()) {
                return@launch
            }
            if (state.value.jobRoleList.count { it.second } > 0) {
                return@launch
            }
        } else {
            /*
            script != null 이면 자기소개서를 수정하는 경우임
            뷰모델에 세팅된 uuid (자기소개서 uuid) 와 script의 uuid 가 같다면, 리턴 시켜서 처리하지 않음
            다르다면 뷰모델에 세팅된 uuid (자기소개서 uuid)를 갱신함
             */
            if(state.value.uuid == script.uuid) { //함수가 두번 이상 호출된경우
                return@launch
            } else { //맨처음에만 호출 된 경우
                _state.update {
                    it.copy(
                        uuid = script.uuid
                    )
                }
            }
        }


        /*
        여기서부터는 뷰모델에 초기 세팅하는 작업임 - 여기부터는 맨처음 호출되는 경우만 아래 코드가 실행됨
         */

        _state.update {
            it.copy(
                dataState = DataState.Loading(),
            )
        }

        fetchJobRoleList()
        fetchScriptItems()

        val scriptUUID = script?.uuid ?:UUID.randomUUID().toString()

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
                    title = script.title,
                    uuid = scriptUUID,
                    dataState = DataState.Normal
                )
            }


        } else {
            _state.update {
                it.copy(
                    uuid = scriptUUID,
                    dataState = DataState.Normal
                )
            }
        }


    }

    /*
    세팅한걸로 초벌 자기소개서 만들기 (질문, 직무 선택하고 다음 페이지 넘어가는 상황)
     */
    fun makeScript() = viewModelScope.launch {

        val scriptTitle = state.value.title

        if (scriptTitle.isBlank()) {
            _effect.emit(
                Effect.ShowMessage("제목을 입력해주세요 :)")
            )
            return@launch
        }


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

    private fun sendScriptToServer(hostUUID: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _state.update {
                it.copy(
                    dataState = DataState.Loading()
                )
            }

            val scriptUUID = state.value.uuid ?: throw Exception("UUID Null2")

            val script = Script(
                date = System.currentTimeMillis(),
                title = state.value.title,
                scriptItems = state.value.scriptItemList.filter { it.second }.map { it.first },
                jobRole = state.value.jobRoleList.first { it.second }.first,
                hostUUID = hostUUID,
                uuid = scriptUUID
            )

            val result = repository.createScript(hostUUID, script)

            if (result.isFailure || result.getOrNull() == false) {
                _effect.emit(
                    Effect.ShowMessage(result.exceptionOrNull()?.message ?: "자기소개서 생성 실패")
                )
                _state.update {
                    it.copy(
                        dataState = DataState.Error(Exception("자기소개서 생성 실패"))
                    )
                }
            } else {
                _effect.emit(
                    Effect.ShowMessage("자기소개서 생성 완료")
                )

                val curPage = state.value.curPage

                /*
                뷰모델의 생명주기가 더 길기 때문에 자기소개서 생성이 완료되면 데이터들을 초기화해주어야 함
                -> 그렇지 않으면 ?
                -> 확인해보니 뒤로 가기시 문제 딱히 없었음
                 */

                _state.update {
                    it.copy(
                        dataState = DataState.Normal,
                        curPage = curPage + 1
                    )
                }
            }
        } catch (e: Exception) {
            _effect.emit(
                Effect.ShowMessage(e.message ?: "자기소개서 서버 전송 오류")
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

    fun moveNextPage(hostUUID: String) = viewModelScope.launch {
        val curPage = state.value.curPage
        val totalPage = state.value.scriptItemList.count { it.second }

        if (curPage < totalPage) {
            _state.update {
                it.copy(
                    curPage = curPage + 1
                )
            }

        } else if (curPage == totalPage) {
            sendScriptToServer(hostUUID)
        } else {
            _effect.emit(
                Effect.ShowMessage("페이지를 넘어갈 수 없어요")
            )
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

    /*
    면접 질문지 가져오기 요청
     */
    fun startInterview(hostUUID: String, reuse: Boolean) = viewModelScope.launch {
        try {
            _state.update {
                it.copy(
                    dataState = DataState.Loading(
                        message = "질문지를 생성하고 있습니다:)\n잠시만 기다려주세요 ..."
                    )
                )
            }


            //TODO 주석 풀어야함
            val scriptUUID = state.value.uuid ?: throw Exception("UUID Null")

            /*
            여기서는 재사용 X
             */
            val result = repository.getQuestionnaire(
                hostUUID, scriptUUID,
                reuse
            )



            if (result.isFailure) {
                throw Exception(result.exceptionOrNull())
            }

            val questionnaire = result.getOrNull() ?: throw Exception()

            _state.update {
                it.copy(
                    questionnaire = questionnaire
                )
            }

            val route = "$ROUTE_INTERVIEW_GUIDE?questionnaire={questionnaire}"
                .replace(
                    oldValue = "{questionnaire}",
                    newValue = state.value.questionnaire!!.toJsonString()
                )

            _effect.emit(
                Effect.NavigateTo(route) {
                    popUpTo(ROUTE_HOME) {
                        inclusive = true
                    }
                }
            )


        } catch (e: Exception) {
            e.printStackTrace()
            _effect.emit(
                Effect.ShowMessage(e.message ?: "질문지 생성 오류")
            )
            _state.update {
                it.copy(
                    dataState = DataState.Error(e)
                )
            }
        }

    }


    data class State(
        var scriptItemList: List<Pair<ScriptItem, Boolean>> = emptyList(), //Boolean 값은 선택된 경우 True
        var jobRoleList: List<Pair<String, Boolean>> = emptyList(),
        var dataState: DataState = DataState.Loading(),
        var uuid: String? = null,
        var interviewed: Boolean = false,
        var title: String = "",
        var dialogState: DialogState = DialogState.Nothing,
        var curPage: Int = 0,
        var questionnaire: Questionnaire? = null
    )


    sealed class Effect {
        data class ShowMessage(val message: String) : Effect()

        //        data class NavigateTo(val questionnaire: Questionnaire) : Effect()
        data class NavigateTo(val route: String, val builder: NavOptionsBuilder.() -> Unit) :
            Effect()
    }


    sealed class DialogState {
        data class CheckRemoveDialog(
            val scriptItem: ScriptItem
        ) : DialogState()

        object Nothing : DialogState()
    }


}