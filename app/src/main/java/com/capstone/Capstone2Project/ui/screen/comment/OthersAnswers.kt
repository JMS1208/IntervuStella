package com.capstone.Capstone2Project.ui.screen.comment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.compose.items
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestion
import com.capstone.Capstone2Project.data.model.fornetwork.TodayQuestionComment
import com.capstone.Capstone2Project.data.resource.DataState
import com.capstone.Capstone2Project.ui.screen.auth.AuthViewModel
import com.capstone.Capstone2Project.ui.screen.error.ErrorScreen
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.DottedShape
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    questionUUID: String,
    navController: NavController
) {

    val viewModel: OthersAnswersViewModel = hiltViewModel()

    val authViewModel: AuthViewModel = hiltViewModel()

//    val dataFlow = viewModel.othersAnswersData.collectAsStateWithLifecycle()

    val state = viewModel.state.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {

        authViewModel.currentUser?.uid?.let {
            viewModel.fetchMyComment(questionUUID, it)
            viewModel.fetchOthersComment(questionUUID, it)
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

                        CommunityContent(
                            totalComments,
                            myComment,
                            todayQuestion,
                            authViewModel.currentUser,
                            isRefreshing,
                            requestRefreshData = { questionUUID ->
                                authViewModel.currentUser?.uid?.let { hostUUID ->
                                    viewModel.refreshOthersAnswersData(questionUUID, hostUUID)
                                }

                            })
                    }

                }
            }

        }
    }


}

@Composable
private fun CommunityContent(
    totalComments: LazyPagingItems<TodayQuestionComment>?,
    myComment: TodayQuestionComment?,
    todayQuestion: TodayQuestion?,
    firebaseUser: FirebaseUser?,
    isRefreshing: Boolean?,
    requestRefreshData: (String) -> Unit
) {

    val viewModel: OthersAnswersViewModel = hiltViewModel()

    val spacing = LocalSpacing.current


    val effectFlow = viewModel.effect.collectAsState()

    val lazyListState = viewModel.state.value.scrollState ?: rememberLazyListState()

    when (effectFlow.value.dialogState) {
        is OthersAnswersViewModel.DialogState.MyCommentDialog -> {
            MyCommentDialog(
                onDismissRequest = viewModel::closeMyCommentDialog,
                postMyComment = { comment ->
                    val hostUUID = firebaseUser?.uid ?: return@MyCommentDialog
                    val questionUUID = todayQuestion?.questionUUID ?: return@MyCommentDialog
                    viewModel.sendMyComment(comment, questionUUID, hostUUID)
                },
                myComment = (effectFlow.value.dialogState as OthersAnswersViewModel.DialogState.MyCommentDialog).myComment
            )
        }

        OthersAnswersViewModel.DialogState.Nothing -> Unit
    }


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
                val hostUUID = firebaseUser?.uid ?: return@MyCommentContent
                val questionUUID = todayQuestion?.questionUUID ?: return@MyCommentContent
                val commentUUID = myComment?.commentUUID ?: return@MyCommentContent
                viewModel.changeCommentLike(hostUUID, questionUUID, commentUUID)

            },
            removeClicked = { myComment->
                val commentUUID = myComment?.commentUUID ?: return@MyCommentContent
                val hostUUID = firebaseUser?.uid ?: return@MyCommentContent
                viewModel.deleteMyComment(commentUUID, hostUUID)
            },
            modifyClicked = {myComment ->
                viewModel.openMyCommentDialog(myComment)
            },
            contentClicked = { myComment ->
                viewModel.openMyCommentDialog(myComment)
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
                },
                likeClicked = { commentUUID ->
                    val hostUUID = firebaseUser?.uid ?: return@OthersAnswersListContent
                    val questionUUID =
                        todayQuestion?.questionUUID ?: return@OthersAnswersListContent

                    viewModel.changeCommentLike(hostUUID, questionUUID, commentUUID)

                },
                lazyListState = lazyListState
            )
        }

    }


}

