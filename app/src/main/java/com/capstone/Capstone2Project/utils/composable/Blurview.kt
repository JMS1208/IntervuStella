package com.capstone.Capstone2Project.utils.composable

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.ui.screen.interview.InterviewMemoDialog
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.generateRandomText
import com.capstone.Capstone2Project.utils.theme.LocalSpacing
import com.capstone.Capstone2Project.utils.theme.bright_blue
import com.capstone.Capstone2Project.utils.theme.highlight_red
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.8f),
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

                            Text(
                                "Score", style = LocalTextStyle.current.copy(
                                    color = White,
                                    fontSize = 30.sp,
                                    shadow = Shadow(
                                        color = DarkGray,
                                        offset = Offset(1f, 1f),
                                        blurRadius = 8f
                                    ),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )


                            ScoreContent(score = interviewResult.score)

                            Spacer(modifier = Modifier.height(spacing.large))

                            FeedBackContent(interviewResult)

                            Spacer(modifier = Modifier.height(spacing.small))
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
                bottomInnerContent = { // 이쪽에 업적

                    if (showScore.value) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                "New",
                                modifier = Modifier
                                    .background(
                                        color = highlight_red,
                                        shape = RoundedCornerShape(5.dp),
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .align(Alignment.Start),
                                style = LocalTextStyle.current.copy(
                                    color = White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    10.dp,
                                    Alignment.CenterHorizontally
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.small)
                            ) {
                                Text(
                                    "갱신된 업적", style = LocalTextStyle.current.copy(
                                        color = White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )

                                Box(
                                    modifier = Modifier
                                        .height(1.dp)
                                        .weight(1f)
                                        .background(White, shape = DottedShape(5.dp))
                                )

                                Text(
                                    "${interviewResult.newAchievement.size}개", style = LocalTextStyle.current.copy(
                                        color = White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(spacing.medium))

                        }
                    }
                },
                showBlurLayout = showScore.value
            )



            if (showMemoDialog.value) {
                InterviewMemoDialog(
                    interviewUUID = interviewResult.uuid,
                    dismissClick = {
                    showMemoDialog.value = false
                })
            }

        }

    }
}

@Composable
private fun FeedBackContent(interviewResult: InterviewResult) {

    val spacing = LocalSpacing.current


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            color = White,
            fontFamily = nexonFont,
            fontWeight = FontWeight.SemiBold,
            shadow = Shadow(
                color = DarkGray,
                offset = Offset(1f, 1f),
                blurRadius = 4f
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.CenterHorizontally
                )
            ) {
                Text("면접 결과", fontSize = 18.sp, textAlign = TextAlign.Start)
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                        .background(White, shape = DottedShape(5.dp))
                )

            }


            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                "점수: ${interviewResult.score}", modifier = Modifier.fillMaxWidth(), style = LocalTextStyle.current.copy(
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End
                )
            )
            Spacer(modifier = Modifier.height(spacing.small))
            Text(
                "소요시간: ${interviewResult.durationToString()}",
                modifier = Modifier.fillMaxWidth(),
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End
                )
            )

            Spacer(modifier = Modifier.height(spacing.medium))


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.CenterHorizontally
                )
            ) {
                Text("면접 평가", fontSize = 18.sp, textAlign = TextAlign.Start)
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                        .background(White, shape = DottedShape(5.dp))
                )

            }

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                interviewResult.feedBack, fontSize = 14.sp, modifier = Modifier
                    .heightIn(50.dp, 150.dp)
                    .verticalScroll(rememberScrollState())
            )
            Spacer(modifier = Modifier.height(spacing.medium))

        }
    }

}

@Composable
private fun ScoreContent(score: Int) {

    var count by remember {
        mutableStateOf((score - 100).coerceIn(0, score))
    }


    val coroutineScope = rememberCoroutineScope()

    SideEffect {
        coroutineScope.launch {
            while (count < score) {
                count++
                delay(400)

            }
        }
    }


    Box(
        modifier = Modifier.padding(16.dp)
    ) {
        AnimatedCounter(
            count = count,
            style = LocalTextStyle.current.copy(
                fontSize = 25.sp,
                fontFamily = nexonFont,
                fontWeight = FontWeight.Bold,
                color = DarkGray
            )
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
                //background = ShapeDrawable(RoundedCornerShape(10.dp))
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
