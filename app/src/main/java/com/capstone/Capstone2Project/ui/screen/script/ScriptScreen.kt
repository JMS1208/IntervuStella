package com.capstone.Capstone2Project.ui.screen.script

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.ScriptItem
import com.capstone.Capstone2Project.data.resource.DataState
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.navigation.ROUTE_INTERVIEW_GUIDE
import com.capstone.Capstone2Project.ui.screen.error.ErrorScreen
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.HighlightText
import com.capstone.Capstone2Project.utils.composable.PocketBookShape
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.extensions.drawVerticalScrollbar
import com.capstone.Capstone2Project.utils.theme.LocalSpacing
import com.capstone.Capstone2Project.utils.theme.bg_grey
import com.capstone.Capstone2Project.utils.theme.bg_mono_grey
import com.capstone.Capstone2Project.utils.theme.bright_blue
import com.capstone.Capstone2Project.utils.theme.highlight_blue
import com.capstone.Capstone2Project.utils.theme.text_blue
import com.capstone.Capstone2Project.utils.theme.text_red
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptScreen(
    navController: NavController,
    oriScript: Script? = null,
    firebaseUser: FirebaseUser
) {
    val viewModel: ScriptViewModel = hiltViewModel()

    val state = viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.setScriptAndFetchBaseData(
            oriScript //?: Script.makeTestScript()
        )
    }

    when (state.value.dataState) {
        is DataState.Error -> {
            ErrorScreen()
        }

        is DataState.Loading -> {
            LoadingScreen()
        }

        DataState.Normal -> {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.White
                        ),
                        title = {
                            Text(
                                "자기소개서 작성",
                                style = LocalTextStyle.current.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        navigationIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "뒤로가기",
                                tint = Color.DarkGray,
                                modifier = Modifier.clickable {
                                    navController.popBackStack()
                                }
                            )
                        }
                    )

                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = bg_grey
                        )
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {

                    if (state.value.curPage == 0) {
                        ScriptSettingContent(
                            state.value.scriptItemList,
                            state.value.jobRoleList,
                            state.value.title,
                            viewModel
                        )

                        when (state.value.dialogState) {
                            is ScriptViewModel.DialogState.CheckRemoveDialog -> {
                                CheckRemoveDialog(
                                    scriptItem = (state.value.dialogState as ScriptViewModel.DialogState.CheckRemoveDialog).scriptItem,
                                    onDismiss = viewModel::closeDialog,
                                    removeClicked = {
                                        viewModel.removeScriptItem(it)
                                    }
                                )
                            }

                            ScriptViewModel.DialogState.Nothing -> Unit
                        }
                    } else if (state.value.curPage == state.value.scriptItemList.count { it.second } + 1) {
                        ScriptLastContent(
                            moveToInterviewClicked = {
                                /*
                                자기소개서 서버에 만들어졌으니, 이제 면접 질문지 생성 요청
                                 */
                                viewModel.startInterview(firebaseUser.uid, false)
                            },
                            moveToHomeClicked = {
                                navController.navigate(ROUTE_HOME) {
                                    popUpTo(ROUTE_HOME) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    } else {

                        val scriptIdx = state.value.curPage - 1
                        val scriptItem = state.value.scriptItemList.filter { it.second }
                            .sortedBy { it.first.index }[scriptIdx]

                        ScriptWritingContent(
                            scriptItem = scriptItem.first,
                            curPage = state.value.curPage,
                            moveNextPage = {
                                viewModel.moveNextPage(firebaseUser.uid)
                            },
                            movePrevPage = viewModel::movePrevPage,
                            answerChanged = {
                                viewModel.updateScriptItemAnswer(scriptItem.first, it)
                            }
                        )


                    }


                }


                LaunchedEffect(viewModel) {
                    viewModel.effect.collect {
                        when (it) {
                            is ScriptViewModel.Effect.ShowMessage -> {
                                val message = it.message
                                AlertUtils.showToast(context, message)
                            }

                            is ScriptViewModel.Effect.NavigateTo -> {
                                val questionnaire = it.questionnaire

                                navController.navigate(
                                    "$ROUTE_INTERVIEW_GUIDE/{questionnaire}".replace(
                                        oldValue = "{questionnaire}",
                                        newValue = questionnaire.toJsonString()
                                    )
                                ) {
                                    popUpTo(ROUTE_HOME) {
                                        inclusive = true
                                    }
                                }



                            }
                        }
                    }

                }
            }


        }
    }


}

