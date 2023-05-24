package com.capstone.Capstone2Project.ui.screen.interview

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle.Event.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.LogLine
import com.capstone.Capstone2Project.data.model.Questionnaire
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.navigation.ROUTE_INTERVIEW_FINISHED
import com.capstone.Capstone2Project.ui.screen.animation.NewLogContent
import com.capstone.Capstone2Project.ui.screen.animation.OldLogContent
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.*
import com.capstone.Capstone2Project.utils.composable.ComposableLifecycle
import com.capstone.Capstone2Project.utils.etc.*
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.extensions.progressToString
import com.capstone.Capstone2Project.utils.service.ScreenRecordService
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.streams.toList

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InterviewScreen(
    navController: NavController,
    questionnaire: Questionnaire?
) {

    val interviewViewModel: InterviewViewModel = hiltViewModel()

    val state = interviewViewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val permissions = remember {
        listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    if (!permissionState.allPermissionsGranted) {
        RequestPermissions(permissionState)
    } else {
        InterviewUIScreenContent(navController = navController, questionnaire = questionnaire)
    }

    LaunchedEffect(interviewViewModel) {
        interviewViewModel.effect.collect {
            when(it) {
                is InterviewViewModel.Effect.ShowMessage -> {
                    val message = it.message
                    AlertUtils.showToast(context, message)
                }
            }
        }
    }

    ComposableLifecycle { _, event ->
        when (event) {
            ON_RESUME -> {
                /*
                커스텀 질문지 패치 전이면 로딩 띄우기
                패치 후면 다이얼로그 띄우기,
                진행중이던게 있으면 다시 시작할지,
                시작전이라면 인터뷰 가이드
                 */
                if (state.value.interviewState == InterviewViewModel.InterviewState.Paused) {
                    interviewViewModel.restartInterview()
                }

            }
            ON_PAUSE -> {
                /*
                아직 시작 전이면 처리 X
                진행 중이면 상태 처리 후 일시정지
                 */
                interviewViewModel.pauseInterview()
                stopRecordingService(context)
            }
            ON_DESTROY -> {
                /*
                백업 또는 날리기
                 */
            }
            else -> Unit
        }
    }


    state.value.let {
        when (it.interviewState) {
            is InterviewViewModel.InterviewState.Error -> {
                AlertUtils.showToast(
                    context,
                    (it.interviewState as InterviewViewModel.InterviewState.Error).message
                )
                navController.popBackStack()
            }

            InterviewViewModel.InterviewState.Prepared -> {
                /*
                인터뷰 시작 전 카운트 다운 다이얼로그 띄우기
                 */
                InterviewCountDownDialog(
                    dismiss = { }
                )

            }

            InterviewViewModel.InterviewState.Ready -> {
                LoadingScreen()
            }

            InterviewViewModel.InterviewState.Loading -> {
                LoadingScreen()
            }

            is InterviewViewModel.InterviewState.Finished -> {
                LaunchedEffect(Unit) {
                    val interviewUUID =
                        (it.interviewState as InterviewViewModel.InterviewState.Finished).interviewUUID
                    navController.navigate(
                        "$ROUTE_INTERVIEW_FINISHED/{interviewUUID}".replace(
                            oldValue = "{interviewUUID}",
                            newValue = interviewUUID
                        )
                    ) {

                        launchSingleTop = true

                        navController.currentDestination?.route?.let { route ->
                            popUpTo(route) {
                                inclusive = true
                            }
                        }

                    }

                }

            }

            is InterviewViewModel.InterviewState.EditAnswer -> {

                with((it.interviewState as InterviewViewModel.InterviewState.EditAnswer).qnA) {
                    BeforeSendingAnswerDialog(
                        dismissClick = { answer ->
                            interviewViewModel.updateAnswer(answer)
                            interviewViewModel.moveToNextPage()
                            //showAnswerDialog.value = false
                        },
                        question = this.questionItem.question,
                        answer = this.answerItem.answer
                    )
                }

            }

            else -> Unit
        }

    }


}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun InterviewCountDownDialog(
    dismiss: () -> Unit,
//                                     useRecording: Boolean
) {
    var count by remember {
        mutableStateOf(5)
    }

    val spacing = LocalSpacing.current

    val coroutineScope = rememberCoroutineScope()

    val interviewViewModel: InterviewViewModel = hiltViewModel()

    LaunchedEffect(true) {
        coroutineScope.launch {
            while (true) {
                if (count == 0) {
                    interviewViewModel.startInterview()
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
//
                }
            ) {
                Text(
                    text = if (count > 0) count.toString() else "Start",
                    style = LocalTextStyle.current.copy(
                        fontSize = if (count > 0) 150.sp else 100.sp,
                        fontFamily = nexonFont,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        shadow = Shadow(
                            offset = Offset(1f, 1f),
                            color = Black,
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
                        fontFamily = nexonFont,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        shadow = Shadow(
                            offset = Offset(1f, 1f),
                            color = Black,
                            blurRadius = 4f
                        )
                    )
                )
            }

        }
    }

}


@Composable
fun InterviewUIScreenContent(
    navController: NavController,
    questionnaire: Questionnaire?
) {

    val interviewViewModel: InterviewViewModel = hiltViewModel()
    //val state = interviewViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(interviewViewModel) {
        interviewViewModel.initQuestionnaire(questionnaire)
    }

    val spacing = LocalSpacing.current


    val showMoreInfo = remember {
        mutableStateOf(false)
    }

    val showPreview = remember {
        mutableStateOf(true)
    }

    val showFeedback = remember {
        mutableStateOf(true)
    }

    val showAnswerDialog = remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val isRecording = remember {
        mutableStateOf(false)
    }

    val screenRecordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode != Activity.RESULT_OK) {
            return@rememberLauncherForActivityResult
        }
        if (it.data == null) {
            return@rememberLauncherForActivityResult
        }

        startRecordingService(context, it.resultCode, it.data!!)
        isRecording.value = true
    }

    fun startRecording() {
        val mediaProjectionManager =
            context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val intent = mediaProjectionManager.createScreenCaptureIntent()
        screenRecordLauncher.launch(intent)
    }

    fun stopRecording() {
        stopRecordingService(context)
        isRecording.value = false
    }

    fun recordingButtonClicked() {
        if (isServiceRunning(context, ScreenRecordService::class.java)) {
            stopRecording()
        } else {
            startRecording()
        }
    }


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

