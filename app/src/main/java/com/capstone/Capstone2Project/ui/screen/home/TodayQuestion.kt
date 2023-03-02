package com.capstone.Capstone2Project.ui.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.capstone.Capstone2Project.utils.composable.GlassMorphismCard
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.extensions.gradientBackground
import com.capstone.Capstone2Project.utils.extensions.shimmerEffect
import com.capstone.Capstone2Project.utils.theme.*

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
        TodayQuestionCard(question = "프로세스와 스레드의 차이는?")
    }

}

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


@Composable
fun ContentOfTodayQuestion(
    question: String,
    modifier: Modifier = Modifier,
    elevation: Dp = 3.dp,
    cornerRadius: Dp = 10.dp,
//    navController: NavController,
//    backgroundColor: Color = bright_blue
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
                .padding(20.dp)
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableWithoutRipple {
                        //TODO {관심 주제 설정 화면으로 }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "다른 주제 볼래요",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = LocalTextStyle.current.color
                )

            }
        }
    }

}