@Composable
fun ScriptLastContent(
    moveToInterviewClicked: () -> Unit,
    moveToHomeClicked: () -> Unit
) {

    val sparkleComposition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/sparkle.json"))
    val sparkleProgress by animateLottieCompositionAsState(
        sparkleComposition,
        iterations = LottieConstants.IterateForever
    )

    val flagComposition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/flags.json"))
    val flagProgress by animateLottieCompositionAsState(
        flagComposition,
        iterations = LottieConstants.IterateForever
    )

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    val screenHeight = configuration.screenHeightDp.dp

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        androidx.compose.material.LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = bg_grey
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(min(screenWidth, screenHeight).div(2)),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = flagComposition,
                    progress = { sparkleProgress }
                )
                LottieAnimation(
                    composition = sparkleComposition,
                    progress = { sparkleProgress }
                )
            }

            Text(
                "자기소개서 작성 완료 !",
                color = Color.DarkGray,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                "작성하신 자기소개서를 바탕으로\nAI 모의 면접 질문이 구성됩니다.",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(spacing.large))

            IconButton(
                onClick = moveToInterviewClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.large)
                    .height(50.dp)
                    .border(
                        width = 1.dp,
                        color = bright_blue,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(
                        color = White,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material.Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = bright_blue
                    )

                    Spacer(modifier = Modifier.width(spacing.small))

                    Text(
                        "바로 AI 모의면접 보기",
                        color = bright_blue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            IconButton(
                onClick = moveToHomeClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.large)
                    .height(50.dp)
                    .shadow(
                        3.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(
                        color = bright_blue,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material.Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = White
                    )

                    Spacer(modifier = Modifier.width(spacing.small))

                    Text(
                        "홈 화면으로 이동하기",
                        style = androidx.compose.material.LocalTextStyle.current.copy(
                            color = White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            shadow = Shadow(
                                DarkGray,
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


@OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class
)
@Composable
private fun ScriptWritingContent(
    scriptItem: ScriptItem,
//    pagerState: PagerState,
    curPage: Int,
    moveNextPage: () -> Unit,
    movePrevPage: () -> Unit,
    answerChanged: (String) -> Unit
) {

    val spacing = LocalSpacing.current

    val tipPage = remember {
        mutableStateOf(0)
    }

    val focusManager = LocalFocusManager.current

    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val coroutineScope = rememberCoroutineScope()

    var textFieldFocused by remember {
        mutableStateOf(false)
    }

    var targetOffsetValue by remember {
        mutableStateOf(false)
    }

    val offsetState by animateDpAsState(
        targetValue = if (targetOffsetValue) 2.dp else (-2).dp,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(true) {
        while (true) {
            targetOffsetValue = !targetOffsetValue
            delay(800)
        }
    }

    LaunchedEffect(scriptItem) {

        tipPage.value = 0

        while (true) {
            delay(5000)
            if (tipPage.value < scriptItem.tips.size - 1) {
                tipPage.value += 1
            } else {
                tipPage.value = 0
            }
        }
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.medium)
                    .shadow(10.dp, shape = PocketBookShape(10.dp, circleWidth = 20.dp))
                    .background(
                        color = bg_mono_grey,
                        shape = PocketBookShape(10.dp, circleWidth = 20.dp)
                    )
                    .padding(top = spacing.large, bottom = spacing.medium)
                    .clickableWithoutRipple {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
            ) {

                Spacer(modifier = Modifier.height(spacing.large))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium),
                    color = LightGray,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(spacing.extraSmall))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium),
                    color = LightGray,
                    thickness = 1.dp
                )


                Spacer(modifier = Modifier.height(spacing.small))

                Text(
                    "$curPage. ${scriptItem.question} (${scriptItem.maxLength}자)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium, vertical = spacing.small),
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(550),
                    color = text_blue
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium)
//                    .shadow(5.dp, shape = RoundedCornerShape(5.dp))
                        .background(color = White, shape = RoundedCornerShape(5.dp))
                        .padding(vertical = spacing.medium, horizontal = spacing.small),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.small),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small, Alignment.Start)
                    ) {
                        androidx.compose.material.Icon(
                            painter = painterResource(id = R.drawable.ic_tip),
                            contentDescription = null,
                            tint = DarkGray
                        )
                        Text(
                            "작성 Tip",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGray
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.extraSmall))

                    AnimatedContent(
                        targetState = scriptItem.tips[tipPage.value],
                        transitionSpec = {
                            fadeIn(
                                animationSpec = tween(220, 90),
                                initialAlpha = 0f
                            ) + slideInVertically {
                                it * 2
                            } with fadeOut(
                                animationSpec = tween(220, 90),
                                targetAlpha = 0f
                            ) + slideOutVertically {
                                -it * 2
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            HighlightText(
                                text = "\" $it \"",
                                fontWeight = FontWeight.Medium,
                                offset = 8.dp,
                                fontSize = 15.sp,
                                color = Black
                            )
                        }

                    }

                }

                Spacer(modifier = Modifier.height(spacing.medium))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = spacing.medium)
