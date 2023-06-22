package com.capstone.Capstone2Project.ui.screen.interview

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.LiveFeedback
import com.capstone.Capstone2Project.data.model.Questionnaire
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.ui.screen.error.ErrorScreen
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.ExpressionAnalyzer
import com.capstone.Capstone2Project.utils.InterviewManager
import com.capstone.Capstone2Project.utils.LocalInterviewManager
import com.capstone.Capstone2Project.utils.RequestPermissions
import com.capstone.Capstone2Project.utils.YuvToRgbConverter
import com.capstone.Capstone2Project.utils.composable.ComposableLifecycle
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.etc.muteBeepSound
import com.capstone.Capstone2Project.utils.etc.toLiveFeedbackInfo
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.extensions.progressToString
import com.capstone.Capstone2Project.utils.theme.LocalSpacing
import com.capstone.Capstone2Project.utils.theme.bg_grey
import com.capstone.Capstone2Project.utils.theme.text_blue
import com.capstone.Capstone2Project.utils.theme.text_red
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.streams.toList


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InterviewScreen2(
    navController: NavController,
    questionnaire: Questionnaire?
) {

    val viewModel: InterviewViewModel = hiltViewModel()

    val context = LocalContext.current

    /*
    OnCreate 에서 Questionnaire 뷰모델에 전달하면
    READY -> Prepared -> 뷰모델에서 면접 상태 (Prepared) 감지 -> CountDown 띄워줌
    -> CountDown 끝나면 뷰모델의 startInterview() 호출 -> InProgress

    OnResume 에서 뷰모델의 restartInterview() 호출
    (면접 상태가 Paused 일 때만 작동 해서 맨처음엔 restart 작동 안 함)

    OnPause 에서 뷰모델의 pauseInterview() 호출
    (면접 상태가 Prepared, InProgress 일 때만 작동)

     */
    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                viewModel.initQuestionnaire(questionnaire)
            }

            Lifecycle.Event.ON_RESUME -> {
                viewModel.restartInterview()
            }

            Lifecycle.Event.ON_PAUSE -> {
                viewModel.pauseInterview()
            }

            else -> Unit
        }
    }

    /*
    권한이 모두 통과 되면 넘어 감
     */
    val permissions = remember {
        listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    if (!permissionState.allPermissionsGranted) {
        RequestPermissions(permissionState)
    } else {
        CompositionLocalProvider(
            LocalTextStyle provides TextStyle(
                fontFamily = CustomFont.nexonFont,
                color = Color.White
            )
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                InterviewScreenContent(
                    navController = navController,
                    viewModel = viewModel
                )
                UIContent(
                    navController = navController,
                    viewModel = viewModel
                )

            }
        }

    }

    /*
    뒤로가기 버튼 처리
     */
    var backPressCnt by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(backPressCnt) {
        if (backPressCnt > 0) {
            delay(2000)
            backPressCnt -= 1
        }
    }

    BackHandler {
        if (backPressCnt == 1) {
            navController.navigate(ROUTE_HOME) {
                popUpTo(ROUTE_HOME) {
                    inclusive = true
                }
            }
        } else {
            backPressCnt += 1
            AlertUtils.showToast(context, "한번 더 누르면 홈화면으로 이동해요")
        }
    }

    /*
    다이얼로그 상태 처리
     */
    val dialogState = viewModel.dialogState.collectAsStateWithLifecycle()

    when (dialogState.value) {
        InterviewViewModel.DialogState.Nothing -> Unit
        InterviewViewModel.DialogState.ShowCountdownDialog -> {
            InterviewCountDownDialog(viewModel::startInterview)
        }

        is InterviewViewModel.DialogState.ShowEditAnswerDialog -> {
            val answer =
                (dialogState.value as InterviewViewModel.DialogState.ShowEditAnswerDialog).answer.answer
            val question =
                (dialogState.value as InterviewViewModel.DialogState.ShowEditAnswerDialog).question.question

            EditAnswerDialog(
                answer = answer,
                question = question,
                dismissAction = viewModel::closeDialog,
                moveToNextAction = viewModel::moveToNextPage,
                updateAction = {
                    viewModel.updateAnswer(it)
                }
            )
        }
    }


    /*
    Effect 처리
     */
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest {
            when (it) {
                is InterviewViewModel.Effect.NavigateTo -> {
                    navController.navigate(it.route, it.builder)
                }

                is InterviewViewModel.Effect.ShowMessage -> {
                    AlertUtils.showToast(context, it.message)
                }
            }
        }
    }


}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun EditAnswerDialog(
    dismissAction: () -> Unit,
    question: String,
    answer: String,
    updateAction: (String) -> Unit,
    moveToNextAction: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/circle_rocket.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val spacing = LocalSpacing.current

    //재시작인지도 파악하기

    var modifiedAnswer by remember {
        mutableStateOf(answer)
    }

    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val coroutineScope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current

    val textFieldFocused = remember {
        mutableStateOf(false)
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont
        )
    ) {

        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableWithoutRipple {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                    .padding(spacing.medium)
                    .padding(bottom = 60.dp)
            ) {

                Column(
                    modifier = Modifier
                        .offset(y = 60.dp)
                        .shadow(
                            5.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Spacer(modifier = Modifier.height(60.dp))

                    Text(
                        text = "답변 제출 전",
                        style = LocalTextStyle.current.copy(
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.medium)
                    )

                    Spacer(modifier = Modifier.height(spacing.small))

                    Text(
                        text = "음성인식된 텍스트에서 오타를 수정해보세요 !",
                        style = LocalTextStyle.current.copy(
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.medium)
                    )

                    Spacer(modifier = Modifier.height(spacing.medium))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                color = Color.LightGray
                            )
                            .background(
                                color = bg_grey
                            )
                            .verticalScroll(rememberScrollState())
                            .padding(spacing.medium)

                    ) {


                        Text(
                            "Q. $question",
                            style = LocalTextStyle.current.copy(
                                color = Color.Black,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(spacing.small))

                        TextField(
                            modifiedAnswer,
                            onValueChange = {
                                modifiedAnswer = it
                                updateAction(it)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .bringIntoViewRequester(bringIntoViewRequester)
                                .onFocusEvent { focusState ->
                                    if (focusState.isFocused) {
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                }
                                .onFocusChanged { focusState ->
                                    textFieldFocused.value = focusState.isFocused
                                },
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.Black,
                                disabledTextColor = Color.Black,
                                backgroundColor = Color.Transparent,
                                cursorColor = Color.LightGray,
                                errorCursorColor = text_red,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            textStyle = LocalTextStyle.current.copy(
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start
                            )
                        )

                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    dismissAction()
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                10.dp,
                                Alignment.CenterHorizontally
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                tint = text_blue,
                                contentDescription = null
                            )

                            Text(
                                text = "돌아가기",
                                style = LocalTextStyle.current.copy(
                                    color = text_blue,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }

                        Text(
                            text = "답변 제출",
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    moveToNextAction()
                                },
                            style = LocalTextStyle.current.copy(
                                color = text_blue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        )
                    }

                }

                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(180.dp)
                        .padding(spacing.medium)
                )

            }

        }
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun InterviewCountDownDialog(
    dismiss: () -> Unit
) {
    var count by remember {
        mutableStateOf(5)
    }

    val spacing = LocalSpacing.current

    LaunchedEffect(true) {
        launch {
            while (true) {
                if (count == 0) {
                    dismiss()
                }
                count--
                delay(1000L)
            }
        }
    }

    AnimatedVisibility(
        count >= 0,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center) {
            it * 2
        },
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedContent(count,
                transitionSpec = {
                    fadeIn(tween(durationMillis = 1000)) + scaleIn(

                    ) with fadeOut(tween(durationMillis = 1000)) + scaleOut()

                }
            ) {
                Text(
                    text = if (count > 0) count.toString() else "Start",
                    style = LocalTextStyle.current.copy(
                        fontSize = if (count > 0) 150.sp else 100.sp,
                        fontFamily = CustomFont.nexonFont,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        shadow = Shadow(
                            offset = Offset(1f, 1f),
                            color = Color.Black,
                            blurRadius = 4f
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            if (count > 0) {
                Text(
                    text = if (count > 2) "면접이 곧 시작됩니다" else "준비해주세요",
                    style = LocalTextStyle.current.copy(
                        fontSize = 30.sp,
                        fontFamily = CustomFont.nexonFont,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        shadow = Shadow(
                            offset = Offset(1f, 1f),
                            color = Color.Black,
                            blurRadius = 4f
                        )
                    )
                )
            }

        }
    }
}

@Composable
private fun UIContent(
    navController: NavController,
    viewModel: InterviewViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current

    val state = viewModel.state.collectAsState()

    val context = LocalContext.current

    var showMoreInfo by remember {
        mutableStateOf(false)
    }

    var showLiveFeedback by remember {
        mutableStateOf(true)
    }

    fun getQuestion(): String? {
        return try {
            val currentPage = state.value.currentPage!!
            state.value.questionnaire?.questions?.get(currentPage)?.question
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAnswer(): String? {
        return try {
            val currentPage = state.value.currentPage!!
            state.value.userAnswers?.get(currentPage)?.answer
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /*
    STT
     */

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        //음성 인식기에 사용되는 음성모델 정보 설정
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        //음성 인식기에 인식되는 언어 설정
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR")

        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

        //부분 인식결과를 출력할 것인지를 설정 default가 false라서 여기서는 true로 지정
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

    }

    val speechRecognizerState = remember {
        mutableStateOf(false)
    }

    val speechRecognizer: SpeechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }


    val listener = object : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) {
            //말하기 시작할 준비가 되면 호출

        }

        override fun onBeginningOfSpeech() {
            //말하기 시작했을 때 호출

        }

        override fun onRmsChanged(rmsdB: Float) {
            //입력받는 소리의 크기를 알려줌
            if (rmsdB >= 0.0f) {
                viewModel.updateDecibel(rmsdB.roundToInt())
            }
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            //말을 시작하고 인식이 된 단어를 buffer에 담음
        }

        override fun onEndOfSpeech() {
            //말하기를 잠시 중지하면 호출
            Log.d("TAG", "onEndOfSpeech 호출")
        }

        override fun onError(error: Int) {
            //네트워크 또는 인식 오류가 발생했을 때 호출
            when (error) {
                SpeechRecognizer.ERROR_AUDIO -> { //3
                    //오디오 에러
                }

                SpeechRecognizer.ERROR_CLIENT -> { //5
                    //클라이언트 에러
                    //start, cancel, stop 등을 실패할 경우

                }

                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                    //퍼미션 부족
                }

                SpeechRecognizer.ERROR_NETWORK -> {
                    //네트워크 에러
                }

                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                    //네트워크 타임 아웃
                }

                SpeechRecognizer.ERROR_NO_MATCH -> { //7
                    //에러 찾을 수 없음

                }

                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> { //8
                    //Recognizer 가 바쁨

                }

                SpeechRecognizer.ERROR_SERVER -> {
                    //서버가 이상함
                }

                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                    //말하는 시간 초과
                }

                else -> {
                    //알 수 없는 오류
                }
            }

            speechRecognizerState.value = false

            //AlertUtils.showToast(context, "오류 $error")

        }

        override fun onResults(results: Bundle?) {
            //인식 결과가 준비되면 호출
            //말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            results ?: return
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            //val confidenceScore = results.getFloat(RecognizerIntent.EXTRA_CONFIDENCE_SCORES)
            matches?.let {
                it.forEach { match ->
                    viewModel.appendAnswer(match)
                }
            }

            speechRecognizerState.value = false

        }

        override fun onPartialResults(partialResults: Bundle?) {
            //부분 인식 결과를 사용할 수 있을 때 호출
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            //이벤트를 추가하기 위해 예약
        }


    }

    fun startSpeechRecognize() {
        if (!speechRecognizerState.value) { // 실행중 X
            muteBeepSound(context)
            speechRecognizer.apply {
                setRecognitionListener(listener)
                startListening(intent)
            }
            speechRecognizerState.value = true
        }
    }

    fun stopSpeechRecognize() {
        if (speechRecognizerState.value) { //실행중이면
            muteBeepSound(context)
            speechRecognizer.apply {
                stopListening()
                destroy()
            }

            speechRecognizerState.value = false
        }
    }

    LaunchedEffect(speechRecognizerState.value) {
        if (state.value.recognizerState == InterviewViewModel.RecognizerState.Started) {
            startSpeechRecognize()
        } else {
            stopSpeechRecognize()
        }
    }


    LaunchedEffect(state.value.recognizerState) {
        if (state.value.recognizerState == InterviewViewModel.RecognizerState.Started) { //유저한테 요청 들어오면
            startSpeechRecognize()
        } else {
            stopSpeechRecognize()
        }
    }


    /*
    카메라 프리뷰 가리는 용
     */
    if (state.value.cameraPreviewState == InterviewViewModel.CameraPreviewState.Off) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/meteor.json"))
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever,
                speed = 1f
            )


            Image(
                painter = painterResource(id = R.drawable.bg_galaxy),
                contentScale = ContentScale.FillHeight,
                contentDescription = null
            )

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .fillMaxHeight()
                    .alpha(0.4f),
                contentScale = ContentScale.FillHeight
            )

        }

    }


    when (state.value.networkState) {
        is InterviewViewModel.NetworkState.Error -> {
            val message =
                (state.value.networkState as InterviewViewModel.NetworkState.Error).message
            ErrorScreen(message)
        }

        is InterviewViewModel.NetworkState.Loading -> {
            val message =
                (state.value.networkState as InterviewViewModel.NetworkState.Loading).message
            LoadingScreen(message)
        }

        InterviewViewModel.NetworkState.Normal -> {

            /*
            UI 들 보여주는 용
             */
            if (state.value.uiState == InterviewViewModel.UIState.ShowUI) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xB3000000)
                                )
                            )
                        )
                        .padding(top = 40.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(spacing.medium),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        if (!showMoreInfo) {
                            MoreButton(
                                buttonClicked = {
                                    showMoreInfo = !showMoreInfo
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(spacing.large))

                        Spacer(modifier = Modifier.height(spacing.large))

                        LiveFeedbackContent(
                            oldLiveFeedback = state.value.oldLiveFeedback,
                            newLiveFeedback = state.value.newLiveFeedback
                        )

                        getQuestion()?.let {
                            QuestionAnswerContent(
                                question = it,
                                answer = getAnswer()
                            )
                        }



                        BottomButtons(
                            speechRecognizerState = state.value.recognizerState,
                            sttButtonClicked = {
                                if (state.value.recognizerState == InterviewViewModel.RecognizerState.Started) {
                                    //음성인식 중
                                    //음성인식 중단
                                    viewModel.handleRecognizerState(InterviewViewModel.RecognizerState.Stopped)

                                } else { //음성인식 중 X
                                    //음성인식 시작
                                    when (state.value.interviewState) {
                                        InterviewViewModel.InterviewState.InProgress -> {
                                            viewModel.handleRecognizerState(InterviewViewModel.RecognizerState.Started)
                                        }

                                        else -> Unit
                                    }
                                }
                            },
                            handleRecognizerState = {
                                viewModel.handleRecognizerState(it)
                            },
                            appendAnswer = {
                                viewModel.appendAnswer(it)
                            },
                            deleteAnswer = {
                                viewModel.deleteAnswer()
                            },
                            checkAnswer = viewModel::checkAnswer,
                            cameraPreviewState = state.value.cameraPreviewState,
                            curPage = state.value.currentPage,
                            questionSize = state.value.questionnaire?.questions?.size,
                            progress = state.value.progress,
                            decibel = state.value.decibel
                        )


                    }
                }
            }

            /*
            더보기 창 띄우는 용
             */

            if (showMoreInfo) {
                /*
                뷰모델에 일시정지 요청
                 */
                MoreInfoScreen(
                    outSideClicked = {
                        showMoreInfo = !showMoreInfo
                    },
                    exitButtonClicked = {
                        /*
                        뷰모델에 요청하거나 바로 나가기 또는 다이얼로그
                         */
                        navController.navigate(ROUTE_HOME) {
                            popUpTo(ROUTE_HOME) {
                                inclusive = true
                            }
                        }
                    },
                    liveButtonClicked = {
                        showLiveFeedback = it
                    },
                    cameraOnOffButtonClicked = viewModel::handleCameraPreviewState,
                    showPreview = state.value.cameraPreviewState == InterviewViewModel.CameraPreviewState.On
                )
            }
        }
    }


}

