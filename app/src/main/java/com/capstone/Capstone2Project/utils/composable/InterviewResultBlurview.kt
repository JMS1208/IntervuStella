package com.capstone.Capstone2Project.utils.composable

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Paint.Align
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoveUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.FeedbackItem
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.ui.screen.interview.InterviewMemoDialog
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.theme.*
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.launch


@Preview(showBackground = true)
@Composable
private fun Preview() {
    InterviewResultContent(
        interviewResult = InterviewResult.createTestInterviewResult(),
        navController = rememberNavController()
    )
}

@Composable
fun InterviewResultContent(
    interviewResult: InterviewResult,
    navController: NavController
) {

    val astronautComposition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/floating_astronaut.json"))
    val astronautProgress by animateLottieCompositionAsState(
        astronautComposition,
        iterations = LottieConstants.IterateForever
    )

    val medalComposition by rememberLottieComposition(spec = LottieCompositionSpec.Asset("lottie/medal2.json"))

    val medalProgress by animateLottieCompositionAsState(
        medalComposition,
        iterations = 1
    )
    val showScore = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(medalProgress) {
        if (medalProgress == 1.0f) {
            showScore.value = true
        }
    }

    val spacing = LocalSpacing.current

    val showMemoDialog = remember {
        mutableStateOf(false)
    }

    val scrollState = rememberScrollState()


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            BlurLayout(
                blurRadius = 5f,
                innerContent = {
                    if (showScore.value) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .fillMaxHeight(0.5f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .verticalScroll(scrollState),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                val starRateComposition by rememberLottieComposition(
                                    spec = LottieCompositionSpec.Asset(
                                        "lottie/star_rate.json"
                                    )
                                )

                                val starRateProgress by animateLottieCompositionAsState(
                                    starRateComposition,
                                    iterations = LottieConstants.IterateForever
                                )


                                LottieAnimation(
                                    composition = starRateComposition,
                                    progress = { starRateProgress },
                                    modifier = Modifier.height(50.dp)
                                )


                                RankContent(interviewResult.rank)

                                Spacer(modifier = Modifier.height(spacing.small))

                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .padding(
                                            horizontal = spacing.small
                                        )
                                        .background(
                                            shape = DottedShape(5.dp),
                                            color = White
                                        )
                                )

                                for (i in interviewResult.feedbackList.indices) {
                                    FeedbackItemContent(
                                        i,
                                        interviewResult.feedbackList[i]
                                    )

                                    if (i < interviewResult.feedbackList.lastIndex) {
                                        Spacer(modifier = Modifier.height(spacing.small))

                                        Divider(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .padding(
                                                    horizontal = spacing.small
                                                )
                                                .background(
                                                    shape = DottedShape(5.dp),
                                                    color = White
                                                )
                                        )

                                        Spacer(modifier = Modifier.height(spacing.small))

                                    }
                                }


                                Spacer(modifier = Modifier.height(spacing.small))
                            }

                            ScrollToTopButton(
                                scrollState = scrollState,
                                threshold = 0,
                                modifier = Modifier.align(Alignment.BottomCenter).size(40.dp)
                            )
                        }


                    }

                },
                backgroundContent = {

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bg_space),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )

                        LottieAnimation(
                            composition = astronautComposition,
                            progress = { astronautProgress },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-150).dp)
                        )

                        if (!showScore.value) {
                            LottieAnimation(
                                composition = medalComposition,
                                progress = { medalProgress },
                                modifier = Modifier.fillMaxSize()
                            )

                        }
                    }

                },
                bottomComposableContent = { //이쪽에 버튼

                    if (showScore.value) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(top = spacing.medium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                spacing.medium,
                                Alignment.CenterHorizontally
                            )
                        ) {

                            Row(
                                modifier = Modifier
                                    .height(40.dp)
                                    .weight(1f)
                                    .background(
                                        color = White,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        showMemoDialog.value = true
                                    }
                                    .padding(horizontal = spacing.medium, vertical = spacing.small),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    spacing.small,
                                    Alignment.CenterHorizontally
                                )
                            ) {
                                Icon(
                                    contentDescription = null,
                                    tint = bright_blue,
                                    painter = painterResource(id = R.drawable.ic_pencil)
                                )

                                Text(
                                    "메모 기록", style = LocalTextStyle.current.copy(
                                        color = bright_blue,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = nexonFont
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .height(40.dp)
                                    .weight(1f)
                                    .background(
                                        color = bright_blue,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        navController.navigate(ROUTE_HOME) {
                                            popUpTo(ROUTE_HOME) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                    .padding(horizontal = spacing.medium, vertical = spacing.small),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    spacing.small,
                                    Alignment.CenterHorizontally
                                )
                            ) {
                                Icon(
                                    contentDescription = null,
                                    tint = White,
                                    imageVector = Icons.Default.Home
                                )

                                Text(
                                    "홈화면 이동", style = LocalTextStyle.current.copy(
                                        color = White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = nexonFont
                                    )
                                )
                            }
                        }
                    }
                },
                bottomInnerContent = { // 이쪽에 감점사항
                    DeductionListContent(interviewResult)
                },
                showBlurLayout = showScore.value
            )



            if (showMemoDialog.value) {
                InterviewMemoDialog(
                    interviewUUID = interviewResult.interviewUUID,
                    dismissClick = {
                        showMemoDialog.value = false
                    })
            }

        }

    }
}

