package com.capstone.Capstone2Project.ui.screen.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont

@Composable
fun ErrorScreen(
    message: String? = null
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/error.json"))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },

            )

        message?.let {
            Text(
                it,
                style = LocalTextStyle.current.copy(
                    fontFamily = nexonFont,
                    fontSize = 15.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal
                )
            )
        }


    }

}