//            InterviewCameraPreview(showPreview = showPreview.value)
            InterviewCameraPreview(showPreview = true)
            if (!showPreview.value) {
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

                    if (!showMoreInfo.value) {
                        MoreButton(
                            buttonClicked = {
                                showMoreInfo.value = !showMoreInfo.value
                            }
                        )
                    }

                    //아쉽지만 사용 불가
//                    RecordButton(
//                        buttonClicked = {
//                            recordingButtonClicked()
//                        },
//                        isRecording.value
//                    )

                    Spacer(modifier = Modifier.height(spacing.large))

                    Spacer(modifier = Modifier.height(spacing.large))

                    AnimatedVisibility(visible = showFeedback.value) {
                        InterviewLogContents(

                        )
                    }

                    QuestionAnswerContents()

                    BottomButtons(showPreview = showPreview.value) {
                        showAnswerDialog.value = true
                        interviewViewModel.checkAnswer()
                    }


                }
            }


            if (showMoreInfo.value) {
                /*
                뷰모델에 일시정지 요청
                 */
                MoreInfoScreen(
                    outSideClicked = {
                        showMoreInfo.value = !showMoreInfo.value
                    },
                    exitButtonClicked = {
                        /*
                        뷰모델에 요청하거나 바로 나가기 또는 다이얼로그
                         */
                        navController.popBackStack()
                    },
                    liveButtonClicked = {
                        showFeedback.value = it
                    },
                    cameraButtonClicked = {
                        showPreview.value = it
                    }
                )
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordButtonPreview() {

}

@Composable
fun RecordButton(buttonClicked: () -> Unit, isRecording: Boolean) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Icon(
            imageVector = if (!isRecording) Icons.Default.VideocamOff else Icons.Default.Videocam,
            contentDescription = null,
            tint = Color(0xFFFFFFFF),
            modifier = Modifier
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
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = if (isRecording) "녹화중" else "녹화시작",
            style = LocalTextStyle.current.copy(
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                shadow = Shadow(
                    offset = Offset(1f, 1f),
                    color = DarkGray,
                    blurRadius = 4f
                )
            )

        )
    }
}