@Composable
private fun ScrollToTopButton(
    scrollState: ScrollState,
    threshold: Int,
    modifier: Modifier) {

    val isVisible by remember(threshold) {
        derivedStateOf { scrollState.value > 0 }
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
                        scrollState.animateScrollTo(
                            0
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
                backgroundColor = bright_blue
            ) {
                Icon(
                    contentDescription = null,
                    imageVector = Icons.Default.ArrowUpward,
                    tint = White
                )
            }
        }

    }

}


@Composable
private fun DeductionListContent(interviewResult: InterviewResult) {

    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(spacing.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(
                        shape = DottedShape(5.dp),
                        color = White
                    )
            )

            Text(
                "감점사항",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontFamily = nexonFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        shape = CircleShape,
                        color = bright_blue
                    )
            ) {}

            Spacer(modifier = Modifier.width(spacing.small))

            Text(
                "부정적인 표정",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontFamily = nexonFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )


        }

        Spacer(modifier = Modifier.height(spacing.small))

        Text(
            interviewResult.badExpressionsToString(),
            style = LocalTextStyle.current.copy(
                color = White,
                fontFamily = nexonFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(modifier = Modifier.height(spacing.medium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        shape = CircleShape,
                        color = bright_purple
                    )
            ) {}

            Spacer(modifier = Modifier.width(spacing.small))

            Text(
                "좋지 못한 자세",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontFamily = nexonFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )


        }

        Spacer(modifier = Modifier.height(spacing.small))

        Text(
            interviewResult.badPosesToString(),
            style = LocalTextStyle.current.copy(
                color = White,
                fontFamily = nexonFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(modifier = Modifier.height(spacing.small))
    }
}

@Composable
fun FeedbackItemContent(index: Int, feedbackItem: FeedbackItem) {

    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.small),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.small)
                .background(
                    color = Color(0x51000000)
                )
                .padding(spacing.small)
        ) {

            Text(
                "${index + 1}.${feedbackItem.question}",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = nexonFont,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                feedbackItem.answer,
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = nexonFont,
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
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = nexonFont,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                feedbackItem.feedback,
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = nexonFont,
                    fontSize = 15.sp
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
                            color = White,
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
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = nexonFont,
                    fontSize = 15.sp
                )
            )

        }

    }

}

