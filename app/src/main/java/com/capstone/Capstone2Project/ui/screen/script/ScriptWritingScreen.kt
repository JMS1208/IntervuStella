package com.capstone.Capstone2Project.ui.screen.script

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.utils.composable.HighlightText
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.TextField
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.ScriptItem
import com.capstone.Capstone2Project.data.resource.DataState
import com.capstone.Capstone2Project.data.resource.Selected
import com.capstone.Capstone2Project.navigation.ROUTE_SCRIPT_WRITING
import com.capstone.Capstone2Project.navigation.ROUTE_SCRIPT_WRITING_FINISH
import com.capstone.Capstone2Project.ui.screen.error.ErrorScreen
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.PocketBookShape
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.google.accompanist.pager.PagerState
import com.google.firebase.auth.FirebaseUser
import java.util.*

@Preview(showBackground = true)
@Composable
private fun Preview() {

//    val script = remember {
//        val tips = listOf("팁 예시1111111111", "팁 예시22222222222", "팁 예시3333333333")
//
//        val scriptUUID = UUID.randomUUID().toString()
//
//        val items = listOf(
//            ScriptItem(question = "예시 질문", tips = tips, maxLength = 300, scriptUUID = scriptUUID),
//            ScriptItem(question = "예시 질문", tips = tips, maxLength = 500, scriptUUID = scriptUUID),
//            ScriptItem(question = "예시 질문", tips = tips, maxLength = 300, scriptUUID = scriptUUID),
//            ScriptItem(question = "예시 질문", tips = tips, maxLength = 500, scriptUUID = scriptUUID),
//        )
//
//        val hostUUID = UUID.randomUUID().toString()
//
//        Script(
//            uuid = scriptUUID,
////            host = hostUUID,
//            date = System.currentTimeMillis(),
//            title = "",
//            questionnaireState = false,
//            scriptItems = items
//        )
//    }
//
//    ScriptPaperContent(
//        navController = rememberNavController(),
//        script = script
//    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptWritingScreen(
    navController: NavController,
    oriScript: Script? = null,
    firebaseUser: FirebaseUser
) {

    val viewModel: ScriptWritingViewModel = hiltViewModel()

    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.setInitialScript(oriScript)
    }



    when (state.value.dataState) {
        is DataState.Error -> {
            ErrorScreen(
                message = (state.value.dataState as DataState.Error).message
            )
        }

        DataState.Loading -> {
            LoadingScreen()
        }

        DataState.Normal -> {

            ScriptPaperContent(
                navController = navController,
                title = state.value.title,
                scriptItemList = state.value.scriptItemList,
                jobRoleList = state.value.jobRoleList,
                firebaseUser = firebaseUser,
                canMakeScript = state.value.canMakeScript,
                scriptTitleChanged = {
                    viewModel.updateScriptTitle(it)
                },
                scriptItemClicked = {
                    viewModel.selectScriptItem(it)
                },
                jobRoleClicked = {
                    viewModel.updateJobRole(it)
                },
                nextButtonClicked = {

                },
            )

//            Text(
//                text = "${state.value.title}, ${state.value.scriptItemList}, ${state.value.jobRoleList}",
//                modifier = Modifier.clickable {
//                    viewModel.selectScriptItem(state.value.scriptItemList.first().first)
//                },
//                color = White
//            )

        }
    }


}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
private fun ScriptPaperContent(
    navController: NavController,
    scriptItemList: List<Pair<ScriptItem, Boolean>>,
    jobRoleList: List<Pair<String, Boolean>>,
    title: String,
    canMakeScript: Boolean,
    firebaseUser: FirebaseUser,
    scriptTitleChanged: (String) -> Unit,
    scriptItemClicked: (ScriptItem) -> Unit,
    jobRoleClicked: (String) -> Unit,
    nextButtonClicked: () -> Unit,
) {
    val spacing = LocalSpacing.current

    val pagerState = rememberPagerState()

    val progress = animateFloatAsState(
        targetValue = (pagerState.currentPage.toFloat() / scriptItemList.filter{it.second}.size),
        animationSpec = tween(delayMillis = 50, durationMillis = 1000)
    )

    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = Black
        )
    ) {

        ScriptSettingsContent(
            title = title,
            scriptItemList = scriptItemList,
            jobRoleList = jobRoleList,
            pagerState = pagerState,
            scriptTitleChanged = { title ->
//                        viewModel.updateScriptTitle(title)
                scriptTitleChanged(title)
            },
            scriptItemClicked = { scriptItem ->
//                        viewModel.selectScriptItem(scriptItem)
                scriptItemClicked(scriptItem)
            },
            jobRoleClicked = { jobRole ->
                jobRoleClicked(jobRole)
            },
            nextButtonClicked = {
                nextButtonClicked()
            },
            canMakeScript = canMakeScript
        )

//        Scaffold(
//            modifier = Modifier
//                .fillMaxSize(),
//            topBar = {
//                CenterAlignedTopAppBar(
//                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                        containerColor = Color.White
//                    ),
//                    title = {
//                        Text(
//                            "자기소개서 작성",
//                            style = LocalTextStyle.current.copy(
//                                fontWeight = FontWeight.SemiBold,
//                                fontSize = 16.sp
//                            )
//                        )
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    navigationIcon = {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "뒤로가기",
//                            tint = Color.DarkGray,
//                            modifier = Modifier.clickable {
//                                navController.popBackStack()
//                            }
//                        )
//                    }
//                )
//            },
//            bottomBar = {
//                if (pagerState.currentPage != 0) {
//                    BottomAppBar(
//                        modifier = Modifier.fillMaxWidth(),
//                        backgroundColor = Transparent,
//                        contentPadding = PaddingValues(0.dp)
//                    ) {
//
//                        Column(
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//
//                            LinearProgressIndicator(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(3.dp),
//                                strokeCap = StrokeCap.Round,
//                                color = bright_blue,
//                                progress = progress.value,
//                                backgroundColor = Transparent
//                            )
//
//                            Row(
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//                                Box(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                        .background(
//                                            color = White
//                                        )
//                                        .padding(spacing.medium),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    IconButton(
//                                        onClick = {
//                                            coroutineScope.launch {
//                                                if (pagerState.canScrollForward && !pagerState.isScrollInProgress) {
//                                                    focusManager.clearFocus()
//                                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
//
//                                                }
//                                            }
//                                        }
//                                    ) {
//                                        Row(
//                                            verticalAlignment = Alignment.CenterVertically,
//                                            horizontalArrangement = Arrangement.spacedBy(
//                                                spacing.small,
//                                                Alignment.CenterHorizontally
//                                            )
//                                        ) {
//
//                                            Icon(
//                                                imageVector = Icons.Default.ChevronLeft,
//                                                contentDescription = null,
//                                                tint = Black
//                                            )
//
//                                            Text(
//                                                "이전",
//                                                style = LocalTextStyle.current.copy(
//                                                    color = Black,
//                                                    fontSize = 16.sp,
//                                                    fontWeight = FontWeight(550)
//                                                )
//                                            )
//
//                                        }
//                                    }
//                                }
//
//                                Box(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                        .background(
//                                            color = mustard_yellow
//                                        )
//                                        .padding(spacing.medium),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    IconButton(
//                                        onClick = {
//                                            coroutineScope.launch {
////                                                if (pagerState.canScrollBackward && !pagerState.isScrollInProgress && pagerState.currentPage != pagerState.pageCount - 1) {
////                                                    focusManager.clearFocus()
////                                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
////                                                    return@launch
////                                                }
////                                                if (pagerState.currentPage == pagerState.pageCount - 1 && !pagerState.isScrollInProgress) {
////                                                    navController.navigate(
////                                                        "$ROUTE_SCRIPT_WRITING_FINISH/{script}".replace(
////                                                            oldValue = "{script}",
////                                                            newValue = script.toJsonString()
////                                                        )
////                                                    ) {
////                                                        popUpTo(ROUTE_SCRIPT_WRITING) {
////                                                            inclusive = true
////                                                        }
////                                                    }
////                                                    return@launch
////                                                }
//
//                                            }
//                                        }
//                                    ) {
//                                        Row(
//                                            verticalAlignment = Alignment.CenterVertically,
//                                            horizontalArrangement = Arrangement.spacedBy(
//                                                spacing.small,
//                                                Alignment.CenterHorizontally
//                                            )
//                                        ) {
//                                            Text(
//                                                if (pagerState.currentPage != pagerState.pageCount - 1) "다음" else "작성완료",
//                                                style = LocalTextStyle.current.copy(
//                                                    color = White,
//                                                    fontSize = 16.sp,
//                                                    fontWeight = FontWeight(550),
//                                                    shadow = Shadow(
//                                                        color = DarkGray,
//                                                        offset = Offset(1f, 1f),
//                                                        blurRadius = 1f
//                                                    )
//                                                )
//                                            )
//
//                                            Icon(
//                                                imageVector = Icons.Default.ChevronRight,
//                                                contentDescription = null,
//                                                tint = White,
//                                                modifier = Modifier
//                                            )
//
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//
//                    }
//                }
//
//            }
//        ) { innerPadding ->
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(color = bg_grey)
//                    .padding(innerPadding),
//                verticalArrangement = Arrangement.Center
//            ) {
//
//                ScriptSettingsContent(
//                    title = title,
//                    scriptItemList = scriptItemList,
//                    jobRoleList = jobRoleList,
//                    pagerState = pagerState,
//                    scriptTitleChanged = { title ->
////                        viewModel.updateScriptTitle(title)
//                        scriptTitleChanged(title)
//                    },
//                    scriptItemClicked = { scriptItem ->
////                        viewModel.selectScriptItem(scriptItem)
//                        scriptItemClicked(scriptItem)
//                    },
//                    jobRoleClicked = { jobRole ->
//                        jobRoleClicked(jobRole)
//                    },
//                    nextButtonClicked = {
//                        nextButtonClicked()
//                    },
//                    canMakeScript = canMakeScript
//                )
//
////                HorizontalPager(
////                    state = pagerState,
////                    count = script.scriptItems.size + 1,
////                    userScrollEnabled = false
////                ) { page ->
////                    if (page == 0) { //첫페이지 자소서 제목, 직무, 질문 정하기
////                        ScriptSettingsContent(
////                            script = script,
////                            scriptItemList = scriptItemList,
////                            jobRoleList = jobRoleList,
////                            pagerState = pagerState,
////                            scriptTitleChanged = { title->
////                                viewModel.updateScriptTitle(title)
////                            },
////                            scriptItemClicked = { scriptItem->
////                                viewModel.selectScriptItem(scriptItem)
////                            },
////                            jobRoleClicked = { jobRole->
////                                viewModel.updateJobRole(jobRole)
////                            },
////                            canMakeScript = canMakeScript
////                        )
////
////                    } else {
////                        PagerContent(
////                            pagerState,
////                            page,
////                            script.scriptItems[page - 1]
////                        ) { updatedScriptItem ->
////
////
////                        }
////                    }
////                }
//            }
//
//        }
    }
}


