package com.capstone.Capstone2Project.ui.screen.mypage

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.InspiringKeyword
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.data.model.InterviewScore
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.inapp.TodayQuestionMemo
import com.capstone.Capstone2Project.data.resource.DataState
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.navigation.ROUTE_LOGIN
import com.capstone.Capstone2Project.navigation.ROUTE_SCRIPT_WRITING
import com.capstone.Capstone2Project.ui.screen.auth.AuthViewModel
import com.capstone.Capstone2Project.ui.screen.error.ErrorScreen
import com.capstone.Capstone2Project.ui.screen.home.ChartScreen
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.etc.invokeVibration
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MyPageScreen(navController = rememberNavController())
}

@Composable
fun MyPageScreen(
    navController: NavController
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    val viewModel: MyPageViewModel = hiltViewModel()

    val context = LocalContext.current

    val firebaseUser = authViewModel.currentUser

    if (firebaseUser == null) {
        LaunchedEffect(Unit) {
            navController.navigate(ROUTE_LOGIN) {
                popUpTo(ROUTE_LOGIN) {
                    inclusive = true
                }
            }
            AlertUtils.showToast(context, "다시 로그인 해주세요")
        }
    } else {

        LaunchedEffect(authViewModel) {
            firebaseUser.uid.let {
                with(viewModel) {
                    fetchMyInspiringKeywords(it)
                    fetchMyInterviewRecords(it)
                    fetchMyInterviewScores(it)
                    fetchMyScripts(it)
                    fetchMyTodayQuestionsMemo(it)
                }
            }
        }

        MyPageContent(
            navController,
            firebaseUser = firebaseUser,
            logOutClicked = authViewModel::logout
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
private fun MyPageContent(
    navController: NavController,
    firebaseUser: FirebaseUser,
    logOutClicked: () -> Unit
) {

    val viewModel: MyPageViewModel = hiltViewModel()

    val spacing = LocalSpacing.current

    val stateFlow = viewModel.state.collectAsStateWithLifecycle()

    val userName = remember(firebaseUser) {
        firebaseUser.displayName ?: "(이름 없음)"
    }

    val userEmail = remember(firebaseUser) {
        firebaseUser.email ?: "(이메일 미지정)"
    }


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = Black
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    title = {
                        Text(
                            "마이 페이지",
                            style = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                    },
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
            when(stateFlow.value.dataState) {
                is DataState.Error -> {
                    ErrorScreen((stateFlow.value.dataState as DataState.Error).message)
                }
                DataState.Loading -> {
                    LoadingScreen()
                }
                DataState.Normal -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(
                                color = bg_grey
                            )
                            .verticalScroll(rememberScrollState())
                            .padding(spacing.small)
                    ) {

                        Spacer(modifier = Modifier.height(spacing.medium))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing.small)
                                .wrapContentHeight()
                                .shadow(3.dp, shape = RoundedCornerShape(5.dp))
                                .background(
                                    color = darker_blue,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clip(
                                    shape = RoundedCornerShape(5.dp)
                                )
                        ) {
                            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/astronaut.json"))
                            val progress by animateLottieCompositionAsState(
                                composition,
                                iterations = LottieConstants.IterateForever
                            )

                            LottieAnimation(
                                composition = composition,
                                progress = { progress },
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )

                            Text(
                                "자기소개서를 작성하고\nAI 모의면접을 시작해보세요 !",
                                style = LocalTextStyle.current.copy(
                                    color = White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight(550),
                                    shadow = Shadow(
                                        color = DarkGray,
                                        offset = Offset(1f, 1f),
                                        blurRadius = 4f
                                    ),
                                    textAlign = TextAlign.Start
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.medium, vertical = spacing.large)
                            )

                        }

                        Column(
                            modifier = Modifier
                                .padding(spacing.small)
                                .shadow(3.dp, shape = RoundedCornerShape(5.dp))
                                .background(
                                    color = White,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .padding(spacing.small)
                        ) {

                            Spacer(modifier = Modifier.height(spacing.small))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.small)
                                    .border(
                                        1.dp,
                                        color = LightGray,
                                        shape = RoundedCornerShape(5.dp)
                                    )
                                    .padding(spacing.small)
                                    .wrapContentHeight(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        userName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkGray
                                    )
                                    Spacer(modifier = Modifier.height(spacing.small))
                                    Text(
                                        userEmail,
                                        color = text_blue,
                                        textDecoration = TextDecoration.Underline,
                                        fontSize = 14.sp
                                    )
                                }


                                Divider(
                                    color = LightGray,
                                    modifier = Modifier
                                        .padding(spacing.small)
                                        .height(40.dp)
                                        .width(1.dp)
                                )

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_github),
                                            contentDescription = null,
                                            tint = Black,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(spacing.small))
                                        Text(
                                            userName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = DarkGray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(spacing.small))
                                    Text(
                                        "깃허브 닉네임",
                                        color = text_blue,
                                        textDecoration = TextDecoration.Underline,
                                        fontSize = 14.sp
                                    )
                                }

                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.small),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "깃허브 레포지토리와 연동해보세요 !",
                                    color = Color(0xFF2238FF),
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFBFC6FF)
                                        )
                                        .padding(4.dp),
                                    fontWeight = FontWeight.SemiBold
                                )

                                IconButton(
                                    onClick = viewModel::showGitLinkDialog
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .border(
                                                1.dp,
                                                color = text_blue,
                                                shape = RoundedCornerShape(50)
                                            )
                                            .padding(
                                                horizontal = spacing.small,
                                                vertical = spacing.extraSmall
                                            ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_link),
                                            contentDescription = null,
                                            tint = text_blue,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                                        Text(
                                            "연동",
                                            color = text_blue,
                                            fontSize = 14.sp
                                        )
                                    }

                                }

                            }
                        }

                        Spacer(modifier = Modifier.height(spacing.large))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing.small)
                                .shadow(3.dp, shape = RoundedCornerShape(5.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            bright_blue,
                                            bright_purple
                                        )
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .padding(spacing.medium)
                        ) {
                            Text(
                                "자기소개서 키워드",
                                fontWeight = FontWeight(550),
                                color = White,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start,
                                fontSize = 20.sp
                            )

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                "자기소개서를 작성하기 막막하신가요?\n우선 키워드로 시작해보세요 !",
                                color = White,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(spacing.large))

                            FlowRow(
                                crossAxisSpacing = spacing.extraSmall,
                                mainAxisSpacing = spacing.extraSmall,
                                modifier = Modifier.fillMaxWidth(),
                                mainAxisAlignment = FlowMainAxisAlignment.SpaceAround,
                                lastLineMainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly
                            ) {

                                stateFlow.value.inspiringKeywords.let {
                                    it.forEach { keyword ->
                                        AnimatedContent(targetState = keyword) {
                                            KeywordItem(keyword)
                                        }
                                    }
                                }
                                KeywordItemAdd(onItemClick = viewModel::showKeywordAddingDialog)

                            }
                        }

                        Spacer(modifier = Modifier.height(spacing.small))

                        Spacer(modifier = Modifier.height(spacing.large))

                        Spacer(modifier = Modifier.height(spacing.small))

                        MyTodayQuestionMemo(navController, firebaseUser = firebaseUser, stateFlow.value.todayQuestionsMemo)

                        Spacer(modifier = Modifier.height(spacing.large))

                        Spacer(modifier = Modifier.height(spacing.small))

                        MyScriptList(navController, firebaseUser = firebaseUser, stateFlow.value.myScripts)

                        Spacer(modifier = Modifier.height(spacing.large))

                        Spacer(modifier = Modifier.height(spacing.small))

                        MyInterviewRecords(navController, firebaseUser, stateFlow.value.myInterviewRecords)

                        Spacer(modifier = Modifier.height(spacing.small))

                        MyInterviewScores(navController, firebaseUser, stateFlow.value.myInterviewScores)

                        Spacer(modifier = Modifier.height(spacing.small))

                        LogOutText(modifier = Modifier, clicked = logOutClicked)

                        Spacer(modifier = Modifier.height(spacing.large))

                    }
                }
            }


            when (stateFlow.value.dialogState) {
                MyPageViewModel.DialogState.GitLinkDialog -> {
                    GitLinkDialog(
                        firebaseUser = firebaseUser,
                        viewModel = viewModel,
                        onDismissRequest = viewModel::closeDialog
                    )
                }

                MyPageViewModel.DialogState.KeywordAddingDialog -> {
                    KeywordAddingDialog(
                        firebaseUser = firebaseUser,
                        viewModel,
                        onDismissRequest = viewModel::closeDialog
                    )
                }

                MyPageViewModel.DialogState.MemoDialog -> {

                }

                MyPageViewModel.DialogState.Nothing -> Unit
            }
        }
    }
}


