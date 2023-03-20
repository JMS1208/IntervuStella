package com.capstone.Capstone2Project.ui.screen.interview3

/*
//@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {


}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InterviewScreen3(
    navController: NavController,
    script: Script?
) {

    val interviewViewModel3: InterviewViewModel3 = hiltViewModel()

    val context = LocalContext.current

    LaunchedEffect(interviewViewModel3) {
        script?.let {
            interviewViewModel3.fetchCustomQuestionnaire(it)
        } ?: run {
            navController.popBackStack()
            AlertUtils.showToast(context, "유효하지 않은 자기소개서 입니다.")
        }
    }

    val questionnaire = interviewViewModel3.customQuestionnaireFlow.collectAsStateWithLifecycle()


    val lifecycleOwner = LocalLifecycleOwner.current


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

                faceDetector.process(inputImage)
                    .addOnSuccessListener { faces ->
                        if (readyToDetectFace) {
                            processFaceDetectionResult(
                                faces,
                                viewModel = interviewViewModel3
                            )
                            readyToDetectFace = false
                        }

                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
                    .addOnCompleteListener {
                        completeDetection()
                    }

                poseDetector.process(inputImage)
                    .addOnSuccessListener { pose ->
                        if (readyToDetectPose) {
                            processPoseDetectionResult(pose, viewModel = interviewViewModel3)
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


    val permissions = remember {
        listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    questionnaire.value?.let {
        when (it) {
            is Resource.Error -> {
                navController.popBackStack()
                AlertUtils.showToast(context, "${it.error} 오류가 발생하였습니다", Toast.LENGTH_LONG)
            }
            Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {
                if (permissionState.allPermissionsGranted) {
                    CompositionLocalProvider(
                        LocalInterviewManager provides interviewManager,
                        LocalTextStyle provides TextStyle(
                            fontFamily = CustomFont.nexonFont
                        )
                    ) {
                        VideoScreenContent(navController)
                    }
                } else {
                    RequestPermissions(permissionState)
                }


            }
            else -> {}
        }
    }

}


private fun processPoseDetectionResult(pose: Pose?, viewModel: InterviewViewModel3) {

    pose?.toLogLine()?.let {
        viewModel.loadInterviewLogLine(it)
    }

}

private fun processFaceDetectionResult(
    faces: List<Face>,
    viewModel: InterviewViewModel3
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
private fun VideoScreenContent(
    navController: NavController
) {

    val showCameraPreview = remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CameraPreview()

        if (!showCameraPreview.value) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Black
                    ),
                contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/face.json"))
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever,
                    speed = 0.5f
                )


                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .alpha(0.5f)
                )

            }
        }


        ComposableLifecycle { _, event ->

            when (event) {

                ON_CREATE -> {
                }
                ON_START -> {
                }
                ON_RESUME -> {
                }
                ON_PAUSE -> {
                }
                ON_STOP -> {
                }
                ON_DESTROY -> {
                }
                else -> Unit
            }
        }
        InterviewUIScreenContent(
            exitButtonClick = {
                navController.popBackStack()
            },
            showPreviewClick = {
                showCameraPreview.value = it
            }
        )
    }
}

@Composable
private fun CameraPreview() {
    val interviewManger = LocalInterviewManager.current

    AndroidView(
        factory = {
            interviewManger.showPreview()
        },
        modifier = Modifier.fillMaxSize(),
        update = {
            interviewManger.updatePreview(it)
        }
    )


}

@Composable
fun InterviewUIScreenContent(
    exitButtonClick: () -> Unit,
    showPreviewClick: (Boolean) -> Unit
) {

    val spacing = LocalSpacing.current

    val interviewViewModel3: InterviewViewModel3 = hiltViewModel()

    val newInterviewLogLine =
        interviewViewModel3.newInterviewLogLineFlow.collectAsStateWithLifecycle()

    val oldInterviewLogLine =
        interviewViewModel3.oldInterviewLogLineFlow.collectAsStateWithLifecycle()

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/mic.json"))

    val isPlaying = remember {
        mutableStateOf(false)
    }

    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying.value,
        iterations = 1
    )

    LaunchedEffect(lottieState.isPlaying) {
        isPlaying.value = lottieState.isPlaying
    }

    val showPreview = remember {
        mutableStateOf(true)
    }

    LaunchedEffect(showPreview.value) {
        showPreviewClick(showPreview.value)
    }


    val currentPage = interviewViewModel3.currentPageFlow.collectAsStateWithLifecycle()

    val customQuestionnaire =
        interviewViewModel3.customQuestionnaireFlow.collectAsStateWithLifecycle()

    val questions = remember {
        customQuestionnaire.value?.successOrNull()?.questions
    }

    val interviewState = interviewViewModel3.interviewState3Flow.collectAsStateWithLifecycle()

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    val context = LocalContext.current

    intent.apply {
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

    var speechText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(speechText) {
        interviewViewModel3.updateAnswer(speechText)
        Log.d("TAG", "스피치 텍스트 : $speechText")
    }

    var partialText by remember {
        mutableStateOf("")
    }

    val soundVolume = remember {
        mutableStateOf<Int?>(null)
    }

    val speechRecognizer: SpeechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val speechRecognizerState = remember {
        mutableStateOf(false)
    }

    val userSpeechRequest = remember {
        mutableStateOf(false)
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
                soundVolume.value = rmsdB.roundToInt()
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

            AlertUtils.showToast(context, "오류 $error")

        }

        override fun onResults(results: Bundle?) {
            //인식 결과가 준비되면 호출
            //말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            results ?: return
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            //val confidenceScore = results.getFloat(RecognizerIntent.EXTRA_CONFIDENCE_SCORES)
            matches?.let {
                it.forEach { match ->
                    speechText += " $match"
                }
            }

            Log.d("TAG", "결과 호출: ")

            speechRecognizerState.value = false

        }

        override fun onPartialResults(partialResults: Bundle?) {
            //부분 인식 결과를 사용할 수 있을 때 호출
            partialResults ?: return
            val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

            matches?.forEach { match ->
                partialText = match
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            //이벤트를 추가하기 위해 예약


        }


    }

    fun startSpeechRecognize() {
        if(!speechRecognizerState.value) { // 실행중 X
            speechRecognizer.apply {
                setRecognitionListener(listener)
                startListening(intent)
            }
            speechRecognizerState.value = true
        }
    }

    fun stopSpeechRecognize() {
        if(speechRecognizerState.value) { //실행중이면

            speechRecognizer.apply {
                stopListening()
                destroy()
            }

            speechRecognizerState.value = false
        }
    }
    LaunchedEffect(userSpeechRequest.value) {
        if(userSpeechRequest.value) { //유저한테 요청 들어오면
            startSpeechRecognize()
        } else {
            stopSpeechRecognize()
        }
    }

    LaunchedEffect(speechRecognizerState.value) {
        if(userSpeechRequest.value) {
            startSpeechRecognize()
        } else {
            stopSpeechRecognize()
        }
        //TODO(대답 제출시 또는 Pause시 값 바꿔주어야함 )
    }



    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {

        interviewState.value.let {
            when (it) {
                InterviewViewModel3.InterviewState3.Ended -> {
                    val interviewResult = remember {
                        interviewViewModel3.interviewResultFlow.value
                    }

                    interviewResult?.apply {
                        Dialog(
                            onDismissRequest = {},
                            properties = DialogProperties(usePlatformDefaultWidth = false)
                        ) {

                        }
                    }

                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Transparent
                            )
                            .padding(spacing.small)
                            .padding(top = 20.dp)
                    ) {

                        IconButton(
                            onClick = {
                                showPreview.value = !showPreview.value
                            },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = White,
                                        shape = RoundedCornerShape(10.dp)

                                    )
                                    .background(
                                        color = Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = spacing.medium, vertical = spacing.small)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = White
                                )

                                Spacer(modifier = Modifier.width(spacing.small))

                                Text(
                                    if (showPreview.value) "카메라 끄기" else "카메라 켜기",
                                    style = LocalTextStyle.current.copy(
                                        color = White,
                                        fontWeight = FontWeight(550),
                                        shadow = Shadow(
                                            color = DarkGray,
                                            offset = Offset(1f, 1f),
                                            blurRadius = 4f
                                        )
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                exitButtonClick()
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = White,
                                        shape = RoundedCornerShape(10.dp)

                                    )
                                    .background(
                                        color = Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = spacing.medium, vertical = spacing.small)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = White
                                )

                                Spacer(modifier = Modifier.width(spacing.small))

                                Text(
                                    "나가기",
                                    style = LocalTextStyle.current.copy(
                                        color = White,
                                        fontWeight = FontWeight(550),
                                        shadow = Shadow(
                                            color = DarkGray,
                                            offset = Offset(1f, 1f),
                                            blurRadius = 4f
                                        )
                                    )
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(
                                spacing.medium,
                                Alignment.Bottom
                            )
                        ) {

                            /*대답 제출*/

                            Column(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(spacing.small)
                                    .clickable {
                                        interviewViewModel3.moveNextPage()
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {


                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    modifier = Modifier.size(70.dp),
                                    tint = White
                                )


                                Text(
                                    "대답 제출",
                                    style = LocalTextStyle.current.copy(
                                        fontWeight = FontWeight(550),
                                        shadow = Shadow(
                                            color = DarkGray,
                                            offset = Offset(1f, 1f),
                                            blurRadius = 8f
                                        ),
                                        fontSize = 18.sp,
                                        color = White
                                    )
                                )
                            }



                            Spacer(modifier = Modifier.height(spacing.medium))

                            /*
                            로그 띄우기
                             */

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(
                                    spacing.medium,
                                    Alignment.CenterVertically
                                ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(
                                        spacing.small,
                                        Alignment.CenterVertically
                                    ),
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    oldInterviewLogLine.value?.let {
                                        OldLogContent(interviewLogLine = it)
                                    }
                                    newInterviewLogLine.value?.let {
                                        NewLogContent(interviewLogLine = it)
                                    }

                                }

                            }


                            /*
                            질문
                             */
                            questions?.let { questions ->
                                currentPage.value?.let {
                                    QuestionContent(
                                        question = questions[it].question
                                    )
                                }
                            }


                            /*
                            대답
                             */
                            if (speechText.isNotBlank()) {

                                AnswerContent(
                                    answer = speechText
                                )

                            }
                            /*
                            Text(
                                "${soundVolume.value ?: 0} dB",
                                style = LocalTextStyle.current.copy(
                                    fontWeight = FontWeight(550),
                                    shadow = Shadow(
                                        color = DarkGray,
                                        offset = Offset(1f, 1f),
                                        blurRadius = 8f
                                    ),
                                    fontSize = 18.sp,
                                    color = White
                                )
                            )

                             */

                            /*
                            마이크 버튼
                             */
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                LottieAnimation(
                                    modifier = Modifier.clickableWithoutRipple {
                                        isPlaying.value = true
                                        invokeVibration(context)
                                        playButtonSound(context)
                                        userSpeechRequest.value = !userSpeechRequest.value

                                    },
                                    composition = composition,
                                    progress = {
                                        lottieState.progress
                                    }

                                )
                            }
                        }

                    }
                }
            }
        }


    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun QuestionContent(
    modifier: Modifier = Modifier,
    question: String
) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 15.dp,
                        bottomEnd = 15.dp,
                        bottomStart = 15.dp
                    )
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            darker_blue,
                            bright_blue
                        )
                    ),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 15.dp,
                        bottomEnd = 15.dp,
                        bottomStart = 15.dp
                    )
                )
                .padding(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    "질문",
                    style = LocalTextStyle.current.copy(
                        color = bright_blue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    modifier = Modifier
                        .shadow(
                            3.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            color = White,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )


            }

            Spacer(modifier = Modifier.height(spacing.small))

            AnimatedContent(
                targetState = question,
                transitionSpec = {
                    fadeIn() + slideInVertically(animationSpec = tween(400),
                        initialOffsetY = { fullHeight -> fullHeight }) with
                            fadeOut(animationSpec = tween(200))
                }
            ) {
                Text(
                    question,
                    style = LocalTextStyle.current.copy(
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight(550),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }


            Spacer(modifier = Modifier.height(spacing.small))

        }
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnswerContent(
    modifier: Modifier = Modifier,
    answer: String
) {
    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small)
                .shadow(
                    3.dp,
                    RoundedCornerShape(
                        topStart = 15.dp,
                        topEnd = 0.dp,
                        bottomEnd = 15.dp,
                        bottomStart = 15.dp
                    )
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(
                        topStart = 15.dp,
                        topEnd = 0.dp,
                        bottomEnd = 15.dp,
                        bottomStart = 15.dp
                    )
                )
                .border(
                    1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            bright_blue,
                            darker_blue
                        )
                    ),
                    shape = RoundedCornerShape(
                        topStart = 15.dp,
                        topEnd = 0.dp,
                        bottomEnd = 15.dp,
                        bottomStart = 15.dp
                    )
                )
                .padding(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "음성인식",
                    style = LocalTextStyle.current.copy(
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        shadow = Shadow(
                            color = DarkGray,
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    ),
                    modifier = Modifier
                        .shadow(
                            3.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    bright_blue,
                                    darker_blue
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(spacing.small))

            AnimatedContent(
                targetState = answer,
                transitionSpec = {
                    fadeIn() + slideInVertically(animationSpec = tween(400),
                        initialOffsetY = { fullHeight -> fullHeight }) with
                            fadeOut(animationSpec = tween(200))
                }
            ) {
                Text(
                    answer,
                    style = LocalTextStyle.current.copy(
                        color = DarkGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight(550),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(spacing.small))

        }
    }
}
*/