@OptIn(
    ExperimentalPagerApi::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class, ExperimentalLayoutApi::class, ExperimentalMaterialApi::class
)
@Composable
private fun ScriptSettingsContent(
    title: String,
    scriptItemList: List<Pair<ScriptItem, Boolean>>,
    jobRoleList: List<Pair<String, Boolean>>,
    pagerState: PagerState,
    scriptTitleChanged: (String) -> Unit,
    scriptItemClicked: (ScriptItem) -> Unit,
    jobRoleClicked: (String) -> Unit,
    nextButtonClicked: () -> Unit,
    canMakeScript: Boolean
) {

    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current

    val spacing = LocalSpacing.current

    val scrollState = rememberScrollState()

    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val coroutineScope = rememberCoroutineScope()



    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.medium)
                    .clickableWithoutRipple {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                    .verticalScroll(scrollState),
//                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                /*
                자소서 제목입력
                 */
//                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = White,
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = spacing.medium,
                                    bottomStart = spacing.medium,
                                    bottomEnd = spacing.medium
                                )
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
                            color = Black,
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
                            color = DarkGray,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(spacing.medium))

                        BasicTextField(
                            value = title,
                            onValueChange = {
                                scriptTitleChanged(it)
                            },
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, color = LightGray, shape = RoundedCornerShape(10.dp))
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
//                }


                /*
                질문 선택부분
                 */