@Composable
fun LogOutText(
    modifier: Modifier = Modifier,
    clicked: () -> Unit
) {

    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text("로그아웃",
            style = LocalTextStyle.current.copy(
                fontFamily = nexonFont,
                fontSize = 14.sp,
                color = text_blue,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.clickable {
                clicked()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogPreview() {
//    KeywordAddingDialog(onKeywordInput = {})
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun KeywordAddingDialog(
    firebaseUser: FirebaseUser,
    viewModel: MyPageViewModel,
    onDismissRequest: () -> Unit
) {

    val keyword = remember {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current

    val spacing = LocalSpacing.current


    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val coroutineScope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }

    val context = LocalContext.current

    fun insertKeyword() {
        if (keyword.value.isNotBlank()) {
            firebaseUser.uid.let { hostUUID ->
                val inspiringKeyword = InspiringKeyword(
                    hostUUID = hostUUID,
                    date = System.currentTimeMillis(),
                    keywordUUID = UUID.randomUUID().toString(),
                    keyword = keyword.value
                )

                viewModel.insertInspiringKeyword(inspiringKeyword)
                AlertUtils.showToast(context, "키워드가 추가되었어요")
            }
        } else {
            AlertUtils.showToast(context, "아무것도 입력하지 않았습니다.")
        }
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            fontSize = 18.sp
        )
    ) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickableWithoutRipple {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }

            ) {


                Column(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                onDismissRequest()
                            }
                            .padding(spacing.medium),
                        contentDescription = "닫기",
                        tint = White
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.medium),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Spacer(modifier = Modifier.height(spacing.medium))

                        Text(
                            "브레인스토밍을 위한\n자기소개서 키워드를 입력해주세요.",
                            modifier = Modifier.fillMaxWidth(),
                            style = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Start,
                                color = White,
                                shadow = Shadow(
                                    color = DarkGray,
                                    offset = Offset(1f, 1f),
                                    blurRadius = 8f
                                ),
                                fontWeight = FontWeight(550)
                            )
                        )

                        Spacer(modifier = Modifier.height(spacing.medium))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {


                            TextField(
                                value = keyword.value,
                                onValueChange = {
                                    keyword.value = it
                                },
                                placeholder = {
                                    Text(
                                        "이곳에 입력해주세요",
                                        style = LocalTextStyle.current.copy(
                                            color = White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                },
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        insertKeyword()
                                    }
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = spacing.small)
                                    .height(50.dp)
                                    .focusRequester(focusRequester)
                                    .bringIntoViewRequester(bringIntoViewRequester)
                                    .onFocusEvent { focusState ->
                                        if (focusState.isFocused) {
                                            coroutineScope.launch {
                                                bringIntoViewRequester.bringIntoView()
                                            }
                                        }
                                    },
                                colors = TextFieldDefaults.textFieldColors(
                                    textColor = White,
                                    disabledTextColor = White,
                                    backgroundColor = Transparent,
                                    cursorColor = White,
                                    focusedIndicatorColor = bright_blue,
                                    unfocusedIndicatorColor = White
                                ),
                            )

                            Spacer(modifier = Modifier.width(spacing.small))

                            IconButton(
                                onClick = {
                                    insertKeyword()
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .shadow(
                                            5.dp,
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                        .background(
                                            color = White,
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                        .padding(vertical = 5.dp, horizontal = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = bright_blue
                                    )

                                    Spacer(modifier = Modifier.width(spacing.small))

                                    Text(
                                        "추가",
                                        style = LocalTextStyle.current.copy(
                                            color = bright_blue,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }
                        }
                    }
                }
            }
        }
    }


}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun GitLinkDialog(
//    authViewModel: AuthViewModel,
    firebaseUser: FirebaseUser,
    viewModel: MyPageViewModel,
    onDismissRequest: () -> Unit
) {

    val keyword = remember {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current

    val spacing = LocalSpacing.current


    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val coroutineScope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }

    val context = LocalContext.current

    fun insertKeyword() {
        if (keyword.value.isNotBlank()) {
            firebaseUser.uid.let { hostUUID ->
                //ViewModel에 추가하기

            }
        } else {
            AlertUtils.showToast(context, "아무것도 입력하지 않았습니다.")
        }
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            fontSize = 18.sp
        )
    ) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickableWithoutRipple {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }

            ) {


                Column(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                onDismissRequest()
                            }
                            .padding(spacing.medium),
                        contentDescription = "닫기",
                        tint = White
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.medium),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Spacer(modifier = Modifier.height(spacing.medium))

                        Text(
                            "깃허브 연동을 위해\nGit-Hub 닉네임을 입력해주세요",
                            modifier = Modifier.fillMaxWidth(),
                            style = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Start,
                                color = White,
                                shadow = Shadow(
                                    color = DarkGray,
                                    offset = Offset(1f, 1f),
                                    blurRadius = 8f
                                ),
                                fontWeight = FontWeight(550)
                            )
                        )

                        Spacer(modifier = Modifier.height(spacing.medium))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {


                            TextField(
                                value = keyword.value,
                                onValueChange = {
                                    keyword.value = it
                                },
                                placeholder = {
                                    Text(
                                        "이곳에 입력해주세요",
                                        style = LocalTextStyle.current.copy(
                                            color = White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                },
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        insertKeyword()
                                    }
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = spacing.small)
                                    .height(50.dp)
                                    .focusRequester(focusRequester)
                                    .bringIntoViewRequester(bringIntoViewRequester)
                                    .onFocusEvent { focusState ->
                                        if (focusState.isFocused) {
                                            coroutineScope.launch {
                                                bringIntoViewRequester.bringIntoView()
                                            }
                                        }
                                    },
                                colors = TextFieldDefaults.textFieldColors(
                                    textColor = White,
                                    disabledTextColor = White,
                                    backgroundColor = Transparent,
                                    cursorColor = White,
                                    focusedIndicatorColor = bright_blue,
                                    unfocusedIndicatorColor = White
                                ),
                            )

                            Spacer(modifier = Modifier.width(spacing.small))

                            IconButton(
                                onClick = {
                                    insertKeyword()
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .shadow(
                                            5.dp,
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                        .background(
                                            color = White,
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                        .padding(vertical = 5.dp, horizontal = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = bright_blue
                                    )

                                    Spacer(modifier = Modifier.width(spacing.small))

                                    Text(
                                        "완료",
                                        style = LocalTextStyle.current.copy(
                                            color = bright_blue,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }
                        }
                    }
                }
            }
        }
    }


}

