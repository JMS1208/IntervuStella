package com.capstone.Capstone2Project.ui.screen.home

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.inapp.TodayQuestionMemo
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.data.resource.successOrNull
import com.capstone.Capstone2Project.data.resource.throwableOrNull
import com.capstone.Capstone2Project.navigation.ROUTE_OTHERS_ANSWERS
import com.capstone.Capstone2Project.ui.screen.auth.AuthViewModel
import com.capstone.Capstone2Project.ui.screen.error.ErrorScreen
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.DottedShape
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.theme.*
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Preview(showBackground = true)
@Composable
fun TodayQuestionPreview() {
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            color = Color.White,
            shadow = Shadow(
                color = Color.Black,
                offset = Offset(1f, 1f),
                blurRadius = 4f
            )
        )
    ) {
        TodayQuestionCard(
            question = "프로세스와 스레드의 차이는?",
            questionUUID = UUID.randomUUID().toString(),
            navController = rememberNavController(),
            isPresentToday = false
        )
    }

}

@Composable
fun TodayQuestionCard(
    question: String,
    questionUUID: String,
    modifier: Modifier = Modifier,
    navController: NavController,
    isPresentToday: Boolean
) {

    val authViewModel: AuthViewModel = hiltViewModel()

    val viewModel: HomeViewModel = hiltViewModel()

    val context = LocalContext.current

    val blurRadius = 3f

    val blurOverlayColor = R.color.blurOverlayColor

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            color = Color.White,
            fontFamily = nexonFont
        )
    ) {
        ConstraintLayout(
            modifier = modifier
                .height(150.dp)
                .clickableWithoutRipple {
//                    showVeil = !showVeil

                }

        ) {


            AndroidView(
                factory = {
                    View.inflate(it, R.layout.todays_question, null)
                },
                modifier = modifier,
                update = {
                    val blurView = it.findViewById<BlurView>(R.id.blur_view)

                    val root = it.findViewById<ViewGroup>(R.id.root)

                    val decorView = getActivityDecorView(context)

                    val windowBackground = decorView?.background

                    blurView.setupWith(root, getBlurAlgorithm(context)) // or RenderEffectBlur
                        .setFrameClearDrawable(windowBackground)
                        .setBlurRadius(blurRadius)
                        .setOverlayColor(
                            ContextCompat.getColor(context, blurOverlayColor)
                        )

                    blurView.apply {
                        outlineProvider = ViewOutlineProvider.BACKGROUND
                        clipToOutline = true
                        visibility = if (!isPresentToday) View.VISIBLE else View.GONE
                    }

                    val innerComposeView = it.findViewById<ComposeView>(R.id.inner_compose_view)

                    innerComposeView.apply {
                        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                        setContent {

                            AnimatedVisibility(
                                !isPresentToday
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clickable {
                                            if (!isPresentToday) {
                                                authViewModel.currentUser?.uid?.let { uid ->
                                                    viewModel.checkAttendance(uid)
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "출석 체크하고\n오늘의 질문 확인하기 !",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        style = LocalTextStyle.current.copy(
                                            shadow = Shadow(
                                                color = Color.DarkGray,
                                                offset = Offset(1f, 1f),
                                                blurRadius = 8f
                                            )
                                        )
                                    )
                                }
                            }

                        }
                    }

                    val backComposeView = it.findViewById<ComposeView>(R.id.back_compose_view)

                    backComposeView.apply {
                        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                        setContent {
                            ContentOfTodayQuestion(
                                question = question,
                                questionUUID = questionUUID,
                                modifier = Modifier,
                                navController = navController,
                                canClickContent = isPresentToday
                            )
                        }
                    }


                }
            )


        }
    }

}


private fun getBlurAlgorithm(context: Context): BlurAlgorithm {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        RenderEffectBlur()
    } else {
        RenderScriptBlur(context)
    }
}

private fun getActivityDecorView(context: Context): View? {
    var ctx = context
    var i = 0
    while (i < 4 && ctx !is Activity && ctx is ContextWrapper) {
        ctx = ctx.baseContext
        i++
    }
    return if (ctx is Activity) {
        ctx.window.decorView
    } else {
        null
    }
}