@Composable
private fun RankContent(rank: String) {

    val ranking = listOf("A", "B", "C", "D", "S")

    val spacing = LocalSpacing.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.small),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append("Rank - ")
                }

                for (i in ranking.indices) {
                    withStyle(
                        style = SpanStyle(
                            color = if (ranking[i] == rank) highlight_yellow else White,
                            fontSize = if (ranking[i] == rank) 30.sp else 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(ranking[i])
                    }

                    if (i < ranking.lastIndex) {
                        withStyle(
                            style = SpanStyle(
                                color = White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(" / ")
                        }
                    }

                }


            }
        )
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.body1
) {

    val oldCount by rememberUpdatedState(count)

//    SideEffect { //외부값 count가 바뀌면 변경되는
//        Log.d("TAG", "테스트: $count")
//        //oldCount = count
//    }

    val spacing = LocalSpacing.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.small, Alignment.CenterHorizontally)
    ) {
        val countString = count.toString()
        val oldCountString = oldCount.toString()

        for (i in countString.indices) {
            val oldChar = oldCountString.getOrNull(i)
            val newChar = countString[i]

            val char = if (oldChar == newChar) {
                oldCountString[i]
            } else {
                countString[i]
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .shadow(
                        4.dp,
                        ambientColor = White,
                        spotColor = White
                    )
                    .border(
                        2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Gray,
                                Color.Transparent
                            )
                        ),
                        shape = RectangleShape
                    )
                    .background(
                        color = White
                    )
                    .padding(spacing.medium)
            ) {
                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        slideInVertically {
                            it * 2
                        } with slideOutVertically {
                            -it * 2
                        }
                    }
                ) {


                    Text(
                        text = it.toString(),
                        style = style,
                        softWrap = false,
                    )
                }

            }
        }
    }
}

@Composable
fun BlurLayout(
    modifier: Modifier = Modifier,
    blurRadius: Float,
    blurOverlayColor: Int = R.color.blurOverlayColor,
    innerContent: @Composable () -> Unit,
    backgroundContent: @Composable () -> Unit,
    bottomComposableContent: @Composable () -> Unit,
    bottomInnerContent: @Composable () -> Unit,
    showBlurLayout: Boolean
) {

    val context = LocalContext.current

    AndroidView(
        factory = {
            View.inflate(it, R.layout.interview_result_screen, null)
        },
        modifier = modifier,
        update = {
            val blurView = it.findViewById<BlurView>(R.id.blur_view)

            val root = it.findViewById<ViewGroup>(R.id.root)

            val decorView = getActivityDecorView(context)

            val windowBackground = decorView?.background

            blurView.setupWith(root, getBlurAlgorithm(context)) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(blurRadius)
                .setOverlayColor(
                    ContextCompat.getColor(context, blurOverlayColor)
                )

            blurView.apply {
                outlineProvider = ViewOutlineProvider.BACKGROUND
                clipToOutline = true
                visibility = if (showBlurLayout) View.VISIBLE else View.GONE
            }

            val bottomBlurView = it.findViewById<BlurView>(R.id.bottom_blur_view)

            bottomBlurView.setupWith(root, getBlurAlgorithm(context)) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(blurRadius)
                .setOverlayColor(
                    ContextCompat.getColor(context, blurOverlayColor)
                )

            bottomBlurView.apply {
                outlineProvider = ViewOutlineProvider.BACKGROUND
                clipToOutline = true
                visibility = if (showBlurLayout) View.VISIBLE else View.GONE
            }

            val innerComposeView = it.findViewById<ComposeView>(R.id.inner_compose_view)

            innerComposeView.apply {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                setContent {
                    innerContent()
                }
            }

            val backgroundComposeView = it.findViewById<ComposeView>(R.id.background_compose_view)

            backgroundComposeView.apply {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                setContent {
                    backgroundContent()
                }
            }

            val bottomInnerComposeView =
                it.findViewById<ComposeView>(R.id.bottom_inner_compose_view)

            bottomInnerComposeView.apply {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                setContent {
                    bottomInnerContent()
                }
            }


            val bottomComposeView = it.findViewById<ComposeView>(R.id.bottom_compose_view)

            bottomComposeView.apply {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                setContent {
                    bottomComposableContent()
                }
            }

        }
    )


}

private fun getBlurAlgorithm(context: Context): BlurAlgorithm {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        RenderEffectBlur()
    } else {
        RenderScriptBlur(context)
    }
}

private fun getActivityDecorView(context: Context): View? {
    var ctx = context
    var i = 0
    while (i < 4 && ctx !is Activity && ctx is ContextWrapper) {
        ctx = ctx.baseContext
        i++
    }
    return if (ctx is Activity) {
        ctx.window.decorView
    } else {
        null
    }
}