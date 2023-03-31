package com.capstone.Capstone2Project.ui.screen.interesting.topic

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.capstone.Capstone2Project.data.model.Topic
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.navigation.ROUTE_TOPIC
import com.capstone.Capstone2Project.ui.screen.auth.AuthViewModel
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.HighlightText
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.extensions.EmojiView
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow

@Preview(showBackground = true)
@Composable
fun TopicScreenPreview() {

}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(
    navController: NavController,

    //authViewModel: AuthViewModel = hiltViewModel()
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    val topicViewModel: TopicViewModel = hiltViewModel()

    LaunchedEffect(authViewModel.currentUser) {
        authViewModel.currentUser?.uid?.let {
            topicViewModel.fetchUserTopics(it)
        }
    }

    val context = LocalContext.current

    val userTopics = topicViewModel.userTopicsFlow.collectAsStateWithLifecycle()

    userTopics.value?.let {
        when(it) {
            is Resource.Error -> {
                it.error?.message?.let { message ->
                    AlertUtils.showToast(context, message, Toast.LENGTH_LONG)
                }
            }
            Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {
                InterestingTopicContent(
                    navController = navController,
                    topicList = it.data
                )
            }
        }

    }


}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestingTopicContent(
    navController: NavController,
    topicList: List<Topic>,
) {

    val scrollState = rememberScrollState()

    val spacing = MaterialTheme.spacing

    val authViewModel: AuthViewModel = hiltViewModel()

    val viewModel: TopicViewModel = hiltViewModel()



    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            color = Color.DarkGray,
            fontFamily = CustomFont.nexonFont,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Text(
                            "관심영역 설정",
                            style = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
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
                    },

                    )
            },

            ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        scrollState
                    )
                    .padding(innerPadding)
                    .padding(spacing.medium),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TopicHeader()



                FlowItems(
                    modifier = Modifier
                        .background(
                            color = bg_grey,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = spacing.medium, vertical = spacing.extraMedium)
                    ,
                    topicList
                ) { topic->
                    viewModel.changeSelectedTopic(topic)
                }




                TopicFooter(
                    topicList
                ) {

                    authViewModel.currentUser?.uid?.let {
                        viewModel.postUserTopics(it, topicList)
                    }

                    navController.navigate(ROUTE_HOME) {
                        popUpTo(ROUTE_TOPIC) {
                            inclusive = true
                        }
                    }
                }

            }


        }
    }
}

@Composable
fun TopicFooter(
    selectedTopics: List<Topic>,
    onClickListener: () -> Unit
) {


    val spacing = LocalSpacing.current

    val nextText = if (selectedTopics.isNotEmpty()) "선택 완료" else "다음에 선택할게요"

    val selectedCount = remember(selectedTopics) {
        selectedTopics.filter {
            it.selected
        }.size
    }


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.medium, start = spacing.small)
            ) {
                Text("선택한 영역 $selectedCount 개")
            }


            Spacer(modifier = Modifier.height(spacing.large))

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableWithoutRipple {
                        onClickListener()
                    }
            ) {
                Text(
                    nextText,
                    textAlign = TextAlign.End,
                    fontSize = 16.sp,
                    color = text_blue,
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = text_blue
                )
            }


        }

    }


}

@Preview(showBackground = true)
@Composable
fun TopicHeader() {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkGray
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = spacing.medium,
                    bottom = spacing.medium
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EmojiView(0x1F4CC)
            Spacer(Modifier.width(spacing.small))
            HighlightText(
                "관심주제를 선택하고, 관련 질문을 연습해보세요 !"
            )
        }
    }

}


@Composable
fun FlowItems(
    modifier: Modifier = Modifier,
    topicList: List<Topic>,
    itemClicked: (Topic) -> Unit
) {

    val spacing = LocalSpacing.current

    FlowRow(
        crossAxisSpacing = spacing.small,
        mainAxisSpacing = spacing.small,
        modifier = modifier.fillMaxWidth(),
        mainAxisAlignment = FlowMainAxisAlignment.SpaceAround,
        lastLineMainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly
    ) {
        topicList.forEach {
            FlowItemContent(
                it,
                itemClicked = {
                    itemClicked(it)
                }
            )
        }
    }

}


@Composable
fun FlowItemContent(
    topic: Topic,
    itemClicked: () -> Unit, //클릭했을때 전달받을것도 나중에 바꿔야함 지금은 String으로 줌
    unselectedColor: Color = Color.White,
    selectedColor: Color = bright_blue,
    cornerRadius: Dp = 15.dp
) {
//    var selected by remember {
//        mutableStateOf(false)
//    }

    val itemColor = animateColorAsState(
        targetValue = if (topic.selected) selectedColor else unselectedColor
    )

    val textColor = remember(topic.selected) {
        if(topic.selected) White else DarkGray
    }

    val spacing = LocalSpacing.current

    Row(
        modifier = Modifier
            .clickableWithoutRipple {
                //topic.selected = !topic.selected
                itemClicked()
            }
            .shadow(
                1.dp,
                shape = RoundedCornerShape(50)
            )
            .clip(
                RoundedCornerShape(50)
            )
            .background(
                color = itemColor.value,
                shape = RoundedCornerShape(50)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        AnimatedVisibility(visible = topic.selected) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White
                )
            }

        }

        Text(
            topic.name,
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 8.dp),
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = CustomFont.nexonFont,
            textAlign = TextAlign.Center
        )


    }
}

