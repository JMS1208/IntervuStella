package com.capstone.Capstone2Project.ui.screen.interview

import android.view.View
import android.widget.TextView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.FeedbackItem
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.pager.*

/*
Comment
왠지는 모르겠지만 Surface로 감싸주어야 터치 오류가 안난다
중첩 스크롤을 사용해서 그런가 싶다
*/
@OptIn(ExperimentalPagerApi::class)
@Composable
fun InterviewResultMotionScreenContent(
    interviewResult: InterviewResult,
    navController: NavController

) {


    val spacing = LocalSpacing.current

    val pagerState: PagerState = rememberPagerState()
    val modifier = remember {
        Modifier
            .fillMaxWidth()
            .background(
                color = Color(0x40FFFFFF),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x80ffffff),
                        Color.Transparent,
                        Color.Transparent,
                        Color(0x80ffffff),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
    }


    val feedbackAdapter = remember {
        FeedbackAdapter().apply {
            setInnerComposeView { index, feedbackItem ->

                FeedbackItemContent(
                    index = index,
                    feedbackItem = feedbackItem,
                    modifier = modifier
                )

            }
            setPagerComposeView {


                Column(
                    modifier = modifier.padding(spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        count = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        itemSpacing = spacing.medium,
                        contentPadding = PaddingValues(vertical = spacing.small)
                    ) { page ->
                        when (page) {
                            0 -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                                ) {
                                    Text(
                                        "감점사항",
                                        style = LocalTextStyle.current.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 20.sp
                                        )
                                    )
                                    Text(
                                        "면접동안 감지된 부정적인 비언어 행동이에요",
                                        style = LocalTextStyle.current.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 16.sp
                                        )
                                    )
                                }
                            }
                            1 -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(spacing.small)
                                ) {
                                    Text(
                                        "부정적인 표정",
                                        style = LocalTextStyle.current.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 20.sp
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(spacing.small))

                                    for (i in InterviewResult.badExpressionsList().indices) {
                                        val badExpression = InterviewResult.badExpressionsList()[i]
                                        val count = interviewResult.badExpressions[i]
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = spacing.medium),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    modifier = Modifier.width(50.dp),
                                                    text = badExpression,
                                                    style = LocalTextStyle.current.copy(
                                                        color = Color.White,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 15.sp,
                                                        textAlign = TextAlign.Start
                                                    )
                                                )

                                                Spacer(modifier = Modifier.width(spacing.medium))

                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            shape = RoundedCornerShape(50),
                                                            color = Color(
                                                                (0..255).random(),
                                                                (0..255).random(),
                                                                (0..255).random()
                                                            )
                                                        )
                                                        .height(10.dp)
                                                        .width(
                                                            min(100.dp, count * (15.dp))
                                                        )
                                                ) {}
                                            }

                                            Text(
                                                "${count}회",
                                                style = LocalTextStyle.current.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = 15.sp
                                                )
                                            )
                                        }

                                    }


                                }
                            }
                            2 -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                                ) {
                                    Text(
                                        "좋지 못한 자세",
                                        style = LocalTextStyle.current.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 20.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(spacing.small))

                                    for (i in InterviewResult.badPosesList().indices) {
                                        val badPose = InterviewResult.badPosesList()[i]
                                        val count = interviewResult.badPoses[i]
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = spacing.medium),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    modifier = Modifier.width(50.dp),
                                                    text = badPose,
                                                    style = LocalTextStyle.current.copy(
                                                        color = Color.White,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 15.sp,
                                                        textAlign = TextAlign.Start
                                                    )
                                                )

                                                Spacer(modifier = Modifier.width(spacing.medium))

                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            shape = RoundedCornerShape(50),
                                                            color = Color(
                                                                (0..255).random(),
                                                                (0..255).random(),
                                                                (0..255).random()
                                                            )
                                                        )
                                                        .height(10.dp)
                                                        .width(
                                                            min(100.dp, count * (15.dp))
                                                        )
                                                ) {}
                                            }

                                            Text(
                                                "${count}회",
                                                style = LocalTextStyle.current.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = 15.sp
                                                )
                                            )
                                        }

                                    }
                                }
                            }
                        }
                    }

                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        activeColor = highlight_yellow,
                        inactiveColor = Color.White,
                        indicatorShape = CircleShape
                    )
                }
            }
        }
    }

    var motionEnded by remember {
        mutableStateOf(false)
    }


    AndroidView(
        factory = { context ->
            View.inflate(context, R.layout.interview_result_motion_screen, null)
        },
        update = {
            val motionLayoutAuto = it.findViewById<MotionLayout>(R.id.motionLayout)

            motionLayoutAuto.setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int
                ) {
                }

                override fun onTransitionChange(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int,
                    progress: Float
                ) = Unit

                override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

                    when (currentId) {
                        R.id.start_auto -> {
                            motionLayout?.transitionToEnd()
                        }
                        R.id.end_auto -> {
                            motionLayout?.progress = 0f
                            motionLayout?.transitionToStart()
                        }
                    }

                }

                override fun onTransitionTrigger(
                    motionLayout: MotionLayout?,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) = Unit

            })

            val cvBackground = it.findViewById<ComposeView>(R.id.cv_background)

            cvBackground.setContent {

                val startColor by animateColorAsState(
                    targetValue = if (motionEnded) bright_blue else darker_blue,
                    animationSpec = tween(
                        1000
                    )
                )

                val endColor by animateColorAsState(
                    targetValue = if (motionEnded) bright_violet else Color.Black,
                    animationSpec = tween(
                        2000
                    )
                )


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                0.0f to startColor,
                                500.0f to endColor,
                                start = Offset.Zero,
                                end = Offset.Infinite
                            )
                        )
                ) {

                }
            }

            val tvRank = it.findViewById<TextView>(R.id.tv_rank)

            val tvDeduction = it.findViewById<TextView>(R.id.tv_deduction)

            val tvTotalDuration = it.findViewById<TextView>(R.id.tv_total_duration)

            tvRank.text = interviewResult.rank
            tvDeduction.text =
                "감점사항 ${interviewResult.badPoses.size + interviewResult.badExpressions.size}개"
            tvTotalDuration.text = "소요시간 ${interviewResult.totalDurationToString()}"


            val rvFeedback = it.findViewById<RecyclerView>(R.id.rv_feedback)

            rvFeedback.adapter = feedbackAdapter

            feedbackAdapter.submitList(interviewResult.feedbackList)

            rvFeedback.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    motionEnded = dy > 0
                    /*
                    모션레이아웃의 콜백함수는 호출이 안될때가 많아서 이걸로 씀
                     */
                }
            })

            val tvMove = it.findViewById<TextView>(R.id.tv_move)

            tvMove.setOnClickListener {
                navController.navigate(ROUTE_HOME) {
                    popUpTo(ROUTE_HOME) {
                        inclusive = true
                    }
                }
            }
        }
    )
}

@Composable
fun FeedbackItemContent(
    index: Int,
    feedbackItem: FeedbackItem,
    modifier: Modifier = Modifier
) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont,
            color = Color.White
        )
    ) {


        Column(
            modifier = modifier
                .padding(vertical = spacing.medium, horizontal = spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.small)
                    .background(
                        color = Color(0x45000000),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(spacing.medium)
            ) {

                Text(
                    "${index + 1}.${feedbackItem.question}",
                    style = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = CustomFont.nexonFont,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                Text(
                    feedbackItem.answer,
                    style = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = CustomFont.nexonFont,
                        fontSize = 14.sp
                    )
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.small),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "Feedback",
                    style = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = CustomFont.nexonFont,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    feedbackItem.feedback,
                    style = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = CustomFont.nexonFont,
                        fontSize = 15.sp,
                        textAlign = TextAlign.End
                    )
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = text_red,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("(${feedbackItem.durationToString()} 소요) ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("Warning")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    feedbackItem.durationWarning,
                    style = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = CustomFont.nexonFont,
                        fontSize = 15.sp,
                        textAlign = TextAlign.End
                    )
                )

            }
        }

    }
}