package com.capstone.Capstone2Project.ui.screen.interview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.*
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.data.resource.successOrNull
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class InterviewViewModel @Inject constructor(
    private val repository: NetworkRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _customQuestionnaireFlow = MutableStateFlow<Resource<CustomQuestionnaire>?>(null)
    val customQuestionnaireFlow: StateFlow<Resource<CustomQuestionnaire>?> =
        _customQuestionnaireFlow


    private var _oldInterviewLogLineFlow: MutableStateFlow<InterviewLogLine?> =
        MutableStateFlow(null)
    val oldInterviewLogLineFlow: StateFlow<InterviewLogLine?> = _oldInterviewLogLineFlow

    private var _newInterviewLogLineFlow: MutableStateFlow<InterviewLogLine?> =
        MutableStateFlow(null)
    val newInterviewLogLineFlow: StateFlow<InterviewLogLine?> = _newInterviewLogLineFlow

    private var _currentPageFlow: MutableStateFlow<Int?> = MutableStateFlow(null)
    val currentPageFlow: StateFlow<Int?> = _currentPageFlow


    //TODO(인터뷰 대답 저장하는 거 만들어야함)
    private var _interviewResultFlow: MutableStateFlow<InterviewResult?> = MutableStateFlow(null)
    val interviewResultFlow: StateFlow<InterviewResult?> = _interviewResultFlow


    private var _progressFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    val progressFlow: StateFlow<Int> = _progressFlow

    private var logState: LogState = LogState(false, null)

    private var _interviewStateFlow: MutableStateFlow<InterviewState> = MutableStateFlow(InterviewState.Ready)
    val interviewStateFlow: StateFlow<InterviewState> = _interviewStateFlow

    fun startInterview() = viewModelScope.launch {

        _interviewStateFlow.value = InterviewState.OnGoing

        while (interviewStateFlow.value == InterviewState.OnGoing) {
            delay(1000)
            _progressFlow.value = progressFlow.value + 1
        }
    }

    //TODO
    fun pauseInterview() = viewModelScope.launch {
        _interviewStateFlow.value = InterviewState.Ready
    }

    fun stopInterview() = viewModelScope.launch {
        _interviewStateFlow.value = InterviewState.Ended
    }

    fun updateAnswer(answer: String) = viewModelScope.launch {
        if(interviewStateFlow.value ==  InterviewState.OnGoing) {
            val currentPage = currentPageFlow.value

            val interviewResult = interviewResultFlow.value

            if(interviewResult != null && currentPage != null) {

                interviewResult.apply {
                    answers[currentPage].answer = answer
                }

            }
        }
    }



    fun fetchCustomQuestionnaire(script: Script) = viewModelScope.launch {
        _customQuestionnaireFlow.value = Resource.Loading

        val result = repository.getCustomQuestionnaire(script)

        _customQuestionnaireFlow.value = result


        result.successOrNull()?.let { customQuestionnaire ->
            _currentPageFlow.value = 0

            val emptyAnswers = mutableListOf<AnswerItem>()

            customQuestionnaire.questions.forEach {
                emptyAnswers.add(AnswerItem(
                    answerUUID = UUID.randomUUID().toString(),
                    questionUUID = it.uuid,
                    answer = ""
                ))
            }

            _interviewResultFlow.value = InterviewResult(
                uuid = UUID.randomUUID().toString(),
                scriptUUID = customQuestionnaire.scriptUUID,
                memo = null,
                memo_date = null,
                interview_date = System.currentTimeMillis(),
                score = null,
                logs = emptyList(),
                answers = emptyAnswers
            )

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
            logLock(newInterviewLogLine.logLine) {
                _oldInterviewLogLineFlow.value = newInterviewLogLineFlow.value
                _newInterviewLogLineFlow.value = newInterviewLogLine
            }
        }

    fun moveNextPage() = viewModelScope.launch {

        val questionnaire = customQuestionnaireFlow.value?.successOrNull() ?: return@launch

        currentPageFlow.value?.let { page ->

            val nextPage = page + 1

            if (nextPage < questionnaire.questions.size) {
                _currentPageFlow.value = nextPage
            } else {
                stopInterview()
            }

        }
    }


    fun loadInterviewLogLine(logLine: LogLine) {

        val currentPage = currentPageFlow.value ?: return

        val questionnaire = customQuestionnaireFlow.value?.successOrNull() ?: return

        val question = questionnaire.questions[currentPage].question

        val interviewLogLine = InterviewLogLine(
            date = System.currentTimeMillis(),
            progress = progressFlow.value,
            logLine = logLine,
            questionItem = QuestionItem(UUID.randomUUID().toString(), question)
        )

        loadNewInterviewLogLine(interviewLogLine)

    }


    data class LogState(
        var locked: Boolean,
        var type: LogLine.Type?
    )

    sealed class InterviewState{
        object Ready: InterviewState()
        object OnGoing: InterviewState()
        object Ended: InterviewState()
    }



}