//                    .shadow(5.dp, shape = RoundedCornerShape(5.dp))
                        .background(color = White, shape = RoundedCornerShape(5.dp))
                        .padding(vertical = spacing.medium, horizontal = spacing.small),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        value = scriptItem.answer,
                        onValueChange = {
                            answerChanged(it)
                        },
                        trailingIcon = null,
                        textStyle = androidx.compose.material.LocalTextStyle.current.copy(
                            fontSize = 14.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(bringIntoViewRequester)
                            .onFocusEvent { focusState ->
                                if (focusState.isFocused) {
                                    coroutineScope.launch {
                                        bringIntoViewRequester.bringIntoView()
                                    }
                                }
                            }
                            .onFocusChanged { focusState ->
                                textFieldFocused = focusState.isFocused
                            },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            textColor = Black,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = bright_blue
                        ),
                        placeholder = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                androidx.compose.material.Icon(
                                    painter = painterResource(id = R.drawable.ic_pencil),
                                    contentDescription = null,
                                    tint = Color.LightGray
                                )

                                Spacer(modifier = Modifier.width(spacing.small))

                                Text(
                                    "작성하기",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.LightGray
                                )

                            }
                        }
                    )

                    Text(
                        "(${scriptItem.answer.length}/${scriptItem.maxLength})",
                        color = text_red,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(spacing.medium))

            }

            /*
                페이지 넘어가는 버튼
                 */
            Box(
                Modifier.align(Alignment.CenterStart)
            ) {
                AnimatedVisibility(
                    visible = !textFieldFocused
                ) {

                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null,
                        tint = DarkGray,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(50.dp)
                            .offset(y = offsetState)
                            .clickable {
                                movePrevPage()
                            }
                    )

                }
            }

            Box(
                Modifier.align(Alignment.CenterEnd)
            ) {
                AnimatedVisibility(visible = !textFieldFocused) {

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = DarkGray,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(50.dp)
                            .offset(y = offsetState)
                            .clickable {
                                moveNextPage()
                            }
                    )

                }
            }


        }

    }


}

@Preview(showBackground = true)
@Composable
private fun DialogPreview() {

    CheckRemoveDialog(
        scriptItem = ScriptItem.createTestScriptItem(),
        onDismiss = { /*TODO*/ },
        removeClicked = {})

}