@Composable
private fun MoreInfoScreen(
    outSideClicked: () -> Unit,
    exitButtonClicked: () -> Unit,
    liveButtonClicked: (Boolean) -> Unit,
    cameraButtonClicked: (Boolean) -> Unit,
) {

    val spacing = LocalSpacing.current

    val showPreview = remember {
        mutableStateOf(true)
    }

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
                    tint = White,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = if (showFeedback.value) "피드백 끄기" else "피드백 켜기",
                    style = LocalTextStyle.current.copy(
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        shadow = Shadow(
                            color = DarkGray,
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    )
                )
            }


            Spacer(modifier = Modifier.height(spacing.medium))

            Column(
                modifier = Modifier.clickable {
                    showPreview.value = !showPreview.value
                    cameraButtonClicked(showPreview.value)
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = if (showPreview.value) R.drawable.ic_camera_alt_24 else R.drawable.ic_camera_off),
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = if (showPreview.value) "카메라 끄기" else "카메라 켜기",
                    style = LocalTextStyle.current.copy(
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        shadow = Shadow(
                            color = DarkGray,
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
                    tint = White,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "나가기",
                    style = LocalTextStyle.current.copy(
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        shadow = Shadow(
                            color = DarkGray,
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    )
                )
            }


        }
    }
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
private fun InterviewCameraPreview(
    showPreview: Boolean
) {

    val interviewViewModel: InterviewViewModel = hiltViewModel()
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
                            val classifiedExpressions = expressionAnalyzer.classifyExpression(bitmapImage)
                            processExpressionClassificationResult(
                                classifiedExpressions,
                                viewModel = interviewViewModel
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e("TAG", "InterviewCameraPreview:${e.message} ")
                        } finally {
                            readyToDetectFace = false
                            completeDetection()
                        }
                    }
                }


//                faceDetector.process(inputImage)
//                    .addOnSuccessListener { faces ->
////                        if (readyToDetectFace) {
//////                            processFaceDetectionResult(
//////                                faces,
//////                                viewModel = interviewViewModel
//////                            )
////                            coroutineScope.launch {
////                                try {
//////                                    val bitmapImage = image.toBitmap()
//////
////                                    val bitmapImage = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
////                                    yuvToRgbConverter.yuvToRgb(image, bitmapImage)
////                                    val result = expressionAnalyzer.classifyExpression(bitmapImage)
////                                    processExpressionClassificationResult(
////                                        result,
////                                        viewModel = interviewViewModel
////                                    )
////
////
////                                } catch (e: Exception) {
////                                    e.printStackTrace()
////                                    Log.e("TAG", "InterviewCameraPreview: ${e.message}", )
////                                } finally {
////                                    readyToDetectFace = false
////                                }
////
////
////                            }
////
////                        }
//
//                    }
//                    .addOnFailureListener { e ->
//                        e.printStackTrace()
//                    }
//                    .addOnCompleteListener {
//                        completeDetection()
//                    }


                poseDetector.process(inputImage)
                    .addOnSuccessListener { pose ->
                        if (readyToDetectPose) {
                            processPoseDetectionResult(pose, viewModel = interviewViewModel)
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
        AndroidView(
            factory = {
                interviewManager.showPreview()
            },
            modifier = Modifier.fillMaxSize(),
            update = {

                interviewManager.updatePreview(it, showPreview)

            }
        )
    }

}

private fun processExpressionClassificationResult(
    expressions: List<Float>,
    viewModel: InterviewViewModel
) {

    var maxProbability = -1f

    var idx = -1

    expressions.forEachIndexed { index, value ->
        if(value > maxProbability) {
            idx = index
            maxProbability = value
        }
    }

    var result = "부정적인 표정 감지 "

    val resultIdx: Int

    result += when(idx) {
        0-> {
            resultIdx = 0
            "[Angry]"
        }
        1-> {
            resultIdx = 1
            "[Disgust]"
        }
        2-> {
            resultIdx = 2
            "[Fear]"
        }
        4-> {
            resultIdx = 3
            "[Sad]"
        }
        else-> return
    }

    val logLine = LogLine(type = LogLine.Type.Camera, message = result, index = resultIdx)

    viewModel.loadInterviewLogLine(
        logLine
    )
}