@Composable
private fun InterviewScreenContent(
    navController: NavController,
    viewModel: InterviewViewModel = hiltViewModel()
) {
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont,
            color = Color.White
        )
    ) {


        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            /*
            카메라 프리뷰
             */
            InterviewCameraPreview(viewModel) { type, message, index ->
                viewModel.updateLiveFeedback(type, message, index)
            }


        }


    }
}

@Composable
private fun MoreInfoScreen(
    outSideClicked: () -> Unit,
    exitButtonClicked: () -> Unit,
    liveButtonClicked: (Boolean) -> Unit,
    cameraOnOffButtonClicked: () -> Unit,
    showPreview: Boolean
) {

    val spacing = LocalSpacing.current


    val showFeedback = remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0x80000000)
            )
            .clickable {
                outSideClicked()
            }
            .padding(top = 40.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier.clickable {
                    showFeedback.value = !showFeedback.value
                    liveButtonClicked(showFeedback.value)
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_live),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = if (showFeedback.value) "피드백 끄기" else "피드백 켜기",
                    style = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        shadow = Shadow(
                            color = Color.DarkGray,
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    )
                )
            }


            Spacer(modifier = Modifier.height(spacing.medium))

            Column(
                modifier = Modifier.clickable {
                    cameraOnOffButtonClicked()
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = if (showPreview) R.drawable.ic_camera_alt_24 else R.drawable.ic_camera_off),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = if (showPreview) "카메라 끄기" else "카메라 켜기",
                    style = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        shadow = Shadow(
                            color = Color.DarkGray,
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Column(
                modifier = Modifier.clickable {
                    exitButtonClicked()
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_exit),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "나가기",
                    style = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        shadow = Shadow(
                            color = Color.DarkGray,
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    )
                )
            }


        }
    }
}