@Composable
fun ContentOfTodayQuestion(
    question: String,
    questionUUID: String,
    modifier: Modifier = Modifier,
    elevation: Dp = 3.dp,
    cornerRadius: Dp = 10.dp,
    navController: NavController,
//    backgroundColor: Color = bright_blue
    canClickContent: Boolean
) {

    val spacing = MaterialTheme.spacing

    val homeViewModel: HomeViewModel = hiltViewModel()

    val authViewModel: AuthViewModel = hiltViewModel()

    val memoViewModel: MemoViewModel = hiltViewModel()

    val memoFlow = memoViewModel.memoFlow.collectAsStateWithLifecycle()

    val context = LocalContext.current

    memoFlow.value.let {
        if (it.showDialog) {
            when (it.memo) {
                is Resource.Error -> {
                    (it.memo as Resource.Error<TodayQuestionMemo>).error?.message?.let { errorMsg ->
                        AlertUtils.showToast(context, errorMsg)
                    }
                }

                Resource.Loading -> {
                    LoadingScreen()
                }

                is Resource.Success -> {
                    authViewModel.currentUser?.uid?.let { hostUUID ->
                        TodayQuestionMemoDialog(
                            (it.memo as Resource.Success<TodayQuestionMemo>).data,
                            saveButtonClicked = { memoToSave ->
                                memoViewModel.saveMemo(
                                    hostUUID,
                                    (it.memo as Resource.Success<TodayQuestionMemo>).data.questionUUID,
                                    memoToSave
                                )
                            },
                            dismissClicked = memoViewModel::closeMemoDialog
                        )
                    }
                }

                null -> Unit
            }

        }
    }

    Box(
        modifier = modifier
            .shadow(elevation, shape = RoundedCornerShape(cornerRadius))
            .background(
                color = bright_blue,
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(canClickContent) {
                navController.navigate(
                    "$ROUTE_OTHERS_ANSWERS/{question_uuid}".replace(
                        oldValue = "{question_uuid}",
                        newValue = questionUUID
                    )
                )
            }
//            .shimmerEffect(2000)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(spacing.small)
                .fillMaxSize()

        ) {
            Text(
                "오늘의 질문 !?",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                question,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )


            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(
                            color = White,
                            shape = DottedShape(5.dp)
                        )
                )

                Spacer(modifier = Modifier.height(spacing.small))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(canClickContent) {
                                //메모 다는 다이얼로그 띄우기
                                authViewModel.currentUser?.uid?.let { hostUUID ->
                                    memoViewModel.loadMemo(hostUUID, questionUUID, question)
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {


                        Icon(
                            painter = painterResource(id = R.drawable.ic_pencil),
                            contentDescription = null,
                            tint = LocalTextStyle.current.color
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            "메모하기",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(canClickContent) {
                                authViewModel.currentUser?.uid?.let {
                                    homeViewModel.getTodayQuestionAttendance(
                                        it,
                                        questionUUID
                                    )
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = LocalTextStyle.current.color
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            "다른 주제 볼래요",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                    }
                }
            }


        }
    }

}

@Preview(showBackground = true)
@Composable
private fun TodayQuestionMemoDialogPreview() {
    //

}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun TodayQuestionMemoDialog(
//    hostUUID: String,
    todayQuestionMemo: TodayQuestionMemo,
    saveButtonClicked: (String) -> Unit,
    dismissClicked: () -> Unit
) {

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/circle_rocket.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val spacing = LocalSpacing.current

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
        SimpleDateFormat("yyyy.MM.dd (E)", Locale.getDefault())
    }

    val textMemoState = remember {
        mutableStateOf(todayQuestionMemo.memo)
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {

        Dialog(
            onDismissRequest = {
                dismissClicked()
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
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
                        text = "오늘의 질문 메모",
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
                        text = "나만의 생각을 정리해보세요 !",
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
                                color = Color.LightGray
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
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontFamily = nexonFont,
                                textAlign = TextAlign.End
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(spacing.small))

                        TextField(
                            value = textMemoState.value,
                            onValueChange = {
                                textMemoState.value = it
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
                        horizontalArrangement = Arrangement.End
                    ) {

                        Text(
                            text = "작성완료",
                            modifier = Modifier
                                .clickable {
                                    saveButtonClicked(textMemoState.value)
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