//                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = White,
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = spacing.medium,
                                    bottomStart = spacing.medium,
                                    bottomEnd = spacing.medium
                                )
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
                                text = "2. 질문 선택",
                                color = Black,
                                highlightColor = highlight_blue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            )
                            Spacer(modifier = Modifier.width(spacing.medium))
                            Text(
                                "(${scriptItemList.filter { it.second }.size}/5)",
                                color = DarkGray
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.small))
                        Text(
                            text = "선택한 질문으로 자기소개서가 구성돼요 :)",
                            textAlign = TextAlign.Start,
                            fontSize = 16.sp,
                            color = DarkGray,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(spacing.medium))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = bg_mono_grey,
                                    shape = RoundedCornerShape(spacing.medium)
                                )
                                .padding(
                                    vertical = spacing.medium,
                                    horizontal = spacing.small
                                )
                                .heightIn(150.dp, 300.dp)
                            ,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            items(scriptItemList) { scriptItem ->
                                Text(
                                    text = "\"${scriptItem.first.question} (${scriptItem.first.maxLength}자)\"",
                                    color = if (scriptItem.second) text_blue else Black,
                                    fontWeight = if (scriptItem.second) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .clickable {

                                            scriptItemClicked(
                                                scriptItem.first
                                            )


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
//                }


                /*
                직무 선택 부분
                 */
//                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = White,
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = spacing.medium,
                                    bottomStart = spacing.medium,
                                    bottomEnd = spacing.medium
                                )
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
                                color = Black,
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
                            color = DarkGray,
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
                                    color = if (jobRole.second) text_blue else Black,
                                    fontWeight = if (jobRole.second) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .clickable {
                                            jobRoleClicked(jobRole.first)
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
//                }


                /*
                다음 페이지 넘어가는 버튼
                 */
//                item {
                    AnimatedVisibility(
                        visible = canMakeScript
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
//                            .padding(horizontal = spacing.medium)
                                .height(50.dp)
                                .clickable {
//                                    coroutineScope.launch {
//                                        if (pagerState.canScrollBackward && !pagerState.isScrollInProgress) {
//                                            focusManager.clearFocus()
//                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
//                                        }
//                                    }
                                    nextButtonClicked()
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
                            Icon(
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
//                }


            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(vertical = spacing.medium)
            ) {
                ScrollToBottomButton(
                    scrollState,
                    3,
                    modifier = Modifier.size(40.dp)
                )
            }

        }

    }


}

@Composable
private fun ScrollToBottomButton(
    scrollState: ScrollState,
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
                Icon(
                    contentDescription = null,
                    imageVector = Icons.Default.ArrowDownward,
                    tint = White
                )
            }
        }

    }

}