@Preview(showBackground = true)
@Composable
private fun MyCommentDialogPreview() {


}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun MyCommentDialog(
    onDismissRequest: () -> Unit,
    postMyComment: (String) -> Unit,
    myComment: TodayQuestionComment?
) {

    val myCommentState = remember {
        mutableStateOf(myComment?.comment ?: "")
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


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            fontSize = 16.sp
        )
    ) {
        Dialog(
            onDismissRequest = onDismissRequest,
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
                        onDismissRequest()
                    }

            ) {


                Column(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()

                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = DarkGray),
                        verticalArrangement = Arrangement.Bottom
                    ) {

                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            color = Gray,
                            thickness = 1.dp
                        )

                        Spacer(modifier = Modifier.height(spacing.small))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing.small),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            TextField(
                                value = myCommentState.value,
                                onValueChange = {
                                    myCommentState.value = it
                                },
                                placeholder = {
                                    Text(
                                        "코멘트 입력",
                                        style = LocalTextStyle.current.copy(
                                            color = White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                },
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (myCommentState.value.isNotEmpty()) {
                                            postMyComment(myCommentState.value)
                                        }
                                        onDismissRequest()
                                    }
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = spacing.small)
                                    .border(
                                        1.dp,
                                        color = White,
                                        shape = RoundedCornerShape(5.dp)
                                    )
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
                                    focusedIndicatorColor = Transparent,
                                    unfocusedIndicatorColor = White
                                )
                            )

                            Spacer(modifier = Modifier.width(spacing.small))


                            Box(
                                modifier = Modifier.wrapContentSize(),
                                contentAlignment = Alignment.Center
                            ) {


                                Icon(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .background(
                                            color = bright_blue,
                                            shape = CircleShape
                                        )
                                        .border(
                                            width = (0.5).dp,
                                            color = White,
                                            shape = CircleShape
                                        )
                                        .padding(spacing.small)
                                        .clickable {
                                            if (myCommentState.value.isNotEmpty()) {
                                                postMyComment(myCommentState.value)
                                                onDismissRequest()
                                            } else {
                                                AlertUtils.showToast(context, "입력한 코멘트가 없어요")
                                            }
                                        },
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = White
                                )

                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(y = (-5).dp)
                                ) {
                                    AnimatedVisibility(
                                        visible = myCommentState.value.isNotEmpty(),
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically(
                                            shrinkTowards = Alignment.Bottom
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(spacing.small)
                                                .background(
                                                    color = highlight_red,
                                                    shape = CircleShape
                                                )
                                                .border(
                                                    width = (0.5).dp,
                                                    color = White,
                                                    shape = CircleShape
                                                )
                                        )
                                    }
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
private fun OthersAnswersListContent(
    todayQuestionComments: LazyPagingItems<TodayQuestionComment>,
    firebaseUser: FirebaseUser?,
    todayQuestion: TodayQuestion?,
    isRefreshing: Boolean,
    requestRefreshData: (String) -> Unit,
    likeClicked: (String) -> Unit,
    lazyListState: LazyListState
) {
    val spacing = LocalSpacing.current



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
                                        OthersAnswerItemContent(
                                            it,
                                            likeClicked = likeClicked
                                        )

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.large)
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
private fun OthersAnswerItemContent(
    todayQuestionComment: TodayQuestionComment,
    likeClicked: (String) -> Unit
) {

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
                    likeClicked(todayQuestionComment.commentUUID)
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
    removeClicked: (TodayQuestionComment?) -> Unit,
    modifyClicked: (TodayQuestionComment?) -> Unit,
    contentClicked: (TodayQuestionComment?) -> Unit
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
                    .padding(spacing.small)
                    ,
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
                        removeClicked(myComment)
                    }
                )
                Text("수정",
                    style = LocalTextStyle.current.copy(
                        color = text_blue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.clickable {
                        modifyClicked(myComment)
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)

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
                            .fillMaxWidth()
                            .clickableWithoutRipple {
                                contentClicked(myComment)
                            }
                        ,
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
                                    likeClicked()
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