@Composable
fun MyTodayQuestionMemo(
    navController: NavController,
//    authViewModel: AuthViewModel,
    firebaseUser: FirebaseUser,
    todayQuestionsMemo: List<TodayQuestionMemo> = emptyList()
) {

    val viewModel: MyPageViewModel = hiltViewModel()

//    val stateFlow = viewModel.state.collectAsStateWithLifecycle()

    val spacing = LocalSpacing.current

//    LaunchedEffect(authViewModel) {
//        authViewModel.currentUser?.uid?.let {
//            with(viewModel) {
//                fetchMyTodayQuestions(it)
//            }
//        }
//    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = text_blue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(firebaseUser.displayName)
                        }

                        withStyle(
                            style = SpanStyle(
                                color = DarkGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("님의 모아둔 질문")
                        }
                    }
                )

            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.small),
                contentAlignment = Alignment.Center
            ) {
                val lazyListState = rememberLazyListState()

                LazyRow(
                    state = lazyListState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(end = 50.dp),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    item {
                        MyTodayQuestionsFirstItem(count = todayQuestionsMemo.size)
                    }

                    items(todayQuestionsMemo.size) { idx ->
                        val todayQuestion = todayQuestionsMemo[idx]
                        MyTodayQuestionsItem(todayQuestion, onItemClick = {
                            //TODO
                        })
                    }

                }

                ScrollToLeftButton(
                    lazyListState = lazyListState,
                    threshold = 0,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.CenterEnd)
                        .padding(spacing.small)
                )