//@OptIn(
//    ExperimentalComposeUiApi::class, ExperimentalPagerApi::class,
//    ExperimentalFoundationApi::class
//)
//@Composable
//private fun ScriptSettingContent(
//    script: Script,
//    pagerState: PagerState,
//    onScriptNameChange: (String) -> Unit
//) {
//
//
//    val focusManager = LocalFocusManager.current
//
//    val keyboardController = LocalSoftwareKeyboardController.current
//
//    val spacing = LocalSpacing.current
//
//    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/code_rocket.json"))
//    val progress by animateLottieCompositionAsState(
//        composition,
//        iterations = LottieConstants.IterateForever
//    )
//
//    val scriptName = remember {
//        mutableStateOf(script.title)
//    }
//
//
//    val coroutineScope = rememberCoroutineScope()
//
//    val bringIntoViewRequester = remember {
//        BringIntoViewRequester()
//    }
//
//    val textFieldFocused = remember {
//        mutableStateOf(false)
//    }
//
//    val simpleDateFormat = remember {
//        SimpleDateFormat("yyyy.MM.dd (E)", Locale.getDefault())
//    }
//
//
//
//    CompositionLocalProvider(
//        LocalTextStyle provides TextStyle(
//            fontFamily = nexonFont
//        )
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(spacing.medium)
//                .shadow(10.dp, shape = PocketBookShape(10.dp, circleWidth = 20.dp))
//                .background(
//                    color = White,
//                    shape = PocketBookShape(10.dp, circleWidth = 20.dp)
//                )
//                .padding(top = spacing.large, bottom = spacing.medium)
//                .clickableWithoutRipple {
//                    focusManager.clearFocus()
//                    keyboardController?.hide()
//                },
//            horizontalAlignment = Alignment.CenterHorizontally,
////            verticalArrangement = Arrangement.Center
//        ) {
//
//            Spacer(modifier = Modifier.height(spacing.large))
//
//            Divider(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = spacing.medium),
//                color = LightGray,
//                thickness = 1.dp
//            )
//
//            Spacer(modifier = Modifier.height(spacing.extraSmall))
//
//            Divider(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = spacing.medium),
//                color = LightGray,
//                thickness = 1.dp
//            )
//
//            Spacer(modifier = Modifier.height(spacing.small))
//
//            Text(
//                simpleDateFormat.format(Date(System.currentTimeMillis())),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = spacing.medium),
//                textAlign = TextAlign.End,
//                color = Gray,
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Normal
//            )
//
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(250.dp)
//                        .padding(spacing.medium)
//                        .aspectRatio(1f)
//                        .shadow(5.dp, shape = CircleShape)
//                        .background(
//                            brush = Brush.linearGradient(
//                                colors = listOf(
//                                    mustard_yellow,
//                                    orange_yellow
//                                )
//                            ),
//                            shape = CircleShape
//                        )
//                        .shimmerEffect(1500),
//                    contentAlignment = Alignment.Center
//                ) {
//                    LottieAnimation(
//                        composition = composition,
//                        progress = { progress }
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(spacing.medium))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.Top,
//                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
//                ) {
//
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_quotation_left),
//                        contentDescription = null,
//                        tint = Black
//                    )
//                    HighlightText(
//                        text = "자기소개서에 사용할 제목을 입력해주세요 !",
//                        highlightColor = bright_blue,
//                        alpha = 0.2f,
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight(550),
//                        color = text_blue
//                    )
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_quotation_right),
//                        contentDescription = null,
//                        tint = Black
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(spacing.medium))
//
//                TextField(
//                    value = scriptName.value,
//                    onValueChange = {
//                        onScriptNameChange(it)
//                        scriptName.value = it
//                    },
//                    colors = TextFieldDefaults.textFieldColors(
//                        textColor = Black,
//                        backgroundColor = Transparent,
//                        cursorColor = bright_blue,
//                        focusedIndicatorColor = Transparent,
//                        unfocusedIndicatorColor = Transparent,
//                    ),
//                    textStyle = LocalTextStyle.current.copy(
//                        textAlign = TextAlign.Center
//                    ),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = spacing.medium)
//                        .height(50.dp)
//                        .border(1.dp, color = LightGray, shape = RoundedCornerShape(10.dp))
//                        .bringIntoViewRequester(bringIntoViewRequester)
//                        .onFocusEvent { focusState ->
//                            if (focusState.isFocused) {
//                                coroutineScope.launch {
//                                    bringIntoViewRequester.bringIntoView()
//                                }
//                            }
//                        }
//                        .onFocusChanged { focusState ->
//                            textFieldFocused.value = focusState.isFocused
//                        },
//                    keyboardOptions = KeyboardOptions(
//                        imeAction = ImeAction.Done
//                    ),
//                    keyboardActions = KeyboardActions(
//                        onDone = {
//                            focusManager.clearFocus()
//                            keyboardController?.hide()
//                        }
//                    )
//                )
//
//                Spacer(modifier = Modifier.height(spacing.medium))
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = spacing.medium)
//                        .height(50.dp)
//                        .clickable {
//                            coroutineScope.launch {
//                                if (pagerState.canScrollBackward && !pagerState.isScrollInProgress) {
//                                    focusManager.clearFocus()
//                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
//                                }
//                            }
//                        }
//                        .shadow(
//                            2.dp,
//                            shape = RoundedCornerShape(10.dp)
//                        )
//                        .background(
//                            color = bright_blue,
//                            shape = RoundedCornerShape(10.dp)
//                        ),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_pencil),
//                        contentDescription = null,
//                        tint = White
//                    )
//                    Spacer(modifier = Modifier.width(spacing.small))
//                    Text(
//                        "자기소개서 작성하러 가기",
//                        fontWeight = FontWeight(550),
//                        color = White,
//                        fontSize = 16.sp
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(spacing.small))
//
//                Text(
//                    "* 자기소개서 수정시 해당 자기소개서를 기반으로 만들어진\n면접 질문은 사용하실 수 없습니다",
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 12.sp,
//                    color = Black,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = spacing.medium),
//                    textAlign = TextAlign.End
//                )
//            }
//
//
//        }
//    }
//
//}

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
private fun PagerPreview() {

    val tips = mutableListOf<String>()

    tips.add("맞춤법에 어긋나는 표현이나 반복적인 표현 등을 주의해야 합니다")

    val scriptItem = ScriptItem(
        question = "프로젝트에서 어려웠던 점과 극복했던 경험을 서술하세요",
        answer = "",
        maxLength = 500,
        tips = tips,
        index = 1
    )

    PagerContent(
        rememberPagerState(),
        1,
        scriptItem = scriptItem,
        onScriptItemUpdate = {

        }
    )
}

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class, ExperimentalPagerApi::class
)
@Composable
private fun PagerContent(
    pagerState: PagerState,
    page: Int,
    scriptItem: ScriptItem,
    onScriptItemUpdate: (ScriptItem) -> Unit
) {

    val spacing = LocalSpacing.current

    val tipPage = remember {
        mutableStateOf(0)
    }

    val answer = remember {
        mutableStateOf(scriptItem.answer ?: "")
    }

    val focusManager = LocalFocusManager.current

    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val keyboardController = LocalSoftwareKeyboardController.current


    var textFieldFocused by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current


    LaunchedEffect(pagerState.currentPage) {

        while (pagerState.currentPage == page) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.medium)
                .shadow(10.dp, shape = PocketBookShape(10.dp, circleWidth = 20.dp))
                .background(
                    color = bright_blue,
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
                color = White,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(spacing.extraSmall))

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium),
                color = White,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                "$page. ${scriptItem.question} (${scriptItem.maxLength}자)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium, vertical = spacing.small),
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                fontWeight = FontWeight(550),
                color = White
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium)
                    .shadow(5.dp, shape = RoundedCornerShape(5.dp))
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
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tip),
                        contentDescription = null,
                        tint = text_blue
                    )
                    Text(
                        "작성 Tip",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = text_blue
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
                    .shadow(5.dp, shape = RoundedCornerShape(5.dp))
                    .background(color = White, shape = RoundedCornerShape(5.dp))
                    .padding(vertical = spacing.medium, horizontal = spacing.small),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = answer.value,
                    onValueChange = {
                        if (it.length > scriptItem.maxLength) {
                            AlertUtils.showToast(context, "글자 수를 초과하여 작성할 수 없어요")
                            return@TextField
                        }
                        answer.value = it
                        onScriptItemUpdate(
                            scriptItem.copy(
                                answer = answer.value
                            )
                        )
                    },
                    trailingIcon = null,
                    textStyle = LocalTextStyle.current.copy(
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
                        backgroundColor = Transparent,
                        textColor = Black,
                        focusedIndicatorColor = Transparent,
                        unfocusedIndicatorColor = Transparent,
                        cursorColor = bright_blue
                    ),
                    placeholder = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pencil),
                                contentDescription = null,
                                tint = LightGray
                            )

                            Spacer(modifier = Modifier.width(spacing.small))

                            Text(
                                "작성하기",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LightGray
                            )

                        }
                    }
                )

                Text(
                    "(${answer.value.length}/${scriptItem.maxLength})",
                    color = text_red,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }

}