@Composable
private fun BottomButtons(
    speechRecognizerState: InterviewViewModel.RecognizerState,
//    updateDecibel: (Int) -> Unit,
//    handleRecognizerState: (Boolean) -> Unit,
    sttButtonClicked: () -> Unit,
    handleRecognizerState: (InterviewViewModel.RecognizerState) -> Unit,
    appendAnswer: (String) -> Unit,
    deleteAnswer: () -> Unit,
    checkAnswer: () -> Unit,
    cameraPreviewState: InterviewViewModel.CameraPreviewState,
    curPage: Int?,
    questionSize: Int?,
    progress: Long,
    decibel: Int?
) {
    val spacing = LocalSpacing.current

    val context = LocalContext.current

    val questionCnt = (questionSize ?: 0) - (curPage ?: 0) - 1


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.medium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.large),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {

            Text(
                if (questionCnt == 0) "마지막 질문" else "남은질문 ${questionCnt}개",
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(
                        color = Color.DarkGray,
                        offset = Offset(1f, 1f),
                        blurRadius = 4f
                    ),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            )

            Text(
                progress.progressToString(),
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(
                        color = Color.DarkGray,
                        offset = Offset(1f, 1f),
                        blurRadius = 4f
                    ),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            )

            Text(
                "${(decibel ?: 0f).toInt()}dB",
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(
                        color = Color.DarkGray,
                        offset = Offset(1f, 1f),
                        blurRadius = 4f
                    ),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(spacing.small))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.large)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(50)
                )
                .background(
                    color = if (cameraPreviewState == InterviewViewModel.CameraPreviewState.On) Color(
                        0x80000000
                    ) else Color(0x887A7A7A),
                    shape = RoundedCornerShape(50)
                )
                .padding(spacing.medium),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_trash),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.clickable {
                    deleteAnswer()
                }
            )


            Icon(
                painter = painterResource(
                    if (speechRecognizerState == InterviewViewModel.RecognizerState.Started) R.drawable.ic_pause_circle
                    else R.drawable.ic_interview_mic
                ),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.clickable {
                    sttButtonClicked()
                    if (speechRecognizerState == InterviewViewModel.RecognizerState.Started) {
                        //음성인식 중
                        //음성인식 중단
                        handleRecognizerState(InterviewViewModel.RecognizerState.Stopped)

                    } else {
                        handleRecognizerState(InterviewViewModel.RecognizerState.Started)
                    }

                }
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.clickable {
                    handleRecognizerState(InterviewViewModel.RecognizerState.Stopped)
                    checkAnswer()
                }
            )
        }
    }
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
private fun InterviewCameraPreview(
    viewModel: InterviewViewModel,
    updateLiveFeedback: (LiveFeedback.Type, String, Int) -> Unit
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current

    val faceDetector = remember {

        val options = FaceDetectorOptions.Builder()
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .enableTracking()
            .build()

        FaceDetection.getClient(options)
    }


    val poseDetector = remember {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            .setPreferredHardwareConfigs(AccuratePoseDetectorOptions.CPU_GPU)
            .build()

        PoseDetection.getClient(options)
    }

    val expressionAnalyzer = remember {
        ExpressionAnalyzer(context)
    }

    val coroutineScope = rememberCoroutineScope()

    val yuvToRgbConverter = remember {
        YuvToRgbConverter(context)
    }


    val imageAnalyzer = remember {
        InterviewManager.ImageAnalyzer { imageProxy ->

            imageProxy.image?.let { image ->
                val inputImage =
                    InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)

                var readyToDetectFace = true
                var readyToDetectPose = true

                fun completeDetection() {
                    if (!readyToDetectFace && !readyToDetectPose) {
                        readyToDetectFace = true
                        readyToDetectPose = true
                        image.close()
                        imageProxy.close()
                    }
                }

                if (readyToDetectFace) {
                    coroutineScope.launch {
                        try {
                            val bitmapImage =
                                Bitmap.createBitmap(
                                    image.width,
                                    image.height,
                                    Bitmap.Config.ARGB_8888
                                )
                            yuvToRgbConverter.yuvToRgb(image, bitmapImage)
                            val classifiedExpressions =
                                expressionAnalyzer.classifyExpression(bitmapImage)
                            processExpressionClassificationResult(
                                classifiedExpressions
                            ) { type, message, index ->
//                                updateLiveFeedback(type, message, index)
                                viewModel.updateLiveFeedback(type, message, index)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            readyToDetectFace = false
                            completeDetection()
                        }
                    }
                }

                poseDetector.process(inputImage)
                    .addOnSuccessListener { pose ->
                        if (readyToDetectPose) {
                            processPoseDetectionResult(pose) { type, message, index ->
//                                updateLiveFeedback(type, message, index)
                                viewModel.updateLiveFeedback(type, message, index)
                            }
                            readyToDetectPose = false
                        }
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
                    .addOnCompleteListener {
                        completeDetection()
                    }
            }
        }
    }


    val interviewManager = remember {
        InterviewManager.Builder(context)
            .setImageAnalyzer(imageAnalyzer)
            .setLifecycleOwner(lifecycleOwner)
            .build()
    }

    CompositionLocalProvider(
        LocalInterviewManager provides interviewManager
    ) {
        Surface() {
            AndroidView(
                factory = {
                    interviewManager.showPreview()
                },
                modifier = Modifier.fillMaxSize(),
                update = {
                    interviewManager.updatePreview(
                        it,
                        true
                    )
                }
            )
        }
    }

}

private fun String.splitToCodePoints(): List<String> {
    return codePoints()
        .toList()
        .map {
            String(Character.toChars(it))
        }
}


@Composable
private fun TypingAnimatedText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 16.sp
) {

    var textToDisplay by remember {
        mutableStateOf("")
    }

    LaunchedEffect(text) {
        text.splitToCodePoints().forEachIndexed { charIndex, _ ->
            textToDisplay = text.splitToCodePoints()
                .take(
                    n = charIndex + 1,
                ).joinToString(
                    separator = "",
                )

            delay(160)
        }
    }



    Text(
        modifier = modifier,
        text = textToDisplay,
        style = LocalTextStyle.current.copy(
            fontSize = fontSize,
            fontWeight = FontWeight(550),
            shadow = Shadow(
                color = Color.DarkGray,
                offset = Offset(1f, 1f),
                blurRadius = 4f
            )
        )
    )
}


