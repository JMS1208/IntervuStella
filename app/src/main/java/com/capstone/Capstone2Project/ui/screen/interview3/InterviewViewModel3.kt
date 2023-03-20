//package com.capstone.Capstone2Project.ui.screen.interview3
//
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.capstone.Capstone2Project.data.model.*
//import com.capstone.Capstone2Project.data.resource.Resource
//import com.capstone.Capstone2Project.data.resource.successOrNull
//import com.capstone.Capstone2Project.repository.NetworkRepository
//import com.capstone.Capstone2Project.ui.screen.interview.InterviewViewModel
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import java.util.UUID
//import javax.inject.Inject
//
//@HiltViewModel
//class InterviewViewModel3 @Inject constructor(
//    private val repository: NetworkRepository,
//    private val savedStateHandle: SavedStateHandle
//) : ViewModel() {
//
//    private var _customQuestionnaireFlow = MutableStateFlow<Resource<CustomQuestionnaire>?>(null)
//    val customQuestionnaireFlow: StateFlow<Resource<CustomQuestionnaire>?> =
//        _customQuestionnaireFlow
//
//
//    private var _oldInterviewLogLineFlow: MutableStateFlow<InterviewLogLine?> =
//        MutableStateFlow(null)
//    val oldInterviewLogLineFlow: StateFlow<InterviewLogLine?> = _oldInterviewLogLineFlow
//
//    private var _newInterviewLogLineFlow: MutableStateFlow<InterviewLogLine?> =
//        MutableStateFlow(null)
//    val newInterviewLogLineFlow: StateFlow<InterviewLogLine?> = _newInterviewLogLineFlow
//
//    private var _currentPageFlow: MutableStateFlow<Int?> = MutableStateFlow(null)
//    val currentPageFlow: StateFlow<Int?> = _currentPageFlow
//
//
//    //TODO(인터뷰 대답 저장하는 거 만들어야함)
//    private var _interviewResultFlow: MutableStateFlow<InterviewResult?> = MutableStateFlow(null)
//    val interviewResultFlow: StateFlow<InterviewResult?> = _interviewResultFlow
//
//
//    private var _progressFlow: MutableStateFlow<Int> = MutableStateFlow(0)
//    val progressFlow: StateFlow<Int> = _progressFlow
//
//    private var logState3: LogState3 = LogState3(false, null)
//
//    private var _interviewState3Flow: MutableStateFlow<InterviewViewModel.InterviewState> = MutableStateFlow(InterviewState3.Ready)
//    val interviewState3Flow: StateFlow<InterviewState3> = _interviewState3Flow
//
//    fun startInterview() = viewModelScope.launch {
//
//        _interviewState3Flow.value = InterviewState3.OnGoing
//
//        while (interviewState3Flow.value == InterviewState3.OnGoing) {
//            delay(1000)
//            _progressFlow.value = progressFlow.value + 1
//        }
//    }
//
//    //TODO
//    fun pauseInterview() = viewModelScope.launch {
//        _interviewState3Flow.value = InterviewState3.Ready
//    }
//
//    fun stopInterview() = viewModelScope.launch {
//        _interviewState3Flow.value = InterviewState3.Ended
//    }
//
//    fun updateAnswer(answer: String) = viewModelScope.launch {
//        if(interviewState3Flow.value ==  InterviewState3.OnGoing) {
//            val currentPage = currentPageFlow.value
//
//            val interviewResult = interviewResultFlow.value
//
//            if(interviewResult != null && currentPage != null) {
//
//                interviewResult.apply {
//                    answers[currentPage].answer = answer
//                }
//
//            }
//        }
//    }
//
//
//
//    fun fetchCustomQuestionnaire(script: Script) = viewModelScope.launch {
//        _customQuestionnaireFlow.value = Resource.Loading
//
//        val result = repository.getCustomQuestionnaire(script)
//
//        _customQuestionnaireFlow.value = result
//
//
//        result.successOrNull()?.let { customQuestionnaire ->
//            _currentPageFlow.value = 0
//
//            val emptyAnswers = mutableListOf<AnswerItem>()
//
//            customQuestionnaire.questions.forEach {
//                emptyAnswers.add(AnswerItem(
//                    answerUUID = UUID.randomUUID().toString(),
//                    questionUUID = it.uuid,
//                    answer = ""
//                ))
//            }
//
//            _interviewResultFlow.value = InterviewResult(
//                uuid = UUID.randomUUID().toString(),
//                scriptUUID = customQuestionnaire.scriptUUID,
//                memo = null,
//                memo_date = null,
//                interview_date = System.currentTimeMillis(),
//                score = null,
//                logs = emptyList(),
//                answers = emptyAnswers
//            )
//
//        }
//
//
//    }
//
//    private suspend inline fun logLock(logLine: LogLine, block: () -> Unit) {
//
//        if (!logState3.locked) {
//            logState3.locked = true
//            logState3.type = logLine.type
//            block()
//
//            delay(3000)
//
//            logState3.locked = false
//            logState3.type = null
//            return
//        }
//
//
//    }
//
//    private fun loadNewInterviewLogLine(newInterviewLogLine: InterviewLogLine) =
//        viewModelScope.launch {
//            logLock(newInterviewLogLine.logLine) {
//                _oldInterviewLogLineFlow.value = newInterviewLogLineFlow.value
//                _newInterviewLogLineFlow.value = newInterviewLogLine
//            }
//        }
//
//    fun moveNextPage() = viewModelScope.launch {
//
//        val questionnaire = customQuestionnaireFlow.value?.successOrNull() ?: return@launch
//
//        currentPageFlow.value?.let { page ->
//
//            val nextPage = page + 1
//
//            if (nextPage < questionnaire.questions.size) {
//                _currentPageFlow.value = nextPage
//            } else {
//                stopInterview()
//            }
//
//        }
//    }
//
//
//    fun loadInterviewLogLine(logLine: LogLine) {
//
//        val currentPage = currentPageFlow.value ?: return
//
//        val questionnaire = customQuestionnaireFlow.value?.successOrNull() ?: return
//
//        val question = questionnaire.questions[currentPage].question
//
//        val interviewLogLine = InterviewLogLine(
//            date = System.currentTimeMillis(),
//            progress = progressFlow.value,
//            logLine = logLine,
//            questionItem = QuestionItem(UUID.randomUUID().toString(), question)
//        )
//
//        loadNewInterviewLogLine(interviewLogLine)
//
//    }
//
//
//    data class LogState3(
//        var locked: Boolean,
//        var type: LogLine.Type?
//    )
//
//    sealed class InterviewState3{
//        object Ready: InterviewState3()
//        object OnGoing: InterviewState3()
//        object Ended: InterviewState3()
//    }
//
//
//
//}