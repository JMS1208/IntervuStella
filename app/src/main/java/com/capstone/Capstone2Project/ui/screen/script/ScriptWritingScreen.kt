package com.capstone.Capstone2Project.ui.screen.script

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.utils.composable.HighlightText
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
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
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.ScriptItem
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.navigation.ROUTE_SCRIPT_WRITING
import com.capstone.Capstone2Project.navigation.ROUTE_SCRIPT_WRITING_FINISH
import com.capstone.Capstone2Project.ui.screen.auth.AuthViewModel
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.PocketBookShape
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.extensions.shimmerEffect
import com.google.accompanist.pager.PagerState
import java.text.SimpleDateFormat
import java.util.*

@Preview(showBackground = true)
@Composable
private fun Preview() {

    val script = remember {
        val tips = listOf("팁 예시1111111111", "팁 예시22222222222", "팁 예시3333333333")

        val scriptUUID = UUID.randomUUID().toString()

        val items = listOf(
            ScriptItem(question = "예시 질문", tips = tips, maxLength = 300, scriptUUID = scriptUUID),
            ScriptItem(question = "예시 질문", tips = tips, maxLength = 500, scriptUUID = scriptUUID),
            ScriptItem(question = "예시 질문", tips = tips, maxLength = 300, scriptUUID = scriptUUID),
            ScriptItem(question = "예시 질문", tips = tips, maxLength = 500, scriptUUID = scriptUUID),
        )

        val hostUUID = UUID.randomUUID().toString()

        Script(
            uuid = scriptUUID,
            host = hostUUID,
            date = System.currentTimeMillis(),
            name = "",
            questionnaireState = false,
            scriptItems = items
        )
    }

    ScriptPaperContent(
        navController = rememberNavController(),
        script = script
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptWritingScreen(
    navController: NavController,
    _script: Script? = null
) {

    val viewModel: ScriptWritingViewModel = hiltViewModel()

    val authViewModel: AuthViewModel = hiltViewModel()

    val hostUUID = remember {
        authViewModel.currentUser?.uid
    }

    val scriptFlow = viewModel.scriptFlow.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.createEmptyScript(hostUUID)
    }

    val context = LocalContext.current


    if (_script != null) {
        ScriptPaperContent(
            navController = navController,
            script = _script
        )
    } else {
        scriptFlow.value?.let {
            when (it) {
                is Resource.Error -> {
                    it.error?.message?.let { message ->
                        AlertUtils.showToast(context, message, Toast.LENGTH_LONG)
                    }
                    navController.popBackStack()
                }
                Resource.Loading -> {
                    LoadingScreen()
                }
                is Resource.Success -> {
                    ScriptPaperContent(
                        navController = navController,
                        script = it.data
                    )
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
private fun ScriptPaperContent(
    navController: NavController,
    script: Script
) {
    val spacing = LocalSpacing.current

    val pagerState = rememberPagerState()

    val progress = animateFloatAsState(
        targetValue = (pagerState.currentPage.toFloat() / script.scriptItems.size),
        animationSpec = tween(delayMillis = 50, durationMillis = 1000)
    )

    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    val viewModel: ScriptWritingViewModel = hiltViewModel()

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = Black
        )
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
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
                                fontSize = 16.sp
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
            },
            bottomBar = {
                if (pagerState.currentPage != 0) {
                    BottomAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Transparent,
                        contentPadding = PaddingValues(0.dp)
                    ) {

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp),
                                strokeCap = StrokeCap.Round,
                                color = bright_blue,
                                progress = progress.value,
                                backgroundColor = Transparent
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            color = White
                                        )
                                        .padding(spacing.medium),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                if (pagerState.canScrollForward && !pagerState.isScrollInProgress) {
                                                    focusManager.clearFocus()
                                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)

                                                }
                                            }
                                        }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(
                                                spacing.small,
                                                Alignment.CenterHorizontally
                                            )
                                        ) {

                                            Icon(
                                                imageVector = Icons.Default.ChevronLeft,
                                                contentDescription = null,
                                                tint = Black
                                            )

                                            Text(
                                                "이전",
                                                style = LocalTextStyle.current.copy(
                                                    color = Black,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight(550)
                                                )
                                            )

                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            color = mustard_yellow
                                        )
                                        .padding(spacing.medium),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                if (pagerState.canScrollBackward && !pagerState.isScrollInProgress && pagerState.currentPage != pagerState.pageCount - 1) {
                                                    focusManager.clearFocus()
                                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                                    return@launch
                                                }
                                                if (pagerState.currentPage == pagerState.pageCount - 1 && !pagerState.isScrollInProgress) {
                                                    navController.navigate(
                                                        "$ROUTE_SCRIPT_WRITING_FINISH/{script}".replace(
                                                            oldValue = "{script}",
                                                            newValue = script.toJsonString()
                                                        )
                                                    ) {
                                                        popUpTo(ROUTE_SCRIPT_WRITING) {
                                                            inclusive = true
                                                        }
                                                    }
                                                    return@launch
                                                }

                                            }
                                        }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(
                                                spacing.small,
                                                Alignment.CenterHorizontally
                                            )
                                        ) {
                                            Text(
                                                if (pagerState.currentPage != pagerState.pageCount - 1) "다음" else "작성완료",
                                                style = LocalTextStyle.current.copy(
                                                    color = White,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight(550),
                                                    shadow = Shadow(
                                                        color = DarkGray,
                                                        offset = Offset(1f, 1f),
                                                        blurRadius = 1f
                                                    )
                                                )
                                            )

                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = White,
                                                modifier = Modifier
                                            )

                                        }
                                    }
                                }
                            }
                        }


                    }
                }

            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = bg_grey)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center
            ) {

                HorizontalPager(
                    state = pagerState,
                    count = script.scriptItems.size + 1,
                    userScrollEnabled = false
                ) { page ->
                    if (page == 0) { //첫페이지 자소서 이름 정하기
                        ScriptNameSetting(script, pagerState) { updatedName ->

                            val updatedScript = script.copy(
                                name = updatedName
                            )

                            viewModel.updateScript(updatedScript)
                        }
                    } else {
                        PagerContent(
                            pagerState,
                            page,
                            script.scriptItems[page - 1]
                        ) { updatedScriptItem ->

                            val updatedScript = script.copy(
                                scriptItems = script.scriptItems.onEach {
                                    if (it.itemUUID == updatedScriptItem.itemUUID) {
                                        it.answer = updatedScriptItem.answer
                                    }
                                }
                            )

                            viewModel.updateScript(updatedScript)
                        }
                    }
                }
            }

        }
    }
}

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalPagerApi::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun ScriptNameSetting(
    script: Script,
    pagerState: PagerState,
    onScriptNameChange: (String) -> Unit
) {


    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current

    val spacing = LocalSpacing.current

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/code_rocket.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val scriptName = remember {
        mutableStateOf(script.name)
    }


    val coroutineScope = rememberCoroutineScope()

    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val textFieldFocused = remember {
        mutableStateOf(false)
    }

    val simpleDateFormat = remember {
        SimpleDateFormat("yyyy.MM.dd (E)", Locale.getDefault())
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
                    color = White,
                    shape = PocketBookShape(10.dp, circleWidth = 20.dp)
                )
                .padding(top = spacing.large, bottom = spacing.medium)
                .clickableWithoutRipple {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
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
                simpleDateFormat.format(Date(System.currentTimeMillis())),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium),
                textAlign = TextAlign.End,
                color = Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .padding(spacing.medium)
                        .aspectRatio(1f)
                        .shadow(5.dp, shape = CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    mustard_yellow,
                                    orange_yellow
                                )
                            ),
                            shape = CircleShape
                        )
                        .shimmerEffect(1500),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress }
                    )
                }

                Spacer(modifier = Modifier.height(spacing.medium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_quotation_left),
                        contentDescription = null,
                        tint = Black
                    )
                    HighlightText(
                        text = "자기소개서에 사용할 제목을 입력해주세요 !",
                        highlightColor = bright_blue,
                        alpha = 0.2f,
                        fontSize = 16.sp,
                        fontWeight = FontWeight(550),
                        color = text_blue
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_quotation_right),
                        contentDescription = null,
                        tint = Black
                    )
                }

                Spacer(modifier = Modifier.height(spacing.medium))

                TextField(
                    value = scriptName.value,
                    onValueChange = {
                        onScriptNameChange(it)
                        scriptName.value = it
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Black,
                        backgroundColor = Transparent,
                        cursorColor = bright_blue,
                        focusedIndicatorColor = Transparent,
                        unfocusedIndicatorColor = Transparent,
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium)
                        .height(50.dp)
                        .border(1.dp, color = LightGray, shape = RoundedCornerShape(10.dp))
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
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium)
                        .height(50.dp)
                        .clickable {
                            coroutineScope.launch {
                                if (pagerState.canScrollBackward && !pagerState.isScrollInProgress) {
                                    focusManager.clearFocus()
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        }
                        .shadow(
                            2.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            color = bright_blue,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pencil),
                        contentDescription = null,
                        tint = White
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        "자기소개서 작성하러 가기",
                        fontWeight = FontWeight(550),
                        color = White,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(spacing.small))

                Text(
                    "* 자기소개서 수정시 해당 자기소개서를 기반으로 만들어진\n면접 질문은 사용하실 수 없습니다",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium),
                    textAlign = TextAlign.End
                )
            }


        }
    }

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