private fun processPoseDetectionResult(pose: Pose?, viewModel: InterviewViewModel) {

    pose?.toLogLine()?.let {
        viewModel.loadInterviewLogLine(it)
    }

}


private fun processFaceDetectionResult(
    faces: List<Face>,
    viewModel: InterviewViewModel
) {
    if (faces.isEmpty()) {
        val logLine = LogLine(
            type = LogLine.Type.Error,
            "얼굴을 발견하지 못했습니다"
        )

        viewModel.loadInterviewLogLine(logLine)
        return
    }

    val logLine = faces.last().toLogLine()

    logLine?.let {
        viewModel.loadInterviewLogLine(it)
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

@Composable
private fun InterviewLogContents(
    modifier: Modifier = Modifier
) {
    val interviewViewModel: InterviewViewModel = hiltViewModel()

    val newInterviewLogLine =
        interviewViewModel.newInterviewLogLineFlow.collectAsStateWithLifecycle()

    val oldInterviewLogLine =
        interviewViewModel.oldInterviewLogLineFlow.collectAsStateWithLifecycle()

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
        oldInterviewLogLine.value?.let {
            OldLogContent(interviewLogLine = it)
        }
        newInterviewLogLine.value?.let {
            NewLogContent(interviewLogLine = it)
        }

    }


}

@Composable
private fun BottomButtons(
    showPreview: Boolean,
    sendClicked: () -> Unit
) {

    val interviewViewModel: InterviewViewModel = hiltViewModel()

    val spacing = LocalSpacing.current

    val state = interviewViewModel.state.collectAsStateWithLifecycle()

    val decibelFlow = interviewViewModel.decibelFlow.collectAsStateWithLifecycle()

    val context = LocalContext.current

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
                interviewViewModel.updateDecibel(rmsdB.roundToInt())
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
                    interviewViewModel.appendAnswer(match)
                }
            }

            Log.d("TAG", "결과 호출: ")

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
        //TODO(대답 제출시 또는 Pause시 값 바꿔주어야함 )
    }


    LaunchedEffect(state.value.recognizerState) {
        if (state.value.recognizerState == InterviewViewModel.RecognizerState.Started) { //유저한테 요청 들어오면
            startSpeechRecognize()
        } else {
            stopSpeechRecognize()
        }
    }


    val questionCnt = (state.value.answers?.size ?: 0) - (state.value.currentPage ?: 0) - 1


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
                state.value.progress.progressToString(),
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
                "${(decibelFlow.value ?: 0f).toInt()}dB",
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
                    color = if (showPreview) Color(0x80000000) else Color(0x887A7A7A),
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
                    interviewViewModel.deleteAnswer()
                }
            )


            Icon(
                painter = painterResource(
                    if (state.value.recognizerState == InterviewViewModel.RecognizerState.Started) R.drawable.ic_pause_circle
                    else R.drawable.ic_interview_mic
                ),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.clickable {
                    if (state.value.recognizerState == InterviewViewModel.RecognizerState.Started) {
                        //음성인식 중
                        //음성인식 중단
                        interviewViewModel.stopRecordSTT()

                    } else { //음성인식 중 X
                        //음성인식 시작
                        when (state.value.interviewState) {
                            is InterviewViewModel.InterviewState.Error -> {
                                AlertUtils.showToast(context, "오류가 발생하였습니다")
                                interviewViewModel.stopRecordSTT()
                            }
                            InterviewViewModel.InterviewState.InProgress -> {
                                //음성인식 시작
                                interviewViewModel.startRecordSTT()

                            }
//                            InterviewViewModel.InterviewState.WritingMemo -> {
//                                AlertUtils.showToast(context, "인터뷰가 완료되었습니다")
//                                interviewViewModel.stopRecordSTT()
//                            }
                            else -> {
                                AlertUtils.showToast(context, "잠시 후 다시 시도해주세요")
                                interviewViewModel.stopRecordSTT()
                            }
                        }
                    }

                }
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.clickable {
                    interviewViewModel.stopRecordSTT()
                    sendClicked()
//                    interviewViewModel.moveToNextPage()
                }
            )
        }
    }

}