@Composable
private fun QuestionAnswerContent(
    question: String,
    answer: String?
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacing.medium)
    ) {

        if (question.isNotBlank()) {
            TypingAnimatedText(
                modifier = Modifier
                    .padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                "Q. $question",
                fontSize = 18.sp
            )

        }


        Spacer(modifier = Modifier.height(spacing.small))


        if (answer?.isNotBlank() == true) {
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_enter),
                    contentDescription = null,
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(spacing.small))

                Text(
                    answer ?: "",
                    style = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(550),
                        shadow = Shadow(
                            color = Color.DarkGray,
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    )
                )
            }
        }

    }

}

@Composable
private fun LiveFeedbackContent(
    modifier: Modifier = Modifier,
    oldLiveFeedback: LiveFeedback?,
    newLiveFeedback: LiveFeedback?
) {
    val spacing = LocalSpacing.current

    Column(
        verticalArrangement = Arrangement.spacedBy(
            spacing.small,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.End,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.small)
    ) {

        oldLiveFeedback?.let {
            OldLiveFeedbackContent(liveFeedback = it)
        }

        newLiveFeedback?.let {
            NewLiveFeedbackContent(liveFeedback = it)
        }

    }

}

@Composable
private fun ItemContent(
    modifier: Modifier = Modifier,
    liveFeedback: LiveFeedback,
    fontSize: TextUnit,
    maxLines: Int = Int.MAX_VALUE,
    isNew: Boolean
) {

    val spacing = LocalSpacing.current


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont,
            color = Color.White,
            shadow = Shadow(
                Color.Black,
                offset = Offset(1f, 1f),
                blurRadius = 4f
            ),
            fontSize = fontSize
        )
    ) {

        ConstraintLayout(
            modifier = modifier.padding(horizontal = spacing.small)
        ) {

            val (progressRef, messageRef, newRef) = createRefs()


            Text(
                liveFeedback.progressToString(),
                modifier = Modifier
                    .constrainAs(progressRef) {
                        end.linkTo(messageRef.start, margin = spacing.extraSmall)
                        bottom.linkTo(messageRef.bottom)
                    },
                style = LocalTextStyle.current.copy(
                    fontSize = fontSize.div(3).times(2),
                    color = Color.White,
                    fontWeight = FontWeight(550),
                    shadow = Shadow(
                        color = Color.DarkGray,
                        offset = Offset(1f, 1f),
                        blurRadius = 4f
                    )
                )
            )



            Text(
                liveFeedback.message,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .constrainAs(messageRef) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)

                        width = Dimension.percent(if (isNew) 0.9f else 0.8f)
                    }
                    .background(
                        color = Color(0x66000000),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(horizontal = spacing.medium, vertical = spacing.small),
                style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Medium,
                    shadow = Shadow(
                        color = Color.DarkGray,
                        offset = Offset(1f, 1f),
                        blurRadius = 4f
                    )
                )
            )

            if (isNew) {
                Text(
                    "New",
                    style = TextStyle(
                        fontFamily = CustomFont.nexonFont,
                        fontSize = 10.sp,
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.DarkGray,
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    ),
                    modifier = Modifier
                        .constrainAs(newRef) {
                            end.linkTo(messageRef.start)
                            top.linkTo(messageRef.top)
                            width = Dimension.wrapContent
                        }
                        .offset(x = spacing.small, y = spacing.extraSmall)
                        .background(
                            color = Color.Red,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)

                )
            }


        }


    }


}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NewLiveFeedbackContent(
    modifier: Modifier = Modifier,
    liveFeedback: LiveFeedback
) {

    val oldLiveFeedback by rememberUpdatedState(newValue = liveFeedback)

    AnimatedContent(
        targetState = oldLiveFeedback,
        transitionSpec = {
            slideInVertically {
                it
            } + fadeIn(
                initialAlpha = 0f
            ) with fadeOut(
                targetAlpha = 0f
            ) + slideOutVertically {
                -it
            }
        },
        modifier = modifier
    ) { il ->
        ItemContent(
            liveFeedback = il,
            fontSize = 16.sp,
            isNew = true,
            maxLines = 2
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OldLiveFeedbackContent(
    modifier: Modifier = Modifier,
    liveFeedback: LiveFeedback
) {

    val oldLiveFeedback by rememberUpdatedState(newValue = liveFeedback)

    AnimatedContent(
        targetState = oldLiveFeedback,
        transitionSpec = {
            slideInVertically {
                it
            } + fadeIn(
                initialAlpha = 0f
            ) with fadeOut(
                targetAlpha = 0f
            ) + slideOutVertically {
                -it
            }
        },
        modifier = modifier.alpha(0.5f)
    ) { il ->
        ItemContent(
            liveFeedback = il,
            fontSize = 14.sp,
            maxLines = 1,
            isNew = false
        )
    }
}

@Composable
private fun MoreButton(
    buttonClicked: () -> Unit
) {
    val spacing = LocalSpacing.current
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            tint = Color(0xFFFFFFFF),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(
                    color = Color(0x7C000000),
                    shape = CircleShape
                )
                .padding(
                    spacing.small
                )
                .clickable {
                    buttonClicked()
                }
        )
    }
}

private fun processExpressionClassificationResult(
    expressions: List<Float>,
    detectAction: (LiveFeedback.Type, String, Int) -> Unit
) {

    var maxProbability = -1f

    var idx = -1

    expressions.forEachIndexed { index, value ->
        if (value > maxProbability) {
            idx = index
            maxProbability = value
        }
    }

    var result = "부정적인 표정 감지 "

    val resultIdx: Int

    result += when (idx) {
        0 -> {
            resultIdx = 0
            "[Angry]"
        }

        1 -> {
            resultIdx = 1
            "[Disgust]"
        }

        2 -> {
            resultIdx = 2
            "[Fear]"
        }

        4 -> {
            resultIdx = 3
            "[Sad]"
        }

        else -> return
    }

    detectAction(LiveFeedback.Type.Expression, result, resultIdx)

}

private fun processPoseDetectionResult(
    pose: Pose?,
    detectAction: (LiveFeedback.Type, String, Int) -> Unit
) {

    pose?.toLiveFeedbackInfo()?.let {
        detectAction(it.first, it.second, it.third)
    }

}