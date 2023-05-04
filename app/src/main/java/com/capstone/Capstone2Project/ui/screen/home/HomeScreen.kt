package com.capstone.Capstone2Project.ui.screen.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.Achievement
import com.capstone.Capstone2Project.data.model.Topic
import com.capstone.Capstone2Project.data.model.inapp.TodayAttendanceQuiz
import com.capstone.Capstone2Project.data.model.inapp.WeekAttendanceInfo
import com.capstone.Capstone2Project.data.model.inapp.WeekItem
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.data.resource.throwableOrNull
import com.capstone.Capstone2Project.navigation.*
import com.capstone.Capstone2Project.ui.screen.auth.AuthViewModel
import com.capstone.Capstone2Project.ui.screen.error.ErrorScreen
import com.capstone.Capstone2Project.ui.screen.interview.stopRecordingService
import com.capstone.Capstone2Project.ui.screen.intro.InterviewIntroDialog
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.HighlightText
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.WithEmojiView
import com.capstone.Capstone2Project.utils.extensions.gradientBackground
import com.capstone.Capstone2Project.utils.extensions.shimmerEffect
import com.capstone.Capstone2Project.utils.theme.*
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.YearMonth
import java.util.*


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {

    val homeViewModel: HomeViewModel = hiltViewModel()

    val authViewModel: AuthViewModel = hiltViewModel()

    HomeScreen(rememberNavController(), homeViewModel, authViewModel)

}

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    authViewModel: AuthViewModel
) {

    val context = LocalContext.current

    val state = homeViewModel.state.collectAsState()

    LaunchedEffect(homeViewModel) {
        homeViewModel.effect.collect {
            when (it) {
                is HomeViewModel.Effect.ShowMessage -> {
                    AlertUtils.showToast(context, it.message)
                }
            }
        }
    }

    LaunchedEffect(authViewModel) {
        authViewModel.currentUser?.uid?.let {
            homeViewModel.fetchAllHomeData(it)

        } ?: run {
            navController.navigate(ROUTE_LOGIN) {
                popUpTo(ROUTE_HOME) {
                    inclusive = true
                }
            }
        }

    }

    state.value?.let {
        HomeScreenContent(
            navController,
            it
        )
    }


}


@Composable
private fun HomeScreenContent(
    navController: NavController,
    state: HomeViewModel.State
) {
    val scrollState = rememberScrollState()

    val spacing = LocalSpacing.current

    val context = LocalContext.current

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            InterviewButton(navController = navController)
        }
    ) { innerPadding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = bg_grey
                )
                .padding(innerPadding)
                .navigationBarsPadding()
                .padding(top = 30.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(spacing.large, Alignment.Top)
        ) {

            LogoAndInfoContent(
                modifier = Modifier.fillMaxWidth(),
                navController
            )

            ScriptAndInfoContent(
                modifier = Modifier.fillMaxWidth(),
                navController
            )


            MyTopicAndTodayQuiz(
                modifier = Modifier.fillMaxWidth(),
                navController,
                topicsState = state.topics,
                todayAttendanceQuiz = state.todayAttendanceQuiz
            )

            AttendanceCheck(
                modifier = Modifier.fillMaxWidth(),
                weekAttendanceInfo = state.weekAttendanceInfo
            )

            Spacer(modifier = Modifier.height(spacing.large))
            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }


}

@Composable
private fun InterviewButton(
    modifier: Modifier = Modifier,
    navController: NavController
) {


    val spacing = LocalSpacing.current


    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        InterviewIntroDialog(navController = navController) {
            showDialog = false
        }
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        FloatingActionButton(
            onClick = { showDialog = true },
            shape = RoundedCornerShape(30.dp),
            backgroundColor = bright_blue,
            modifier = Modifier.align(Alignment.Center),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 3.dp,
                pressedElevation = 6.dp,
                hoveredElevation = 4.dp,
                focusedElevation = 4.dp
            )
        ) {


            Row(
                modifier = Modifier.padding(horizontal = spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_microphone),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)

                )
                Text(
                    text = "모의면접 시작",
                    modifier = Modifier,
                    style = LocalTextStyle.current.copy(
                        color = White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = nexonFont
                    )
                )
            }


        }

    }


}

