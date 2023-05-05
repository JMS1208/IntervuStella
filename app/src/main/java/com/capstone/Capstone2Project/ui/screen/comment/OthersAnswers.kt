package com.capstone.Capstone2Project.ui.screen.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestion
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestionComment
import com.capstone.Capstone2Project.data.resource.DataState
import com.capstone.Capstone2Project.ui.screen.auth.AuthViewModel
import com.capstone.Capstone2Project.ui.screen.error.ErrorScreen
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.DottedShape
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OthersAnswersScreen(
    questionUUID: String,
    navController: NavController
) {

    val viewModel: OthersAnswersViewModel = hiltViewModel()

    val authViewModel: AuthViewModel = hiltViewModel()

//    val dataFlow = viewModel.othersAnswersData.collectAsStateWithLifecycle()

    val state = viewModel.state.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.fetchOthersComment(questionUUID)
        authViewModel.currentUser?.uid?.let{
            viewModel.fetchMyComment(questionUUID, it)
        }
        viewModel.fetchTodayQuestion(questionUUID)
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

            when (state.value.dataState) {
                is DataState.Error -> {
                    ErrorScreen((state.value.dataState as DataState.Error).message)
                }

                DataState.Loading -> {
                    LoadingScreen()
                }

                DataState.Normal -> {
                    with(state.value) {
                        val totalComments = totalComments?.flow?.collectAsLazyPagingItems()

                        OthersCommentsContent(
                            totalComments,
                            myComment,
                            todayQuestion,
                            authViewModel.currentUser,
                            isRefreshing,
                            requestRefreshData = {
                                viewModel.refreshOthersAnswersData(it)
                            })
                    }

                }
            }

        }
    }


}

@Composable
private fun OthersCommentsContent(
    totalComments: LazyPagingItems<TodayQuestionComment>?,
    myComment: TodayQuestionComment?,
    todayQuestion: TodayQuestion?,
    firebaseUser: FirebaseUser?,
    isRefreshing: Boolean?,
    requestRefreshData: (String) -> Unit
) {

//    val viewModel: OthersAnswersViewModel = hiltViewModel()

    val spacing = LocalSpacing.current


    Column(
        modifier = Modifier.fillMaxSize()
    ) {


        todayQuestion?.let {
            QuestionContent(
                it
            )
        }


        MyCommentContent(
            myComment,
            firebaseUser = firebaseUser,
            likeClicked = {
            },
            removeClicked = {

            },
            modifyClicked = {

            },
            contentClicked = {

            }
        )


        Spacer(modifier = Modifier.height(spacing.medium))

        totalComments?.let {
            OthersAnswersListContent(
                it,
                firebaseUser = firebaseUser,
                todayQuestion = todayQuestion,
                isRefreshing = isRefreshing ?: false,
                requestRefreshData = { questionUUID ->
                    requestRefreshData(questionUUID)
                }
            )
        }

    }


}

@Composable
private fun OthersAnswersListContent(
    todayQuestionComments: LazyPagingItems<TodayQuestionComment>,
    firebaseUser: FirebaseUser?,
    todayQuestion: TodayQuestion?,
    isRefreshing: Boolean,
    requestRefreshData: (String) -> Unit
) {
    val spacing = LocalSpacing.current

    val lazyListState = rememberLazyListState()

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
                        "답글 ${todayQuestionComments.itemCount}개",
                        style = LocalTextStyle.current.copy(
                            color = Gray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(spacing.small)
                    )


                }

                if (todayQuestionComments.itemCount > 0) {

                    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

                    val context = LocalContext.current

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {

                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = {
//                                viewModel.refreshOthersAnswersData(questionUUID)
                                todayQuestion?.let {
                                    requestRefreshData(it.questionUUID)
                                }
                            }
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    top = spacing.small,
                                    bottom = spacing.large,
                                    start = spacing.small,
                                    end = spacing.small
                                ),
                                state = lazyListState
                            ) {
                                items(todayQuestionComments) { todayQuestionComment ->
                                    todayQuestionComment?.let {
                                        OthersAnswerItemContent(it)
                                    }
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
                            color = Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth().padding(vertical = spacing.large)
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
private fun OthersAnswerItemContent(todayQuestionComment: TodayQuestionComment) {

    val spacing = LocalSpacing.current

    val viewModel: OthersAnswersViewModel = hiltViewModel()

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
                todayQuestionComment.nickName,
                style = LocalTextStyle.current.copy(
                    color = Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                "(${todayQuestionComment.email})",
                style = LocalTextStyle.current.copy(
                    color = Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        Text(
            todayQuestionComment.comment,
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
                imageVector = if (todayQuestionComment.isLiked) Icons.Default.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
                tint = if (todayQuestionComment.isLiked) bright_blue else DarkGray,
                modifier = Modifier.clickable {
                    //TODO 좋아요 변경 처리
                }
            )

            Text(
                todayQuestionComment.like.toString(),
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
private fun QuestionContent(todayQuestion: TodayQuestion) {

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
                    todayQuestion.field ?: "", style = LocalTextStyle.current.copy(
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
                    todayQuestion.question, style = LocalTextStyle.current.copy(
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
private fun MyCommentContent(
    myComment: TodayQuestionComment?,
    firebaseUser: FirebaseUser?,
    likeClicked: () -> Unit,
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

                if (myComment == null) {

                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "위 질문에 대한 나의 답변을 달아보세요", style = LocalTextStyle.current.copy(
                                color = Gray,
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
                                firebaseUser?.displayName ?: "이름 없음",
                                style = LocalTextStyle.current.copy(
                                    color = Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            Text(
                                "(${firebaseUser?.email})",
                                style = LocalTextStyle.current.copy(
                                    color = Gray,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }

                        Text(
                            myComment.comment,
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
                                imageVector = if (myComment.isLiked) Icons.Default.ThumbUpAlt else Icons.Outlined.ThumbUpAlt,
                                tint = if (myComment.isLiked) bright_blue else DarkGray,
                                modifier = Modifier.clickable {
                                    //myAnswer.like = !myAnswer.like
                                    //TODO(뷰모델처리)

                                }
                            )

                            Text(
                                myComment.like.toString(),
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