@Composable
private fun CheckRemoveDialog(
    scriptItem: ScriptItem,
    onDismiss: () -> Unit,
    removeClicked: (ScriptItem) -> Unit
) {

    val spacing = LocalSpacing.current


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = Black
        )
    ) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.medium)
                    .background(
                        color = White,
                        shape = RoundedCornerShape(spacing.medium)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.large, horizontal = spacing.extraMedium),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(spacing.medium))

                    Text(
                        text = "해당 질문은 입력한 내용이 있어요",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(spacing.extraSmall))
                    Text(
                        text = "삭제를 누르면 기존에 입력한 내용이 삭제돼요:)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Gray,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(spacing.extraMedium))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "질문", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Text(
                            text = "${scriptItem.question} (${scriptItem.answer.length}/${scriptItem.maxLength}자)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(spacing.large))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "내용", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Text(
                            text = scriptItem.answer,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.large))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onDismiss()
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = null,
                                tint = text_blue
                            )
                            Spacer(modifier = Modifier.width(spacing.small))
                            Text(
                                text = "취소",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = text_blue
                            )
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    removeClicked(scriptItem)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "삭제",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = text_blue
                            )
                        }
                    }

                }

            }

        }
    }


}


@OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterialApi::class, ExperimentalLayoutApi::class
)
@Composable
private fun ScriptSettingContent(
    scriptItemList: List<Pair<ScriptItem, Boolean>>,
    jobRoleList: List<Pair<String, Boolean>>,
    title: String,
    viewModel: ScriptViewModel,
) {

    val spacing = LocalSpacing.current

    val lazyListState = rememberLazyListState()

    val scrollState = rememberScrollState()

    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current

    val itemShape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = spacing.medium,
        bottomStart = spacing.medium,
        bottomEnd = spacing.medium
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickableWithoutRipple {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .drawVerticalScrollbar(lazyListState),
            state = lazyListState,
            contentPadding = PaddingValues(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.extraMedium, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /*
            자소서 제목 입력
             */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 1.dp,
                            shape = itemShape
                        )
                        .background(
                            color = Color.White,
                            shape = itemShape
                        )
                        .padding(
                            vertical = spacing.extraMedium,
                            horizontal = spacing.small
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    HighlightText(
                        text = "1. 자기소개서 제목",
                        color = Color.Black,
                        highlightColor = highlight_blue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier,
                        textAlign = TextAlign.Start,
                        fontSize = 18.sp

                    )
                    Spacer(modifier = Modifier.height(spacing.small))
                    Text(
                        text = "나를 더 돋보일 수 있는\n자기소개서 제목을 입력해주세요 :)",
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.DarkGray,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(spacing.medium))

                    BasicTextField(
                        value = title,
                        onValueChange = {
                            viewModel.updateScriptTitle(it)
                        },
                        textStyle = androidx.compose.material.LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .bringIntoViewRequester(bringIntoViewRequester)
                            .onFocusEvent { focusState ->
                                if (focusState.isFocused) {
                                    coroutineScope.launch {
                                        bringIntoViewRequester.bringIntoView()
                                    }
                                }
                            },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        )
                    ) {
                        TextFieldDefaults.TextFieldDecorationBox(
                            value = title,
                            innerTextField = it,
                            enabled = true,
                            singleLine = false,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = MutableInteractionSource(),
                            contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                                top = spacing.medium,
                                bottom = spacing.medium
                            )
                        )
                    }

                }
            }

            /*
            질문 선택
             */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 1.dp,
                            shape = itemShape
                        )
                        .background(
                            color = Color.White,
                            shape = itemShape
                        )
                        .padding(
                            vertical = spacing.extraMedium,
                            horizontal = spacing.medium
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        HighlightText(
                            text = "2. 질문 선택",
                            color = Color.Black,
                            highlightColor = highlight_blue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Text(
                            "(${scriptItemList.filter { it.second }.size}/5)",
                            color = Color.DarkGray
                        )
                    }
                    Spacer(modifier = Modifier.height(spacing.small))
                    Text(
                        text = "선택한 질문으로 자기소개서가 구성돼요 :)",
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(spacing.medium))


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = bg_mono_grey,
                                shape = RoundedCornerShape(spacing.medium)
                            )
                            .padding(
                                vertical = spacing.medium
                            )
                            .heightIn(100.dp, 300.dp)
                            .drawVerticalScrollbar(scrollState)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(
                            spacing.small,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        scriptItemList.forEach { scriptItem ->
                            Text(
                                text = "\"${scriptItem.first.question} (${scriptItem.first.maxLength}자)\"",
                                color = if (scriptItem.second) text_blue else Color.Black,
                                fontWeight = if (scriptItem.second) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.selectScriptItem(scriptItem.first)
                                    }
                                    .padding(
                                        vertical = spacing.small
                                    ),
                                fontSize = 16.sp
                            )

                        }

                    }

                    Spacer(modifier = Modifier.height(spacing.small))

                    AnimatedVisibility(visible = scriptItemList.filter { it.second }.size < 2) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.End),
                            text = "*최소 2개는 선택해주셔야 해요",
                            color = text_red,
                            fontSize = 14.sp,
                            textAlign = TextAlign.End
                        )
                    }

                }
            }

            /*
            직무 선택 부분
            */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 1.dp,
                            shape = itemShape
                        )
                        .background(
                            color = White,
                            shape = itemShape
                        )
                        .padding(
                            vertical = spacing.extraMedium,
                            horizontal = spacing.small
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        HighlightText(
                            text = "3. 직무 선택",
                            color = Color.Black,
                            highlightColor = highlight_blue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(spacing.small))
                    Text(
                        text = "지원하려는 기업의 직무를 선택해주세요",
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(spacing.medium))
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = bg_mono_grey,
                                shape = RoundedCornerShape(spacing.medium)
                            )
                            .padding(
                                vertical = spacing.medium,
                                horizontal = spacing.small
                            ),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        jobRoleList.forEach { jobRole ->
                            Text(
                                text = jobRole.first,
                                color = if (jobRole.second) text_blue else Color.Black,
                                fontWeight = if (jobRole.second) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.updateJobRole(jobRole.first)
                                    }
                                    .padding(
                                        vertical = spacing.extraSmall,
                                        horizontal = spacing.small
                                    ),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }


            /*
            다음 페이지 넘어가는 버튼
            */
            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