@Composable
private fun MyAchievement(
    modifier: Modifier = Modifier
) {

    val spacing = LocalSpacing.current

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.medium)
        ) {


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        3.dp,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                bright_blue,
                                darker_blue
                            )
                        ),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .shimmerEffect(2000)

            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.small)
                ) {

                    Spacer(modifier = Modifier.height(spacing.medium))

                    Text(
                        "나의 달성 업적",
                        color = White,
                        fontWeight = FontWeight(550),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.small),
                        textAlign = TextAlign.Start,
                        fontSize = 20.sp,
                        style = LocalTextStyle.current.copy(
                            shadow = Shadow(
                                DarkGray,
                                offset = Offset(1f, 1f),
                                blurRadius = 4f
                            )
                        )
                    )
                    val achievement = remember {
                        listOf(
                            Achievement(System.currentTimeMillis(), "회원가입", type = 1),
                            Achievement(System.currentTimeMillis(), "자기소개서 1회 작성 완료 !", type = 2)
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(100.dp, 150.dp)
                            .shadow(
                                3.dp,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .background(
                                color = White,
                                shape = RoundedCornerShape(5.dp)
                            ),
                        contentPadding = PaddingValues(spacing.small),
                        verticalArrangement = Arrangement.spacedBy(
                            spacing.extraSmall,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.Start
                    ) {

                        items(achievement) {
                            Text(
                                "${it.timeToString()} - ${it.text}",
                                color = DarkGray,
                                fontSize = 13.sp
                            )
                        }
                    }

                }

            }

            Image(
                painter = painterResource(id = R.drawable.ic_trophy),
                contentDescription = null,
                modifier = Modifier
                    .size(
                        screenWidth
                            .div(5)
                            .times(2)
                    )
                    .align(Alignment.TopEnd)
                    .padding(start = spacing.medium)
                    .offset(y = (-50).dp)
            )

        }
    }

}

@Composable
private fun MyTopicAndTodayQuiz(
    modifier: Modifier = Modifier,
    navController: NavController,
    topicsState: Resource<List<Topic>>,
    todayAttendanceQuiz: Resource<TodayAttendanceQuiz>
) {

    val spacing = LocalSpacing.current

    val context = LocalContext.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = Black
        )
    ) {

        when (topicsState) {
            is Resource.Error -> {
                AlertUtils.showToast(context, "네트워크 오류")
                ErrorScreen(topicsState.throwableOrNull()?.message)
            }
            Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {
                val topicList = topicsState.data

                Column(
                    modifier = modifier.padding(horizontal = spacing.medium),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "나의 관심주제",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            modifier = Modifier.clickable {
                                navController.navigate(ROUTE_TOPIC) {

                                }
                            },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("변경하기", fontSize = 16.sp, color = Gray)
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Gray
                            )
                        }


                    }

                    Spacer(modifier = Modifier.height(spacing.small))

                    Column(
                        modifier = Modifier
                            .shadow(3.dp, shape = RoundedCornerShape(5.dp))
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .fillMaxWidth()
                            .padding(spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(spacing.medium)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("면접에 나올만한 질문들을\n하루에 하나씩 대비해보세요 !", color = Gray, fontSize = 16.sp)
                            Image(
                                painter = painterResource(id = R.drawable.ic_quiz),
                                contentDescription = null,
                                modifier = Modifier.height(60.dp)
                            )
                        }

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(topicList.filter { it.selected }) {
                                Box(
                                    modifier = Modifier
                                        .shadow(
                                            2.dp,
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFFE3EDFF),
                                                    Color.White
                                                )
                                            ),
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                        .border(
                                            border = BorderStroke(
                                                1.dp,
                                                brush = Brush.linearGradient(
                                                    colors = listOf(
                                                        bright_sky_blue,
                                                        bright_violet
                                                    )
                                                )
                                            ),
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                        .padding(vertical = 5.dp, horizontal = 10.dp)
                                ) {
                                    Text("#${it.name}", fontSize = 15.sp)
                                }
                            }
                        }



                        when (todayAttendanceQuiz) {
                            is Resource.Error -> {
                                ErrorScreen(todayAttendanceQuiz.error?.message)
                            }
                            Resource.Loading -> {
                                LoadingScreen()
                            }
                            is Resource.Success -> {
                                TodayQuestionCard(
                                    question = todayAttendanceQuiz.data.question,
                                    questionUUID = todayAttendanceQuiz.data.questionUUID,
                                    isPresentToday = todayAttendanceQuiz.data.isPresentToday,
                                    modifier = Modifier.fillMaxWidth(),
                                    navController = navController
                                )
                            }
                        }

                    }

                }
            }
        }

    }


}

