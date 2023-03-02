package com.capstone.Capstone2Project.ui.screen.interview2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.model.AnswerItem
import com.capstone.Capstone2Project.data.model.CustomQuestionnaire
import com.capstone.Capstone2Project.data.model.InterviewLogLine
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.resource.successOrNull
import com.capstone.Capstone2Project.repository.NetworkRepository
import com.capstone.Capstone2Project.ui.screen.interview.InterviewViewModel
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
class InterviewViewModel2 @Inject constructor(
    private val repository: NetworkRepository,
) : ViewModel() {

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    init {
        viewModelScope.launch {
            state.collectLatest {
                while (it.interviewState == InterviewState2.InProgress) {
                    delay(1000)
                    it.progress += 1
                }
            }
        }
    }

    fun fetchCustomQuestionnaire(script: Script?) = viewModelScope.launch {
        if (script == null) {
            _state.update {
                it.copy(
                    interviewState = InterviewState2.OnError("유효하지 않은 자기소개서 입니다")
                )
            }
            return@launch
        }

        _state.update {
            it.copy(
                interviewState = InterviewState2.OnReady
            )
        }

        val result = repository.getCustomQuestionnaire(script)

        delay(5000)

        result.successOrNull()?.let { questionnaire ->

            if (questionnaire.questions.isEmpty()) {
                _state.update {
                    it.copy(
                        interviewState = InterviewState2.OnError(
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
                    interviewState = InterviewState2.OnPrepared,
                    answers = answers,
                    currentPage = 0,
                    interviewDate = System.currentTimeMillis(),
                    customQuestionnaire = questionnaire,
                    progress = 0
                )
            }
        }

    }

    fun updateAnswer(answer: String) = viewModelScope.launch {
        if (state.value.interviewState == InterviewState2.InProgress) {
            handleException {
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

    fun startInterview() = viewModelScope.launch {
        if (state.value.interviewState == InterviewState2.OnPrepared) {
            _state.update {
                it.copy(
                    interviewState = InterviewState2.InProgress
                )
            }
        }
    }


    fun destroyInterview() = viewModelScope.launch {

    }

    fun pauseInterview() = viewModelScope.launch {
        _state.update {
            it.copy(
                interviewState = InterviewState2.OnPaused
            )
        }
    }

    fun restartInterview() = viewModelScope.launch {
        if (state.value.interviewState == InterviewState2.OnPaused
        ) {
            _state.update {
                it.copy(
                    interviewState = InterviewState2.OnPrepared
                )
            }
            return@launch
        }

    }

    private inline fun handleException(block: () -> Unit) {
        try {
            block()
        } catch (E: Exception) {
            _state.update {
                it.copy(
                    interviewState = InterviewState2.OnError(
                        E.message.toString()
                    )
                )
            }
        }
    }

    data class State(
        var interviewState: InterviewState2 = InterviewState2.OnReady,
        var progress: Int = 0,
        val logs: List<InterviewLogLine> = emptyList(),
        var currentPage: Int? = null,
        val customQuestionnaire: CustomQuestionnaire? = null,
        val answers: List<AnswerItem>? = null,
        var memo: String? = null,
        var memoDate: Long? = null,
        var interviewDate: Long? = null
    )

    sealed class InterviewState2 {
        object OnReady : InterviewState2()
        object OnPrepared : InterviewState2()
        object InProgress : InterviewState2()
        object OnPaused : InterviewState2()
        object OnFinished : InterviewState2()
        data class OnError(
            val message: String
        ) : InterviewState2()
    }
}