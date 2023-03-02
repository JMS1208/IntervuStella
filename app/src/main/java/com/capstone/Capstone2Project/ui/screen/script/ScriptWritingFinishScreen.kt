package com.capstone.Capstone2Project.ui.screen.script

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.navigation.ROUTE_CAMERA
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.navigation.ROUTE_SCRIPT_WRITING
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.theme.LocalSpacing
import com.capstone.Capstone2Project.utils.theme.bg_grey
import com.capstone.Capstone2Project.utils.theme.bright_blue

@Composable
fun ScriptWritingFinishScreen(
    script: Script,
    navController: NavController
) {

    val sparkleComposition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/sparkle.json"))
    val sparkleProgress by animateLottieCompositionAsState(
        sparkleComposition,
        iterations = LottieConstants.IterateForever
    )

    val flagComposition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/flags.json"))
    val flagProgress by animateLottieCompositionAsState(
        flagComposition,
        iterations = LottieConstants.IterateForever
    )

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    val screenHeight = configuration.screenHeightDp.dp

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(
                color = bg_grey
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(min(screenWidth, screenHeight).div(2)),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = flagComposition,
                    progress = { sparkleProgress }
                )
                LottieAnimation(
                    composition = sparkleComposition,
                    progress = { sparkleProgress }
                )
            }

            Text(
                "자기소개서 작성 완료 !",
                color = Color.DarkGray,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                "작성하신 자기소개서를 바탕으로\nAI 모의 면접 질문이 구성됩니다.",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(spacing.large))

            IconButton(
                onClick = {
                    navController.navigate(
                        "$ROUTE_CAMERA/{script}".replace(
                            oldValue = "{script}",
                            newValue = script.toJsonString()
                        )
                    ) {
                        popUpTo(ROUTE_HOME) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.large)
                    .height(50.dp)
                    .border(
                        width = 1.dp,
                        color = bright_blue,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = bright_blue
                    )

                    Spacer(modifier = Modifier.width(spacing.small))

                    Text(
                        "바로 AI 모의면접 보기",
                        color = bright_blue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            IconButton(
                onClick = {
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(ROUTE_HOME) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.large)
                    .height(50.dp)
                    .shadow(
                        3.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(
                        color = bright_blue,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(spacing.small))

                    Text(
                        "홈 화면으로 이동하기",
                        style = LocalTextStyle.current.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            shadow = Shadow(
                                Color.DarkGray,
                                offset = Offset(1f, 1f),
                                blurRadius = 4f
                            )
                        )
                    )
                }
            }
        }

    }


}