@Composable
private fun AttendanceCheck(
    modifier: Modifier = Modifier,
    weekAttendanceInfo: Resource<WeekAttendanceInfo>
) {

    val spacing = LocalSpacing.current


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = Black
        )
    ) {
        when (weekAttendanceInfo) {
            is Resource.Error -> {
                ErrorScreen(weekAttendanceInfo.error?.message)
            }
            Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {
                Column(
                    modifier = modifier
                        .padding(horizontal = spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.small)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Weekly 출석 !", fontSize = 20.sp, fontWeight = FontWeight.Medium)

                        if (weekAttendanceInfo.data.continuousCount > 0) {
                            Text(
                                "연속 ${weekAttendanceInfo.data.continuousCount}일 출석 !",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Gray
                            )
                        }

                    }


                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, shape = RoundedCornerShape(5.dp))
                            .background(
                                color = White,
                                shape = RoundedCornerShape(5.dp)
                            ),
                        horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                        contentPadding = PaddingValues(
                            start = spacing.small,
                            end = spacing.small,
                            top = spacing.small
                        )
                    ) {
                        items(weekAttendanceInfo.data.weekAttendance) {
                            ItemAttendance(weekItem = it)
                        }
                    }


                    Text(
                        "오늘은 ${
                            SimpleDateFormat(
                                "yyyy.MM.dd (E)",
                                Locale.getDefault()
                            ).format(System.currentTimeMillis())
                        }",
                        fontSize = 14.sp,
                        color = text_blue
                    )

                }

            }
        }


    }
}

@Composable
private fun ItemAttendance(weekItem: WeekItem) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = White,
            fontWeight = FontWeight(550)
        )
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .background(
                        color = bg_darker_gray,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(weekItem.dayOfWeek.value)
            }

            weekItem.isPresent?.let { isPresent->
                Image(
                    painter = painterResource(id = if (isPresent) R.drawable.ic_check_att else R.drawable.ic_close_att),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(40.dp)
                )
            }



        }


    }
}

