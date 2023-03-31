package com.capstone.Capstone2Project.ui.screen.interview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.*
import com.capstone.Capstone2Project.data.resource.successOrNull
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class InterviewViewModel @Inject constructor(
    private val repository: NetworkRepository,
) : ViewModel() {

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

//    private var _recordRequest: MutableStateFlow<Boolean> = MutableStateFlow(false)
//    val recordRequest: StateFlow<Boolean> = _recordRequest

    private var _oldInterviewLogLineFlow: MutableStateFlow<InterviewLogLine?> =
        MutableStateFlow(null)
    val oldInterviewLogLineFlow: StateFlow<InterviewLogLine?> = _oldInterviewLogLineFlow

    private var _newInterviewLogLineFlow: MutableStateFlow<InterviewLogLine?> =
        MutableStateFlow(null)
    val newInterviewLogLineFlow: StateFlow<InterviewLogLine?> = _newInterviewLogLineFlow

    private var logState: InterviewViewModel.LogState = InterviewViewModel.LogState(false, null)


    private var _decibelFlow: MutableStateFlow<Int?> = MutableStateFlow(null)
    val decibelFlow: StateFlow<Int?> = _decibelFlow

    init {
        viewModelScope.launch {
            state.collectLatest { _s ->
                while (_s.interviewState == InterviewState.InProgress) {
                    delay(1000)
                    _state.update { s ->
                        val newProgress = s.progress + 1
                        s.copy(
                            progress = newProgress
                        )
                    }

                }
            }
        }
    }

    private suspend inline fun logLock(logLine: LogLine, block: () -> Unit) {

        if (!logState.locked) {
            logState.locked = true
            logState.type = logLine.type
            block()

            delay(3000)

            logState.locked = false
            logState.type = null
            return
        }


    }

    private fun loadNewInterviewLogLine(newInterviewLogLine: InterviewLogLine) =
        viewModelScope.launch {
            if (state.value.interviewState == InterviewState.InProgress) {
                logLock(newInterviewLogLine.logLine) {
                    _oldInterviewLogLineFlow.value = newInterviewLogLineFlow.value
                    _newInterviewLogLineFlow.value = newInterviewLogLine

                    _state.update {

                        val logs = it.logs.toMutableList()

                        logs.add(newInterviewLogLine)

                        it.copy(
                            logs = logs
                        )
                    }

                }
            }
        }

    fun loadInterviewLogLine(logLine: LogLine) {

        val currentPage = state.value.currentPage ?: return

        val questionnaire = state.value.customQuestionnaire ?: return

        val question = questionnaire.questions[currentPage].question

        val progress = state.value.progress

        val interviewLogLine = InterviewLogLine(
            date = System.currentTimeMillis(),
            progress = progress,
            logLine = logLine,
            questionItem = QuestionItem(UUID.randomUUID().toString(), question)
        )

        loadNewInterviewLogLine(interviewLogLine)

    }

    fun fetchCustomQuestionnaire(script: Script?) = viewModelScope.launch {
        if (script == null) {
            _state.update {
                it.copy(
                    interviewState = InterviewState.Error("유효하지 않은 자기소개서 입니다")
                )
            }
            return@launch
        }

        _state.update {
            it.copy(
                interviewState = InterviewState.Ready
            )
        }

        val result = repository.getCustomQuestionnaire(script)


        result.successOrNull()?.let { questionnaire ->

            if (questionnaire.questions.isEmpty()) {
                _state.update {
                    it.copy(
                        interviewState = InterviewState.Error(
                            "생성된 질문 목록이 없습니다"
                        )
                    )
                }
                return@launch
            }

            _state.update {

                val answers = mutableListOf<AnswerItem>()

                questionnaire.questions.forEach { question ->
                    answers.add(
                        AnswerItem(
                            answerUUID = UUID.randomUUID().toString(),
                            questionUUID = question.uuid,
                            answer = ""
                        )
                    )
                }

                it.copy(
                    interviewState = InterviewState.Prepared,
                    answers = answers,
                    currentPage = 0,
                    interviewDate = System.currentTimeMillis(),
                    customQuestionnaire = questionnaire,
                    progress = 0,
                    durations = List(answers.size) {0}
                )
            }
        }

    }


    fun moveToNextPage() = viewModelScope.launch {
        handleStateException {
            if (state.value.interviewState != InterviewState.InProgress && state.value.interviewState !is InterviewState.EditAnswer) {
                return@launch
            }

            val currentPage = state.value.currentPage ?: return@launch

            val questions = state.value.customQuestionnaire?.questions ?: return@launch

            val durations = state.value.durations ?: return@launch

            if (currentPage + 1 < questions.size) {
                _state.update {

//                    val newDurations = durations.mapIndexed { idx, duration ->
//                        if (idx == currentPage) {
//                            if (currentPage == 0) {
//                                it.progress
//                            } else {
//                                it.progress - durations[idx-1]
//                            }
//                        } else {
//                            duration
//                        }
//                    }

                    val newCumDurations = durations.mapIndexed { index, duration ->
                        if (index == currentPage) {
                            it.progress
                        } else {
                            duration
                        }
                    }

                    it.copy(
                        currentPage = currentPage + 1,
                        interviewState = InterviewState.InProgress,
                        durations = newCumDurations
                    )

                }
            } else {
                finishInterview()
            }
        }
    }

    fun appendAnswer(answer: String) = viewModelScope.launch {
        if (state.value.interviewState == InterviewState.InProgress) {
            handleStateException {
                _state.update {
                    val updatedAnswers = it.answers?.let { answers ->
                        for (i in answers.indices) {
                            if (i == it.currentPage) {
                                answers[i].answer += " ${answer.trim()}"
                            }
                        }
                        answers
                    }

                    it.copy(
                        answers = updatedAnswers
                    )

                }

            }
        }
    }

    fun updateAnswer(answer: String) = viewModelScope.launch {
        if (state.value.interviewState == InterviewState.InProgress) {
            handleStateException {
                _state.update {
                    val updatedAnswers = it.answers?.let { answers ->
                        for (i in answers.indices) {
                            if (i == it.currentPage) {
                                answers[i].answer = answer
                            }
                        }
                        answers
                    }

                    it.copy(
                        answers = updatedAnswers
                    )

                }
            }
        }
    }

    fun deleteAnswer() = viewModelScope.launch {
        updateAnswer("")
    }

    fun checkAnswer() = viewModelScope.launch {
        handleStateException {
            state.value.let {
                val questionnaire = it.customQuestionnaire
                val answers = it.answers
                val currentPage = it.currentPage
                val answerItem = answers!![currentPage!!]
                val questionItem = questionnaire!!.questions[currentPage]
                val qna = QnA(
                    answerItem = answerItem,
                    questionItem = questionItem
                )

                _state.update {_->
                    it.copy(
                        interviewState = InterviewState.EditAnswer(
                            qna
                        )
                    )
                }
            }



        }

    }


    fun startInterview() = viewModelScope.launch {
        if (state.value.interviewState == InterviewState.Prepared) {
            _state.update {
                it.copy(
                    interviewState = InterviewState.InProgress
                )
            }
        } else {
            _state.update {
                it.copy(
                    interviewState = InterviewState.Error(
                        message = "아직 준비되지 않았습니다"
                    )
                )
            }
        }
    }


    private fun finishInterview() = viewModelScope.launch {
        handleStateException {

            val durations = state.value.durations ?: return@launch

            val currentPage = state.value.currentPage ?: return@launch


            _state.update{

                val newCumDurations = durations.mapIndexed { index, duration ->
                    if (index == currentPage) {
                        it.progress
                    } else {
                        duration
                    }
                }.toMutableList()

                for (i in newCumDurations.size-1 downTo 0) {
                    val beforeDuration = if (i-1 < 0) 0 else newCumDurations[i-1]
                    newCumDurations[i] -= beforeDuration
                }

                it.copy(
                    interviewState = InterviewState.Loading,
                    durations = newCumDurations
                )
            }

            delay(1000)

            with(state.value) {

                val interviewUUID = UUID.randomUUID().toString()
                val interviewData = InterviewData(
                    interviewUUID = interviewUUID,
                    interviewDate = System.currentTimeMillis(),
                    logs = logs,
                    scriptUUID = customQuestionnaire!!.scriptUUID,
                    durations = durations
                )

                val result = repository.sendInterviewData(interviewData)

                if(result.result == "성공") {
                    _state.update {
                        it.copy(
                            interviewState = InterviewState.Finished(interviewUUID)
                        )
                    }

                } else {
                    throw Exception("네트워크 오류")
                }
            }
        }
    }

    fun destroyInterview() = viewModelScope.launch {

    }

    fun pauseInterview() = viewModelScope.launch {
        _state.update {
            it.copy(
                interviewState = InterviewState.Paused,
                recognizerState = RecognizerState.Stopped
            )
        }
    }

    fun restartInterview() = viewModelScope.launch {
        if (state.value.interviewState == InterviewState.Paused
        ) {
            _state.update {
                it.copy(
                    interviewState = InterviewState.InProgress
                )
            }
            return@launch
        }

    }

    fun stopRecordSTT() = viewModelScope.launch {
        _state.update {
            it.copy(
                recognizerState = RecognizerState.Stopped
            )
        }
    }

    fun startRecordSTT() = viewModelScope.launch {
        _state.update {
            it.copy(
                recognizerState = RecognizerState.Started
            )
        }
    }

    fun updateDecibel(decibel: Int) = viewModelScope.launch {
        _decibelFlow.update {
            decibel
        }
    }

    private inline fun handleStateException(block: () -> Unit) {
        try {
            block()
        } catch (E: Exception) {
            E.printStackTrace()
            _state.update {
                it.copy(
                    interviewState = InterviewState.Error(
                        E.message.toString()
                    )
                )
            }
        }
    }


    data class State(
        var interviewState: InterviewState = InterviewState.Ready,
        var progress: Int = 0,
        val logs: List<InterviewLogLine> = emptyList(),
        var currentPage: Int? = null,
        val customQuestionnaire: CustomQuestionnaire? = null,
        val answers: List<AnswerItem>? = null,
        var interviewDate: Long? = null,
        var recognizerState: RecognizerState = RecognizerState.Stopped,
        val durations: List<Int>? = null
    )

    data class QnA(
        val answerItem: AnswerItem,
        val questionItem: QuestionItem
    )

    sealed class RecognizerState {

        object Started : RecognizerState()
        object Stopped : RecognizerState()

    }


    sealed class InterviewState {
        object Ready : InterviewState() //패치전
        object Prepared : InterviewState() //패치후
        object InProgress : InterviewState() //진행중
        object Paused : InterviewState() //일시중지
        //object WritingMemo : InterviewState() //메모적기
        data class EditAnswer(
            val qnA: QnA
        ): InterviewState()
        data class Finished(
            val interviewUUID: String
        ): InterviewState()
        data class Error( //오류
            val message: String
        ) : InterviewState()
        object Loading: InterviewState()
    }

    data class LogState(
        var locked: Boolean,
        var type: LogLine.Type?
    )
}