@Composable
private fun QuestionAnswerContents(

) {

    val spacing = LocalSpacing.current

    val interviewViewModel: InterviewViewModel = hiltViewModel()

    val state = interviewViewModel.state.collectAsStateWithLifecycle()


    val question = remember(state.value.currentPage) {
        derivedStateOf {
            with(state.value) {
                if (questionnaire?.questions != null && currentPage != null) {
                    questionnaire.questions[currentPage!!].question
                } else {
                    ""
                }
            }
        }
    }

    with(state.value) {

        if (interviewState == InterviewViewModel.InterviewState.InProgress) {


            if (questionnaire?.questions != null && currentPage != null && answers != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.medium)
                ) {

                    if (question.value.isNotBlank()) {
                        TypingAnimatedText(
                            modifier = Modifier
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 5.dp
                                ),
                            "Q. ${question.value}",
                            fontSize = 18.sp
                        )

                    }


                    Spacer(modifier = Modifier.height(spacing.small))


                    if (answers[currentPage!!].answer.isNotBlank()) {
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
                                answers[currentPage!!].answer,
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


        }
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

private fun String.splitToCodePoints(): List<String> {
    return codePoints()
        .toList()
        .map {
            String(Character.toChars(it))
        }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun BeforeSendingAnswerDialog(
    dismissClick: (String) -> Unit,
    question: String,
    answer: String
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/circle_rocket.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val spacing = LocalSpacing.current

    //재시작인지도 파악하기

    val modifiedAnswer = remember {
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
            fontFamily = nexonFont
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
                            color = White,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Spacer(modifier = Modifier.height(60.dp))

                    Text(
                        text = "답변 제출 전",
                        style = LocalTextStyle.current.copy(
                            color = Black,
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
                            color = Gray,
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
                                color = LightGray
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
                                color = Black,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(spacing.small))

                        TextField(
                            value = modifiedAnswer.value,
                            onValueChange = {
                                modifiedAnswer.value = it
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
                                textColor = Black,
                                disabledTextColor = Black,
                                backgroundColor = Transparent,
                                cursorColor = LightGray,
                                errorCursorColor = text_red,
                                focusedIndicatorColor = Transparent,
                                unfocusedIndicatorColor = Transparent
                            ),
                            textStyle = LocalTextStyle.current.copy(
                                color = Black,
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
                            modifier = Modifier.weight(1f),
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
                                    dismissClick(modifiedAnswer.value)
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun InterviewMemoDialog(
    dismissClick: () -> Unit,
    interviewUUID: String
) {

    //인터뷰가 종료된 후 메모를 남기는 다이얼로그

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/circle_rocket.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val spacing = LocalSpacing.current

    //재시작인지도 파악하기

    val context = LocalContext.current

    val interviewResultViewModel: InterviewResultViewModel = hiltViewModel()

    val memoFlow = interviewResultViewModel.writingMemoResultFlow.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            interviewResultViewModel.initMemoState()
        }
    }

    val memo = remember(memoFlow.value) {
        mutableStateOf((memoFlow.value as? Resource.Success)?.data ?: "")
    }

    //TODO (나중에 바꿔야함 메모 남기기 클릭시 이전에 썼던 메모 올라가있게해야함)
    memoFlow.value?.let {
        when (it) {
            is Resource.Error -> {
                dismissClick()
                AlertUtils.showToast(context, "다음에 다시 시도해주세요")
            }
            Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {
                dismissClick()
                memo.value = it.data
                AlertUtils.showToast(context, "메모를 저장했어요")
            }
        }
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


    val simpleDateFormat = remember {
        SimpleDateFormat("yyyy.MM.dd (E) hh:mm", Locale.getDefault())
    }
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
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
                            color = White,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Spacer(modifier = Modifier.height(60.dp))

                    Text(
                        text = "면접 일기",
                        style = LocalTextStyle.current.copy(
                            color = Black,
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
                        text = "이번 면접에 대한 생각을 남겨 기록해보세요 !",
                        style = LocalTextStyle.current.copy(
                            color = Gray,
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
                                color = LightGray
                            )
                            .background(
                                color = bg_grey
                            )
                            .verticalScroll(rememberScrollState())
                            .padding(spacing.medium)

                    ) {


                        Text(
                            simpleDateFormat.format(Date(System.currentTimeMillis())),
                            style = LocalTextStyle.current.copy(
                                color = Black,
                                fontSize = 12.sp,
                                fontFamily = nexonFont,
                                textAlign = TextAlign.End
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(spacing.small))

                        TextField(
                            value = memo.value,
                            onValueChange = {
                                memo.value = it
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            placeholder = {
                                Text(
                                    "이곳에 입력하세요",
                                    style = LocalTextStyle.current.copy(
                                        color = text_blue,
                                        fontSize = 14.sp,
                                        fontFamily = nexonFont
                                    )
                                )
                            },
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(100.dp, 250.dp)
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
                                }
                                .drawBehind {
                                    drawRoundRect(
                                        color = Color.Gray,
                                        style = Stroke(
                                            width = 1f,
                                            pathEffect = PathEffect.dashPathEffect(
                                                floatArrayOf(10f, 10f), 0f
                                            )
                                        )
                                    )
                                },
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Black,
                                disabledTextColor = Black,
                                backgroundColor = Transparent,
                                cursorColor = LightGray,
                                errorCursorColor = text_red,
                                focusedIndicatorColor = Transparent,
                                unfocusedIndicatorColor = Transparent
                            ),
                            textStyle = LocalTextStyle.current.copy(
                                color = Black,
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
                        horizontalArrangement = Arrangement.End
                    ) {

                        Text(
                            text = "작성완료",
                            modifier = Modifier
                                .clickable {
                                    memo.value.let {
                                        interviewResultViewModel.writeMemo(interviewUUID, it)

                                    }
                                },
                            style = LocalTextStyle.current.copy(
                                color = text_blue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.End
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


@Preview(showBackground = true)
@Composable
private fun DialogPreview() {
    BeforeSendingAnswerDialog(
        dismissClick = {

        },
        question = "프로세스와 스레드의 차이는 무엇인가요?",
        answer = ""
    )
}

@Composable
fun InterviewPreparedDialog(
    dismissClick: () -> Unit,
    backButtonClick: () -> Unit

) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/circle_rocket.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val spacing = LocalSpacing.current

    //재시작인지도 파악하기

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
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
                            color = White,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Spacer(modifier = Modifier.height(60.dp))

                    Text(
                        text = "AI 면접 안내",
                        style = LocalTextStyle.current.copy(
                            color = Black,
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
                        text = "면접은 아래와 같이 진행돼요 !",
                        style = LocalTextStyle.current.copy(
                            color = Gray,
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
                                color = LightGray
                            )
                            .background(
                                color = bg_grey
                            )
                            .verticalScroll(rememberScrollState())
                            .padding(spacing.medium)

                    ) {

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "1. 면접 질문",
                                style = LocalTextStyle.current.copy(
                                    color = Black,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                "면접 질문은 최대 4~5개로 구성돼요.\n자기소개서와 관심주제를 바탕으로 질문이 만들어져요.",
                                style = LocalTextStyle.current.copy(
                                    color = Black,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.medium))

                        }

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "2. 면접 답변",
                                style = LocalTextStyle.current.copy(
                                    color = Black,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                "음성인식을 통해 답변할 수 있어요.\n인터뷰에 집중하기 위해 한번에 쭉 답변하고\n다음 질문으로 넘어가기 전에\n답변에 오타가 있다면 수정해보세요.",
                                style = LocalTextStyle.current.copy(
                                    color = Black,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.medium))

                        }

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "3. 실시간 피드백",
                                style = LocalTextStyle.current.copy(
                                    color = Black,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                "웃는 모습, 목소리 크기, 자세 등 면접 도중\n피드백을 실시간으로 받아볼 수 있어요.",
                                style = LocalTextStyle.current.copy(
                                    color = Black,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.medium))

                        }

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "4. 면접 점수",
                                style = LocalTextStyle.current.copy(
                                    color = Black,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                "면접을 보고 나의 점수를 확인해보세요.\n점수는 마이페이지에 업데이트됩니다.",
                                style = LocalTextStyle.current.copy(
                                    color = Black,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Start
                                )
                            )


                        }

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
                                    backButtonClick()
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
                            text = "바로 시작",
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    dismissClick()
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