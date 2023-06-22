package com.capstone.Capstone2Project.ui.screen.interview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.capstone.Capstone2Project.data.model.AnswerItem
import com.capstone.Capstone2Project.data.model.InterviewData
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.data.model.LiveFeedback
import com.capstone.Capstone2Project.data.model.QuestionItem
import com.capstone.Capstone2Project.data.model.Questionnaire
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.navigation.ROUTE_INTERVIEW_FINISHED
import com.capstone.Capstone2Project.repository.NetworkRepository
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalPermissionsApi::class)
@HiltViewModel
class InterviewViewModel @Inject constructor(
    private val repository: NetworkRepository,
) : ViewModel() {

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private var _dialogState: MutableStateFlow<DialogState> = MutableStateFlow(DialogState.Nothing)
    val dialogState: StateFlow<DialogState> = _dialogState

    private var _effect: MutableSharedFlow<Effect> = MutableSharedFlow()
    val effect: SharedFlow<Effect> = _effect

    init { //면접 상태에 따른 처리
        viewModelScope.launch {
            state.collectLatest {
                when(it.interviewState) {
                    is InterviewState.Finished -> {
                        //인터뷰 끝나면 내비게이션
                        val interviewResult = (it.interviewState as InterviewState.Finished).interviewResult
                        val route = "$ROUTE_INTERVIEW_FINISHED?interview_result={interview_result}".replace(
                            oldValue = "{interview_result}",
                            newValue = interviewResult.toJsonString()
                        )
                        _effect.emit(
                            Effect.NavigateTo(
                                route
                            ) {
                                popUpTo(ROUTE_HOME) {
                                    inclusive = true
                                }
                            }
                        )

                    }
                    InterviewState.InProgress -> { //면접 진행 중이라면 0.1초 단위로 면접 진행 시간 갱신
                        while (it.interviewState == InterviewState.InProgress) {
                            delay(1000L)

                            val newProgress = it.progress + 1
                            _state.update { s ->
                                s.copy(
                                    progress = newProgress
                                )
                            }
                        }
                    }
                    InterviewState.Prepared -> {
                        //준비 완료되면 시작 카운트다운
                        _dialogState.update {
                            DialogState.ShowCountdownDialog
                        }
                    }
                    else-> Unit
                }
            }
        }
    }

    /*
    다이얼로그 닫기
     */
    fun closeDialog() = viewModelScope.launch {
        _dialogState.update {
            DialogState.Nothing
        }
    }

    /*
    현재 답변 삭제하기
     */
    fun deleteAnswer() = viewModelScope.launch {
        updateAnswer("")
    }

    /*
    제출 버튼 한번 누르기 - 답변 수정할 수 있는 다이얼로그 띄워줌
     */
    fun checkAnswer() = viewModelScope.launch {
        try {
            if(state.value.interviewState != InterviewState.InProgress) {
                throw Exception("면접이 진행 중이지 않습니다")
            }
            val questionnaire = state.value.questionnaire ?: throw Exception("면접 질문지가 제대로 초기화되지 않았어요")
            val answers = state.value.userAnswers ?: throw Exception("면접 질문지가 제대로 초기화되지 않았어요")
            val currentPage = state.value.currentPage ?: throw Exception("면접 질문지가 제대로 초기화되지 않았어요")
            val answerItem = answers[currentPage]
            val questionItem = questionnaire.questions[currentPage]

            _dialogState.update {
                DialogState.ShowEditAnswerDialog(answerItem, questionItem)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _effect.emit(
                Effect.ShowMessage(e.message ?: "오류가 발생했어요")
            )
        }
    }

    /*
    STT로 받은 텍스트 이어 붙이기
     */
    fun appendAnswer(answer: String) = viewModelScope.launch {
        try {
            if(state.value.interviewState != InterviewState.InProgress) {
                throw Exception("면접이 진행 중이지 않습니다")
            }
            val currentPage = state.value.currentPage ?: throw Exception("면접 질문지가 제대로 초기화되지 않았어요")
            val curAnswer = state.value.userAnswers?.get(currentPage) ?: throw Exception("면접 질문지가 제대로 초기화되지 않았어요")
            updateAnswer(curAnswer.answer.trim()+" "+answer)
        } catch (e: Exception) {
            e.printStackTrace()
            _effect.emit(
                Effect.ShowMessage(e.message ?: "오류가 발생했어요")
            )
        }
    }

    /*
    답변 업데이트하기
     */
    fun updateAnswer(newAnswer: String) = viewModelScope.launch {
        try {
            if(state.value.interviewState != InterviewState.InProgress) {
                throw Exception("면접이 진행 중이지 않습니다")
            }

            val currentPage = state.value.currentPage ?: throw Exception("면접 질문지가 제대로 초기화되지 않았어요")
            val newAnswers = state.value.userAnswers?.mapIndexed { idx, answer->
                if(idx == currentPage) {
                    answer.copy(
                        answer = newAnswer
                    )
                } else {
                    answer
                }
            }

            _state.update {
                it.copy(
                    userAnswers = newAnswers
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _effect.emit(
                Effect.ShowMessage(e.message?:"오류가 발생했습니다")
            )
        }

    }



    /*
    다음 질문으로 넘어가기
     */
    fun moveToNextPage() = viewModelScope.launch {
        try {
            if (state.value.interviewState != InterviewState.InProgress) {
                throw Exception("면접이 진행 중이지 않습니다")
            }

            val currentPage = state.value.currentPage ?: throw Exception("질문지가 제대로 초기화되지 않았습니다")

            val questions = state.value.questionnaire?.questions ?: throw Exception("질문지가 제대로 초기화되지 않았습니다")

            val durations = state.value.durations ?: throw Exception("질문지가 제대로 초기화되지 않았습니다")

            if(currentPage + 1 < questions.size) {
                _state.update {
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

            closeDialog()
            _effect.emit(
                Effect.ShowMessage("${state.value.userAnswers?.get(currentPage)?.answer}")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            _effect.emit(
                Effect.ShowMessage(e.message ?: "잠시 후 다시 시도해주세요")
            )
        }
    }


    /*
    Lock을 걸어서 3초마다 실시간 피드백이 생성되도록 함
    Lock이랑 면접 상태를 검사함
     */
    private suspend inline fun handleLiveFeedback(liveFeedback: LiveFeedback, block: () -> Unit) {
        val liveFeedbackState = state.value.liveFeedbackState

        val interviewState = state.value.interviewState

        if (liveFeedbackState == LiveFeedbackState.UnLocked && interviewState == InterviewState.InProgress) {
            _state.update {
                it.copy(
                    liveFeedbackState = LiveFeedbackState.Locked
                )
            }

            block()

            delay(10000)

            _state.update {
                it.copy(
                    liveFeedbackState = LiveFeedbackState.UnLocked
                )
            }
        }
    }

    /*
    면접 질문지 초기화하기
     */
    fun initQuestionnaire(questionnaire: Questionnaire?) = viewModelScope.launch {
        if (questionnaire == null) {
            _effect.emit(
                Effect.ShowMessage("잠시 후 다시 시도해주세요")
            )
            _state.update {
                it.copy(
                    networkState = NetworkState.Error("개인 면접 질문지를 받지 못했어요 :(")
                )
            }
            return@launch
        }

        _state.update {
            it.copy(
                interviewState = InterviewState.Ready
            )
        }

        val answers = mutableListOf<AnswerItem>()

        questionnaire.questions.forEach {
            answers.add(
                AnswerItem(
                    answerUUID = UUID.randomUUID().toString(),
                    questionUUID = it.uuid,
                    answer = "",
                    question = it.question
                )
            )
        }

        _state.update {
            it.copy(
                interviewState = InterviewState.Prepared,
                questionnaire = questionnaire,
                progress = 0,
                currentPage = 0,
                durations = List(answers.size) { 0 },
                userAnswers = answers
            )
        }
    }


    /*
    면접 시작하기
     */
    fun startInterview() = viewModelScope.launch {
        val currentState = state.value.interviewState
        if(currentState == InterviewState.Prepared) {
            _state.update {
                it.copy(
                    interviewState = InterviewState.InProgress,
                    uiState = UIState.ShowUI
                )
            }
            _dialogState.update {
                DialogState.Nothing
            }
        } else {
            _effect.emit(
                Effect.ShowMessage("아직 준비 되지 않았어요 :(")
            )
        }
    }

    /*
    면접 종료하기
     */
    fun finishInterview() = viewModelScope.launch {
        try {
            val durations = state.value.durations ?: throw Exception("면접 질문지 초기화 되지 않음")
            val currentPage = state.value.currentPage ?: throw Exception("면접 질문지 초기화 되지 않음")

            _state.update {
                val newCumDurations = durations.mapIndexed { index, duration ->
                    if (index == currentPage) {
                        it.progress
                    } else {
                        duration
                    }
                }.toMutableList()

                for (i in newCumDurations.size - 1 downTo 0) {
                    val beforeDuration = if (i - 1 < 0) 0 else newCumDurations[i - 1]
                    newCumDurations[i] -= beforeDuration
                }
                it.copy(
                    networkState = NetworkState.Loading("피드백을 생성하고 있습니다 :)"),
                    durations = newCumDurations,
                    cameraPreviewState = CameraPreviewState.Off,
                    uiState = UIState.NotShowUI,
                    interviewState = InterviewState.Paused,
                    recognizerState = RecognizerState.Stopped
                )
            }

            val answers = state.value.userAnswers ?: throw Exception("면접 질문지 초기화 되지 않음")

            val questionnaireUUID = state.value.questionnaire?.uuid ?: throw Exception("면접 질문지 초기화 되지 않음")

            val interviewData = InterviewData(
                answers = answers,
                badExpressions = state.value.badExpressions,
                badPose = state.value.badPoses,
                durations = durations,
                progress = durations.sum(),
                questionnaireUUID = questionnaireUUID
            )

            val result = repository.getInterviewFeedback(interviewData)

            if(result.isFailure || result.getOrNull() == null) {
                _effect.emit(
                    Effect.ShowMessage("네트워크 오류")
                )
                _state.update {
                    it.copy(
                        networkState = NetworkState.Error("네트워크 오류")
                    )
                }
                return@launch
            }

            _state.update {
                it.copy(
                    interviewState = InterviewState.Finished(result.getOrNull()!!)
                )
            }


        } catch (e: Exception) {
            e.printStackTrace()
            _state.update {
                it.copy(
                    networkState = NetworkState.Error(e.message)
                )
            }
        }

    }

    /*
    면접 일시 중지하기
     */
    fun pauseInterview() = viewModelScope.launch {
        val currentState = state.value.interviewState
        if(currentState == InterviewState.InProgress || currentState == InterviewState.Prepared) {
            _state.update {
                it.copy(
                    interviewState = InterviewState.Paused,
                    recognizerState = RecognizerState.Stopped
                )
            }
            _effect.emit(
                Effect.ShowMessage("면접이 일시 중지 되었어요")
            )
        }
    }

    /*
    면접 재시작하기
     */
    fun restartInterview() = viewModelScope.launch {
        val currentState = state.value.interviewState
        if(currentState == InterviewState.Paused) {

            _state.update {
                it.copy(
                    interviewState = InterviewState.InProgress
                )
            }

            _effect.emit(
                Effect.ShowMessage("면접이 재시작 되었어요")
            )

        }
    }

    /*
    데시벨 업데이트
     */
    fun updateDecibel(decibel: Int) = viewModelScope.launch {
        _state.update {
            it.copy(
                decibel = decibel
            )
        }
    }

    /*
    인터뷰 진행 중일 때만 UI (답변 제출 등 버튼) 보여지게 상태 처리
     */
    fun handleUIState(uiState: UIState? = null) = viewModelScope.launch {
        if (uiState == null) {
            val newState = when (state.value.uiState) {
                UIState.ShowUI -> UIState.NotShowUI
                UIState.NotShowUI -> UIState.ShowUI
            }
            _state.update {
                it.copy(
                    uiState = newState
                )
            }

        } else {
            _state.update {
                it.copy(
                    uiState = uiState
                )
            }
        }
    }

    /*
    Android Recognizer 상태 처리
     */
    fun handleRecognizerState(recognizerState: RecognizerState) = viewModelScope.launch {
        _state.update {
            it.copy(
                recognizerState = recognizerState
            )
        }
    }

    /*
    카메라 프리뷰 온오프 상태 처리
     */
    fun handleCameraPreviewState(cameraPreviewState: CameraPreviewState? = null) =
        viewModelScope.launch {
            if (cameraPreviewState == null) { //매개변수 전달 안 하면 그냥 반전시키기
                val newState = when (state.value.cameraPreviewState) {
                    CameraPreviewState.On -> CameraPreviewState.Off
                    CameraPreviewState.Off -> CameraPreviewState.On
                }

                _state.update {
                    it.copy(
                        cameraPreviewState = newState
                    )
                }

            } else { //카메라 프리뷰 설정 바꿔주기
                _state.update {
                    it.copy(
                        cameraPreviewState = cameraPreviewState
                    )
                }
            }
        }

    fun updateLiveFeedback(type: LiveFeedback.Type, message: String, index: Int) =
        viewModelScope.launch {

            val progress = state.value.progress

            val liveFeedback = LiveFeedback(
                timestamp = progress,
                type = type,
                message = message,
                index = index
            )

            //같은 메시지면 처리하지 않음
            val isSame = state.value.newLiveFeedback?.message == liveFeedback.message
            if (isSame) return@launch

            handleLiveFeedback(liveFeedback) {
                val oldLiveFeedback = state.value.newLiveFeedback
                val newLiveFeedback = liveFeedback
                val badExpressions = state.value.badExpressions.toMutableList()
                val badPoses = state.value.badPoses.toMutableList()

                when (liveFeedback.type) {
                    LiveFeedback.Type.Pose -> {
                        liveFeedback.index?.let {
                            badPoses[it] += 1
                        }
                    }

                    LiveFeedback.Type.Expression -> {
                        liveFeedback.index?.let {
                            badExpressions[it] += 1
                        }
                    }

                    else -> Unit
                }

                _state.update {
                    it.copy(
                        oldLiveFeedback = oldLiveFeedback,
                        newLiveFeedback = newLiveFeedback,
                        badExpressions = badExpressions,
                        badPoses = badPoses
                    )
                }

            }
        }


    data class State(
        var progress: Long = 0,
        var interviewState: InterviewState = InterviewState.Ready,
        var liveFeedbackState: LiveFeedbackState = LiveFeedbackState.UnLocked,
        val networkState: NetworkState = NetworkState.Normal,
        val cameraPreviewState: CameraPreviewState = CameraPreviewState.On,
        val recognizerState: RecognizerState = RecognizerState.Stopped,
        val uiState: UIState = UIState.NotShowUI,
        var oldLiveFeedback: LiveFeedback? = null,
        var newLiveFeedback: LiveFeedback? = null,
        var badExpressions: List<Int> = List(4) { 0 },
        var badPoses: List<Int> = List(2) { 0 },
        val userAnswers: List<AnswerItem>? = null,
        val questionnaire: Questionnaire? = null,
        val currentPage: Int? = null,
        val durations: List<Long>? = null,
        val decibel: Int = 0
    )

    sealed class NetworkState {
        object Normal : NetworkState()
        data class Loading(val message: String? = null) : NetworkState()
        data class Error(val message: String? = null) : NetworkState()
    }

    sealed class LiveFeedbackState {
        object Locked : LiveFeedbackState()
        object UnLocked : LiveFeedbackState()
    }

    sealed class InterviewState {
        object Ready : InterviewState()
        object Prepared : InterviewState()
        object InProgress : InterviewState()
        object Paused : InterviewState()
        data class Finished(val interviewResult: InterviewResult) : InterviewState()
    }

    enum class CameraPreviewState {
        On,
        Off
    }

    enum class RecognizerState {
        Started,
        Stopped
    }

    enum class UIState {
        ShowUI,
        NotShowUI
    }


    sealed class DialogState {
        object ShowCountdownDialog : DialogState()
        data class ShowEditAnswerDialog(
            val answer: AnswerItem,
            val question: QuestionItem
        ) : DialogState()
        object Nothing: DialogState()
    }


    sealed class Effect {
        data class ShowMessage(val message: String) : Effect()
        data class NavigateTo(
            val route: String,
            val builder: NavOptionsBuilder.() -> Unit
        ) : Effect()



    }

}