//                stateFlow.value.todayQuestionsMemo.let {
//                    when (it) {
//                        is Resource.Error -> {
//                            Text("${it.error}")
//                        }
//
//                        Resource.Loading -> {
//                            CircularProgressIndicator(
//                                color = bright_blue
//                            )
//                        }
//
//                        is Resource.Success -> {
//
//                            val lazyListState = rememberLazyListState()
//
//                            LazyRow(
//                                state = lazyListState,
//                                modifier = Modifier.fillMaxWidth(),
//                                contentPadding = PaddingValues(end = 50.dp),
//                                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
//                            ) {
//                                item {
//                                    MyTodayQuestionsFirstItem(count = it.data.size)
//                                }
//
//                                items(it.data.size) { idx ->
//                                    val todayQuestion = it.data[idx]
//                                    MyTodayQuestionsItem(todayQuestion, onItemClick = {
//                                        //TODO
//                                    })
//                                }
//
//                            }
//
//                            ScrollToLeftButton(
//                                lazyListState = lazyListState,
//                                threshold = 0,
//                                modifier = Modifier
//                                    .size(60.dp)
//                                    .align(Alignment.CenterEnd)
//                                    .padding(spacing.small)
//                            )
//                        }
//                    }
//                }
            }

        }
    }


}

@Composable
private fun MyTodayQuestionsItem(
    todayQuestion: TodayQuestionMemo,
    onItemClick: (TodayQuestionMemo) -> Unit
) {
    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = Black
        )
    ) {
        Column(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(spacing.small)
                .clickable {
                    onItemClick(todayQuestion)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "Q. ${todayQuestion.question}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = text_blue
            )

            Spacer(modifier = Modifier.height(spacing.small))

            //TODO(아래 나중에 바꿔줘야함)
            Text(
                "A. ",
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MyTodayQuestionsFirstItem(count: Int) {
    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    color = bright_purple,
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            val (titleRef, countRef, imgRef) = createRefs()

            Text(
                "저장한 개수",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(parent.top, margin = spacing.medium)
                    start.linkTo(parent.start, margin = spacing.medium)
                }
            )

            Text(
                "$count 개",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.constrainAs(countRef) {
                    bottom.linkTo(parent.bottom, margin = spacing.medium)
                    start.linkTo(parent.start, margin = spacing.medium)
                }
            )

            Image(
                painter = painterResource(R.drawable.ic_memo),
                contentDescription = null,
                modifier = Modifier.constrainAs(imgRef) {
                    top.linkTo(parent.top, margin = spacing.small)
                    bottom.linkTo(parent.bottom, margin = spacing.small)
                    end.linkTo(parent.end, margin = spacing.small)
                    start.linkTo(titleRef.end, margin = spacing.small)
                }
            )


        }
    }
}

