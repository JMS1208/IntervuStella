package com.capstone.Capstone2Project

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionLayout.TransitionListener
import androidx.constraintlayout.widget.ConstraintSet.Motion
import androidx.navigation.compose.rememberNavController
import androidx.recyclerview.widget.RecyclerView
import com.capstone.Capstone2Project.data.model.FeedbackItem
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.databinding.InterviewResultMotionScreenBinding
import com.capstone.Capstone2Project.ui.screen.interesting.topic.TopicViewModel
import com.capstone.Capstone2Project.ui.screen.interview.FeedbackAdapter
import com.capstone.Capstone2Project.ui.screen.interview.InterviewResultMotionScreenContent
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviewActivity : BaseActivity() {

    private val viewModel by viewModels<TopicViewModel>()

    private val binding by lazy {
        InterviewResultMotionScreenBinding.inflate(layoutInflater)
    }

    private lateinit var feedbackAdapter: FeedbackAdapter

    var motionProgress = 0f

    @OptIn(ExperimentalPagerApi::class)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setBaseContent {

            InterviewResultMotionScreenContent(
                InterviewResult.createTestInterviewResult(),
                rememberNavController()
            )

        }

//        setBaseContent {
//
//            OthersAnswersScreen("uuid", rememberNavController())
//        }
        /*
        setContentView(binding.root)

        val textStyle = TextStyle(
            fontFamily = nexonFont,
            fontSize = 16.sp,
            color = Color.White
        )

        feedbackAdapter = FeedbackAdapter().apply {
            setInnerComposeView { index, feedbackItem ->

                FeedbackItemContent(index = index, feedbackItem = feedbackItem)

            }
            setPagerComposeView {
                Box(
                    modifier = Modifier
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
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("dddd", color = Color.White)
                }
            }
        }

        binding.rvFeedback.apply {
            adapter = feedbackAdapter
        }

        binding.tvMove.text = "홈화면 이동"
        binding.tvDeduction.text = "감점사항 9개"
        binding.tvRank.text = "S"
        binding.tvTotalDuration.text = "소요시간 10분 40초"



        binding.motionLayout.apply {
            setTransitionListener(object : MotionLayout.TransitionListener {
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
                ) {
                }

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
                ) {
                }

            })
        }




        binding.motionLayoutTouch.setTransitionListener(object : TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) = Unit

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                motionProgress = progress
                Log.e("TAG", "onTransitionChange: $motionProgress")
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) = Unit

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) = Unit

        })


        binding.cvBackground.setContent {

            //TODO remember로 바꿔야함
            val startColor by animateColorAsState(
                targetValue = if (motionProgress > 0.90f) bright_blue else darker_blue
            )

            val endColor by animateColorAsState(
                targetValue = if (motionProgress > 0.90f) bright_violet else Color.Black
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

        val feedbackItemList = mutableListOf<FeedbackItem>()

        for (i in 0 until 5) {
            feedbackItemList.add(FeedbackItem.createTestFeedbackItem())
        }

        feedbackAdapter.submitList(
            feedbackItemList
        )

    }

         */
    }

}