//                            .padding(horizontal = spacing.medium)
                        .height(50.dp)
                        .clickable {
                            viewModel.makeScript()
                        }
                        .shadow(
                            2.dp,
                            shape = RoundedCornerShape(spacing.medium)
                        )
                        .background(
                            color = bright_blue,
                            shape = RoundedCornerShape(spacing.medium)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material.Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = White
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        "이대로 자기소개서 만들기",
                        fontWeight = FontWeight(550),
                        color = White,
                        fontSize = 16.sp
                    )
                }

            }


        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = spacing.medium)
        ) {

            ScrollToBottomButton(
                lazyListState,
                3,
                modifier = Modifier.size(40.dp)
            )
        }
    }


}


@Composable
private fun ScrollToBottomButton(
    scrollState: LazyListState,
    lastIndex: Int,
    modifier: Modifier
) {

    val isVisible by remember {
        derivedStateOf { scrollState.canScrollForward }
    }

    val coroutineScope = rememberCoroutineScope()

    AnimatedVisibility(isVisible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                modifier = modifier,
                onClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(lastIndex)
                    }
                },
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 6.dp,
                    hoveredElevation = 4.dp,
                    focusedElevation = 4.dp
                ),
                shape = CircleShape,
                backgroundColor = bright_blue
            ) {
                androidx.compose.material.Icon(
                    contentDescription = null,
                    imageVector = Icons.Default.ArrowDownward,
                    tint = White
                )
            }
        }

    }

}