@Composable
fun MyInterviewScores(
    navController: NavController,
    firebaseUser: FirebaseUser,
    interviewScores: List<InterviewScore>
) {

    val spacing = LocalSpacing.current


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.small),
            contentAlignment = Alignment.Center
        ) {
            ChartScreen()
        }

    }

}

@Composable
fun MyInterviewRecords(
    navController: NavController,
    firebaseUser: FirebaseUser,
    interviewRecords: List<InterviewResult>
) {
    val spacing = LocalSpacing.current

    val lazyListState = rememberLazyListState()

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = text_blue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(firebaseUser.displayName)
                        }

                        withStyle(
                            style = SpanStyle(
                                color = DarkGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("님의 인터뷰 연습 기록")
                        }
                    }
                )

            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.small),
                contentAlignment = Alignment.Center
            ) {


                LazyRow(
                    state = lazyListState,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                    contentPadding = PaddingValues(end = 50.dp)
                ) {
                    item {
                        MyInterviewLogFirstItem(count = interviewRecords.size)
                    }

                    items(interviewRecords.size) { idx ->
                        val interviewRecord = interviewRecords[idx]
                        MyInterviewRecordItem(interviewRecord)
                    }

                }

                ScrollToLeftButton(
                    lazyListState = lazyListState,
                    threshold = 0,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.CenterEnd)
                        .padding(spacing.small)
                )
            }


        }
    }

}

