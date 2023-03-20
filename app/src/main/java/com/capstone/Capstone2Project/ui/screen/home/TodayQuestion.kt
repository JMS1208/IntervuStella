package com.capstone.Capstone2Project.ui.screen.home

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.setLayerType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.navigation.ROUTE_OTHERS_ANSWERS
import com.capstone.Capstone2Project.navigation.ROUTE_SCRIPT_WRITING
import com.capstone.Capstone2Project.navigation.ROUTE_SCRIPT_WRITING_FINISH
import com.capstone.Capstone2Project.ui.screen.auth.AuthViewModel
import com.capstone.Capstone2Project.utils.composable.DottedShape
import com.capstone.Capstone2Project.utils.composable.GlassMorphismCard
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.extensions.gradientBackground
import com.capstone.Capstone2Project.utils.extensions.shimmerEffect
import com.capstone.Capstone2Project.utils.theme.*
import eightbitlab.com.blurview.BlurAlgorithm
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur
import java.util.*

@Preview(showBackground = true)
@Composable
fun TodayQuestionPreview() {
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            color = Color.White,
            shadow = Shadow(
                color = Color.Black,
                offset = Offset(1f, 1f),
                blurRadius = 4f
            )
        )
    ) {
        TodayQuestionCard(
            question = "프로세스와 스레드의 차이는?",
            questionUUID = UUID.randomUUID().toString(),
            navController = rememberNavController()
        )
    }

}

/*
@Composable
fun TodayQuestionCard(
    question: String,
    modifier: Modifier = Modifier
) {

    var showVeil by remember {
        mutableStateOf(true)
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            color = Color.White,
            fontFamily = nexonFont
        )
    ) {
        ConstraintLayout(
            modifier = modifier
                .height(150.dp)
                .clickableWithoutRipple {
                    showVeil = !showVeil
                },

            ) {

            val (glassRef, textRef, contentRef) = createRefs()

            ContentOfTodayQuestion(
                question = question,
                modifier = Modifier.constrainAs(contentRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )

            AnimatedVisibility(
                visible = showVeil,
                exit = fadeOut() + slideOutVertically(spring(1f)) {
                    -it
                },
                enter = fadeIn()
            ) {
//                GlassMorphismCard(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .constrainAs(glassRef) {
//                            top.linkTo(parent.top)
//                            bottom.linkTo(parent.bottom)
//                        },
//                    blurRadius = 25f,
//                    overlayColor = 0x50FFFFFF
//                )
                Box(
                    modifier = Modifier
                        .shimmerEffect(2000)
                        .fillMaxSize()
//                        .constrainAs(textRef) {
//                            top.linkTo(glassRef.top)
//                            bottom.linkTo(glassRef.bottom)
//                        }
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    bright_blue,
                                    darker_blue
                                ),

                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "출석 체크하고\n오늘의 질문 확인하기!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = LocalTextStyle.current.copy(
                            shadow = Shadow(
                                color = Color.DarkGray,
                                offset = Offset(3f, 3f),
                                blurRadius = 8f
                            )
                        )
                    )
                }
            }


        }
    }

}

 */
@Composable
fun TodayQuestionCard(
    question: String,
    questionUUID: String,
    modifier: Modifier = Modifier,
    navController: NavController
) {


    val authViewModel: AuthViewModel = hiltViewModel()

    val viewModel: HomeViewModel = hiltViewModel()

    val isPresentFlow = viewModel.isPresentFlow.collectAsStateWithLifecycle()

    LaunchedEffect(authViewModel.currentUser) {
        authViewModel.currentUser?.uid?.let {
            viewModel.fetchHomeInformation(it)
        }
    }

    val showVeil by remember(isPresentFlow.value) {
        mutableStateOf(!isPresentFlow.value)
    }

    val context = LocalContext.current

    val blurRadius = 3f

    val blurOverlayColor = R.color.blurOverlayColor

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            color = Color.White,
            fontFamily = nexonFont
        )
    ) {
        ConstraintLayout(
            modifier = modifier
                .height(150.dp)
                .clickableWithoutRipple {
//                    showVeil = !showVeil
                    if(!isPresentFlow.value) {
                        authViewModel.currentUser?.uid?.let {
                            viewModel.checkAttendance(it)
                        }
                    }
                },

            ) {


            AndroidView(
                factory = {
                    View.inflate(it, R.layout.todays_question, null)
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
                        visibility = if (showVeil) View.VISIBLE else View.GONE
                    }

                    val innerComposeView = it.findViewById<ComposeView>(R.id.inner_compose_view)

                    innerComposeView.apply {
                        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                        setContent {

                            AnimatedVisibility(
                                showVeil
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "출석 체크하고\n오늘의 질문 확인하기 !",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        style = LocalTextStyle.current.copy(
                                            shadow = Shadow(
                                                color = Color.DarkGray,
                                                offset = Offset(1f, 1f),
                                                blurRadius = 8f
                                            )
                                        )
                                    )
                                }
                            }

                        }
                    }

                    val backComposeView = it.findViewById<ComposeView>(R.id.back_compose_view)

                    backComposeView.apply {
                        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                        setContent {
                            ContentOfTodayQuestion(
                                question = question,
                                questionUUID = questionUUID,
                                modifier = Modifier,
                                navController = navController,
                                canClickContent = !showVeil
                            )
                        }
                    }


                }
            )


        }
    }

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


@Composable
fun ContentOfTodayQuestion(
    question: String,
    questionUUID: String,
    modifier: Modifier = Modifier,
    elevation: Dp = 3.dp,
    cornerRadius: Dp = 10.dp,
    navController: NavController,
//    backgroundColor: Color = bright_blue
    canClickContent: Boolean
) {

    val spacing = MaterialTheme.spacing

    Box(
        modifier = modifier
            .shadow(elevation, shape = RoundedCornerShape(cornerRadius))
            .background(
                color = bright_blue,
                shape = RoundedCornerShape(cornerRadius)
            )
//            .shimmerEffect(2000)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(spacing.small)
                .fillMaxSize()

        ) {
            Text(
                "오늘의 질문 !?",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                question,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )


            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(
                            color = White,
                            shape = DottedShape(5.dp)
                        )
                )

                Spacer(modifier = Modifier.height(spacing.small))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(canClickContent) {
                                navController.navigate(
                                    "$ROUTE_OTHERS_ANSWERS/{question_uuid}".replace(
                                        oldValue = "{question_uuid}",
                                        newValue = questionUUID
                                    )
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {


                        Icon(
                            painter = painterResource(id = R.drawable.ic_pencil),
                            contentDescription = null,
                            tint = LocalTextStyle.current.color
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            "답변하기",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(canClickContent) {
                                //TODO {관심 주제 설정 화면으로 }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = LocalTextStyle.current.color
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            "다른 주제 볼래요",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                    }
                }
            }


        }
    }

}