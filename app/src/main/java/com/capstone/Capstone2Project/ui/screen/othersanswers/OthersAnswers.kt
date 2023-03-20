package com.capstone.Capstone2Project.ui.screen.othersanswers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.MoveUp
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.util.TableInfo
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.DottedShape
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OthersAnswersScreen(
    questionUUID: String,
    navController: NavController
) {

    val viewModel: OthersAnswersViewModel = hiltViewModel()

    val dataFlow = viewModel.othersAnswersData.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.fetchData(questionUUID)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Text(
                        "전체 답글",
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
                        tint = DarkGray,
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
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    color = bg_grey
                )
        ) {

            dataFlow.value?.let {
                when (it) {
                    is Resource.Error -> {
                        navController.popBackStack()
                        AlertUtils.showToast(context, "네트워크 오류입니다")
                    }
                    Resource.Loading -> LoadingScreen()
                    is Resource.Success -> {
                        OthersAnswersContent(it.data, questionUUID)
                    }
                }
            }


        }
    }


}

@Composable
private fun OthersAnswersContent(data: OthersAnswersData, questionUUID: String) {

    val viewModel: OthersAnswersViewModel = hiltViewModel()

    val spacing = LocalSpacing.current


    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        QuestionContent(
            data.questionData
        )


        MyAnswerContent(
            data.myAnswer,
            likeClicked = { ad, like ->
                viewModel.changeMyAnswerLike(ad, like)
            },
            removeClicked = {

            },
            modifyClicked = {

            },
            contentClicked = {

            }
        )

        Spacer(modifier = Modifier.height(spacing.medium))

        OthersAnswersListContent(
            data.othersAnswers,
            questionUUID
        )
    }


}

@Composable
private fun OthersAnswersListContent(othersAnswers: List<AnswerData>, questionUUID: String) {


    val spacing = LocalSpacing.current

    val lazyListState = rememberLazyListState()

    val viewModel: OthersAnswersViewModel = hiltViewModel()

    val isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle()

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp)
                    .background(White)
                    .padding(spacing.small)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Text(
                        "전체 답글",
                        style = LocalTextStyle.current.copy(
                            color = White,
                            fontWeight = FontWeight(550),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .background(
                                color = bright_blue,
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 20.dp, vertical = 3.dp)
                    )

                    Text(
                        "답글 ${othersAnswers.size}개", style = LocalTextStyle.current.copy(
                            color = Gray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        ), modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(spacing.small)
                    )


                }

                if (othersAnswers.isNotEmpty()) {

                    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing.value)

                    val context = LocalContext.current

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {

                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = {
                                viewModel.refreshOthersAnswersData(questionUUID)
                            }
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(top = spacing.small, bottom = spacing.large, start = spacing.small, end = spacing.small),
                                state = lazyListState
                            ) {
                                items(othersAnswers) {
                                    OthersAnswerItemContent(it)
                                }
                            }
                        }


                        ScrollToTopButton(
                            lazyListState,
                            threshold = 0,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }


                } else {
                    Text(
                        "아직 등록된 답변이 없어요", style = LocalTextStyle.current.copy(
                            color = Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }



        }


    }


}


@Composable
private fun ScrollToTopButton(
    lazyListState: LazyListState,
    threshold: Int,
    modifier: Modifier
) {
    val isVisible = remember(threshold) {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    val coroutineScope = rememberCoroutineScope()

    if (isVisible.value) {
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
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = null,
                tint = White
            )
        }
    }
}

@Composable
private fun OthersAnswerItemContent(answerData: AnswerData) {

    val spacing = LocalSpacing.current

    val viewModel:OthersAnswersViewModel = hiltViewModel()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacing.small),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                answerData.nickName,
                style = LocalTextStyle.current.copy(
                    color = Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                "(${answerData.email})",
                style = LocalTextStyle.current.copy(
                    color = Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        Text(
            answerData.content,
            style = LocalTextStyle.current.copy(
                color = Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start
            ),
            maxLines = 1,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.End)
        ) {
            Icon(
                contentDescription = null,
                imageVector = if (answerData.like) Icons.Default.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
                tint = if (answerData.like) bright_blue else DarkGray,
                modifier = Modifier.clickable {
                    //myAnswer.like = !myAnswer.like
                    viewModel.changeOthersAnswerLike(answerData, !answerData.like)
                }
            )

            Text(
                answerData.likeCount.toString(),
                style = LocalTextStyle.current.copy(
                    color = Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    color = LightGray,
                    shape = DottedShape(5.dp)
                )
        )
    }
}

@Composable
private fun QuestionContent(questionData: QuestionData) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = Modifier
                .padding(spacing.medium)
                .fillMaxWidth()
                .shadow(3.dp, shape = RoundedCornerShape(10.dp))
                .background(
                    color = bright_blue,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterVertically)
        ) {

            Box(
                modifier = Modifier
                    .shadow(3.dp, RoundedCornerShape(50))
                    .background(color = White, RoundedCornerShape(50))
                    .padding(horizontal = 15.dp, vertical = 3.dp)
            ) {
                Text(
                    questionData.field, style = LocalTextStyle.current.copy(
                        color = bright_blue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_quotation_left),
                    contentDescription = null,
                    tint = White
                )

                Text(
                    questionData.question, style = LocalTextStyle.current.copy(
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_quotation_right),
                    contentDescription = null,
                    tint = White
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))
        }

    }


}


@Composable
private fun MyAnswerContent(
    myAnswer: AnswerData?,
    likeClicked: (AnswerData, Boolean) -> Unit,
    removeClicked: () -> Unit,
    modifyClicked: () -> Unit,
    contentClicked: () -> Unit
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
                .shadow(3.dp)
                .background(
                    color = White
                )
                .padding(spacing.small)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(spacing.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                Text("삭제",
                    style = LocalTextStyle.current.copy(
                        color = text_blue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.clickable {
                        removeClicked()
                    }
                )
                Text("수정",
                    style = LocalTextStyle.current.copy(
                        color = text_blue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.clickable {
                        modifyClicked()
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .clickableWithoutRipple {
                        contentClicked()
                    }
            ) {
                Text(
                    "나의 답글",
                    style = LocalTextStyle.current.copy(
                        color = White,
                        fontWeight = FontWeight(550),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(
                            color = Gray,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 20.dp, vertical = 3.dp)

                )

                if (myAnswer == null) {

                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "위 질문에 대한 나의 답변을 달아보세요", style = LocalTextStyle.current.copy(
                                color = Black,
                                fontSize = 16.sp
                            )
                        )
                    }

                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.small),
                        verticalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                myAnswer.nickName,
                                style = LocalTextStyle.current.copy(
                                    color = Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            Text(
                                "(${myAnswer.email})",
                                style = LocalTextStyle.current.copy(
                                    color = Gray,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }

                        Text(
                            myAnswer.content,
                            style = LocalTextStyle.current.copy(
                                color = Gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start
                            ),
                            maxLines = 1,
                            minLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.End)
                        ) {
                            Icon(
                                contentDescription = null,
                                imageVector = if (myAnswer.like) Icons.Default.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
                                tint = if (myAnswer.like) bright_blue else DarkGray,
                                modifier = Modifier.clickable {
                                    //myAnswer.like = !myAnswer.like
                                    //TODO(뷰모델처리)
                                    likeClicked(myAnswer, !myAnswer.like)
                                }
                            )

                            Text(
                                myAnswer.likeCount.toString(),
                                style = LocalTextStyle.current.copy(
                                    color = Gray,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                    }

                }
            }


        }
    }

}