@Composable
private fun ScrollToLeftButton(
    lazyListState: LazyListState,
    threshold: Int,
    modifier: Modifier = Modifier
) {

    val isVisible by remember(threshold) {
        derivedStateOf { lazyListState.firstVisibleItemIndex > 0 }
    }

    val coroutineScope = rememberCoroutineScope()

    if (isVisible) {
        FloatingActionButton(
            modifier = modifier,
            onClick = {
                coroutineScope.launch {
                    lazyListState.animateScrollToItem(
                        index = 0
                    )
                }
            },
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 3.dp,
                pressedElevation = 6.dp,
                hoveredElevation = 4.dp,
                focusedElevation = 4.dp
            ),
            shape = CircleShape,
            backgroundColor = highlight_red

        ) {
            Icon(
                contentDescription = null,
                imageVector = Icons.Default.ArrowBack,
                tint = White
            )
        }
    }
}

@Composable
fun MyInterviewRecordItem(interviewRecord: InterviewResult) {
    val spacing = LocalSpacing.current

    val simpleDateFormat = remember {
        SimpleDateFormat("yy.MM.dd (E) hh:mm", Locale.getDefault())
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = Black
        )
    ) {
        Column(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                interviewRecord.interviewUUID,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                simpleDateFormat.format(Date(interviewRecord.interviewDate)),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MyInterviewLogFirstItem(count: Int) {
    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = Black
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    color = bright_blue,
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            val (titleRef, countRef, imgRef) = createRefs()

            Text(
                "연습 횟수",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(parent.top, margin = spacing.medium)
                    start.linkTo(parent.start, margin = spacing.medium)
                }
            )

            Text(
                "$count 번",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.constrainAs(countRef) {
                    bottom.linkTo(parent.bottom, margin = spacing.medium)
                    start.linkTo(parent.start, margin = spacing.medium)
                }
            )

            Image(
                painter = painterResource(R.drawable.ic_blue_microphone),
                contentDescription = null,
                modifier = Modifier.constrainAs(imgRef) {
                    top.linkTo(parent.top, margin = spacing.small)
                    bottom.linkTo(parent.bottom, margin = spacing.small)
                    end.linkTo(parent.end, margin = spacing.small)
                    start.linkTo(titleRef.end, margin = spacing.small)
                }
            )


        }
    }
}

@Composable
private fun MyScriptList(
    navController: NavController,
    firebaseUser: FirebaseUser,
    myScripts: List<Script> = emptyList()
) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = text_blue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(firebaseUser.displayName)
                        }

                        withStyle(
                            style = SpanStyle(
                                color = DarkGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("님의 자기소개서")
                        }
                    }
                )

                Text(
                    "새로 작성",
                    style = LocalTextStyle.current.copy(
                        color = text_blue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.clickable {
                        navController.navigate(ROUTE_SCRIPT_WRITING)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.small),
                contentAlignment = Alignment.Center
            ) {

                val lazyListState = rememberLazyListState()



                LazyRow(
                    state = lazyListState,
                    contentPadding = PaddingValues(end = 50.dp),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    item {
                        MyScriptFirstItem(count = myScripts.size)
                    }

                    items(myScripts.size) { idx ->
                        val script = myScripts[idx]
                        MyScriptItem(script)
                    }

                }

                ScrollToLeftButton(
                    lazyListState = lazyListState,
                    threshold = 0,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.CenterEnd)
                        .padding(spacing.small)
                )
            }
        }


    }


}