@Composable
private fun ScriptAndInfoContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val spacing = LocalSpacing.current

    val authViewModel: AuthViewModel = hiltViewModel()

    val userName = remember(authViewModel) {
        authViewModel.currentUser?.displayName ?: ""
    }

    CompositionLocalProvider(
        LocalTextStyle provides androidx.compose.ui.text.TextStyle(
            fontFamily = nexonFont,
            color = Black
        )
    ) {
        ConstraintLayout(
            modifier = modifier
        ) {

            val (boxScriptRef, boxInfoRef, imageRef, scriptContentRef, infoContentRef) = createRefs()


            Box(
                modifier = Modifier
                    .constrainAs(boxScriptRef) {
                        start.linkTo(parent.start, margin = spacing.medium)
                        end.linkTo(boxInfoRef.start, margin = spacing.small)
                        top.linkTo(parent.top)
                        bottom.linkTo(imageRef.bottom, margin = spacing.extraSmall)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .shadow(4.dp, shape = RoundedCornerShape(5.dp))
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(5.dp)
                    ),
            ) {

            }

            Box(
                modifier = Modifier
                    .constrainAs(boxInfoRef) {
                        start.linkTo(boxScriptRef.end, margin = spacing.small)
                        end.linkTo(parent.end, margin = spacing.medium)

                        top.linkTo(parent.top)
                        bottom.linkTo(boxScriptRef.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .shadow(5.dp, shape = RoundedCornerShape(5.dp))
                    .gradientBackground(
                        colors = listOf(
                            bright_blue,
                            bright_purple
                        ),
                        angle = 45f
                    )
                    .shimmerEffect(1500)

            ) {

            }

            Column(
                modifier = Modifier
                    .constrainAs(scriptContentRef) {
                        start.linkTo(boxScriptRef.start, margin = spacing.small)
                        top.linkTo(boxScriptRef.top, margin = spacing.small)
                        width = Dimension.wrapContent
                        height = Dimension.wrapContent
                    }
                    .padding(spacing.small)
                    .clickable {
                        navController.navigate(ROUTE_SCRIPT_WRITING)
                    },
                verticalArrangement = Arrangement.spacedBy(
                    spacing.small,
                    Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.Start
            ) {
                Text("자기소개서 작성", fontWeight = FontWeight.Medium, fontSize = 20.sp)
                Text("AI가 개인 맞춤 질문을\n만들어드려요", fontSize = 15.sp, color = Gray)
            }

            Column(
                modifier = Modifier
                    .constrainAs(infoContentRef) {
                        start.linkTo(boxInfoRef.start)
                        end.linkTo(boxInfoRef.end)
                        top.linkTo(boxInfoRef.top)
                        bottom.linkTo(boxInfoRef.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .padding(spacing.small),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    userName,
                    fontWeight = FontWeight(550),
                    fontSize = 18.sp,
                    textAlign = TextAlign.End,
                    color = White,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "깃허브 사용언어",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = White
                )
                GitLanguageAnalysis(

                )
            }


            Image(
                painter = painterResource(id = R.drawable.ic_script),
                contentDescription = null,
                modifier = Modifier
                    .height(spacing.image_100)
                    .constrainAs(imageRef) {
                        top.linkTo(scriptContentRef.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        width = Dimension.wrapContent
                        height = Dimension.wrapContent
                    }
            )


        }
    }


}


@Composable
private fun GitLanguageAnalysis(
    modifier: Modifier = Modifier
) {

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontWeight = FontWeight.Medium,
            color = White,
            fontSize = 12.sp
        )
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Kotlin 40%")
            Text("그 외 60%")
            Text(
                "주 언어",
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }


}


@Composable
private fun LogoAndInfoContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val spacing = LocalSpacing.current

    val context = LocalContext.current

    CompositionLocalProvider(
        LocalTextStyle provides androidx.compose.ui.text.TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Row(
            modifier = modifier
                .padding(horizontal = spacing.medium)
                .padding(top = spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Image(
                painterResource(id = R.drawable.logo3),
                contentDescription = null,
                modifier = Modifier
                    .height(30.dp)
                    .clickable {
                        stopRecordingService(context)
                    })

            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        navController.navigate(ROUTE_MY_PAGE) {}
                    }
            )
        }
    }


}

@Composable
fun ChartData() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .shadow(
                5.dp,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .clip(
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .wrapContentHeight()
            .fillMaxWidth()

    ) {
        ChartScreen()
    }
}

@Composable
fun WeeklyData() {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides androidx.compose.ui.text.TextStyle(
            fontFamily = CustomFont.nexonFont
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(spacing.medium),
        ) {

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                WithEmojiView(
                    unicode = 0x1F4C6
                ) {
                    HighlightText("Weekly 출석", fontSize = 18.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .wrapContentSize()
                        .offset(y = 5.dp)
                ) {
                    Text("이전 기록", fontSize = 14.sp)
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                    )
                }

            }



            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(
                    spacing.medium,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = spacing.small)
            ) {
                items(7) {
                    DailyData()
                }
            }
        }
    }


}

@Composable
fun DailyData(
    modifier: Modifier = Modifier
) {


    Box(
        modifier = modifier
            .shadow(4.dp, shape = CircleShape)
            .clip(CircleShape)
            .size(40.dp)
            .background(
                color = unselected_grey,
                shape = CircleShape
            )
            .border(
                BorderStroke(1.dp, color = Color.White),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            "월", style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = shadow_color,
                    offset = Offset(1f, 1f),
                    blurRadius = 8f
                )
            ), color = Color.White, fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )

    }

}


@Composable
fun CalendarScreen() {

    val currentMonth = remember {
        YearMonth.now()
    }

    val startMonth = remember {
        currentMonth.minusMonths(100)
    }

    val endMonth = remember {
        currentMonth.plusMonths(100)
    }

    val firstDayOfWeek = remember {
        firstDayOfWeekFromLocale()
    }

    val daysOfWeek = remember {
        daysOfWeek()
    }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            state.firstVisibleMonth.yearMonth.month.getDisplayName(
                java.time.format.TextStyle.SHORT,
                Locale.getDefault()
            ),
            textAlign = TextAlign.Center
        )
        DaysOfWeekTitle(daysOfWeek = daysOfWeek)
        HorizontalCalendar(
            state = state,
            dayContent = { calendarDay ->
                Day(calendarDay)
            }
        )
    }


}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(
                    java.time.format.TextStyle.SHORT,
                    Locale.getDefault()
                ),
            )
        }
    }
}

@Composable
fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(daysOfWeek.size) { idx ->
            Text(daysOfWeek[idx].name.substring(0, 3))
        }
    }
}

@Composable
fun Day(day: CalendarDay) {
    Box(
        modifier = Modifier
            .aspectRatio(1f), // This is important for square sizing!
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.date.dayOfMonth.toString())
    }
}