@Composable
private fun MyScriptItem(
    script: Script
) {

    val spacing = LocalSpacing.current

    val simpleDateFormat = remember {
        SimpleDateFormat("yy.MM.dd (E) hh:mm", Locale.getDefault())
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = Black
        )
    ) {
        Column(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                script.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                simpleDateFormat.format(Date(script.date)),
                fontSize = 14.sp
            )
        }
    }

}

@Composable
private fun MyScriptFirstItem(
    count: Int
) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    color = deep_darker_blue,
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            val (titleRef, countRef, imgRef) = createRefs()

            Text(
                "작성한 개수",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(parent.top, margin = spacing.medium)
                    start.linkTo(parent.start, margin = spacing.medium)
                }
            )

            Text(
                "$count 개",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.constrainAs(countRef) {
                    bottom.linkTo(parent.bottom, margin = spacing.medium)
                    start.linkTo(parent.start, margin = spacing.medium)
                }
            )

            Image(
                painter = painterResource(R.drawable.ic_blue_script),
                contentDescription = null,
                modifier = Modifier.constrainAs(imgRef) {
                    top.linkTo(parent.top, margin = spacing.small)
                    bottom.linkTo(parent.bottom, margin = spacing.small)
                    end.linkTo(parent.end, margin = spacing.small)
                    start.linkTo(titleRef.end, margin = spacing.small)
                }
            )


        }
    }

}


@Composable
private fun KeywordItemAdd(
    onItemClick: () -> Unit
) {

    val spacing = LocalSpacing.current


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .height(30.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(50)
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            mustard_yellow,
                            orange_yellow
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
                .clickable {
                    onItemClick()
                }
                .padding(vertical = spacing.small, horizontal = spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = White
            )
            Spacer(modifier = Modifier.width(spacing.extraSmall))
            Text("추가", fontSize = 14.sp, color = White, fontWeight = FontWeight(550))
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KeywordItem(
    keyword: InspiringKeyword,
    viewModel: MyPageViewModel = hiltViewModel()
) {

    val spacing = LocalSpacing.current

    val context = LocalContext.current

    val focusedState = rememberSaveable {
        mutableStateOf(false)
    }

    val textModifier = remember(focusedState.value) {
        if (focusedState.value) {
            Modifier
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(50)
                )
                .border(
                    width = 1.dp,
                    color = bright_white_blue,
                    shape = RoundedCornerShape(50)
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = spacing.medium, vertical = spacing.small)
        } else {
            Modifier
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(50)
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = spacing.medium, vertical = spacing.small)
        }

    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Box(
            modifier = Modifier
                .clickable {
                    focusedState.value = !focusedState.value
                }
        ) {

            Text(
                keyword.keyword,
                fontSize = 14.sp,
                color = DarkGray,
                modifier = textModifier
            )

            if (
                focusedState.value
            ) {

                Icon(
                    contentDescription = null,
                    tint = Gray,
                    imageVector = Icons.Default.Close,
                    modifier = Modifier
                        .offset(x = 5.dp, y = (-5).dp)
                        .shadow(
                            1.dp,
                            shape = CircleShape
                        )
                        .border(
                            1.dp,
                            shape = CircleShape,
                            color = bright_white_blue
                        )
                        .background(
                            color = White,
                            shape = CircleShape
                        )
                        .size(20.dp)
                        .aspectRatio(1f)
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                        .clickable {
                            invokeVibration(context)
                            viewModel.deleteInspiringKeyword(keyword)
                            AlertUtils.showToast(context, "삭제 완료")
                        }
                )

            }

        }
    }

}