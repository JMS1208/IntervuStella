package com.capstone.Capstone2Project.ui.screen.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont

@Composable
fun LoadingScreen(
    message: String? = null
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/loading.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress }
        )

        message?.let {
            Text(
                it,
                color = Color.LightGray,
                textAlign = TextAlign.Start,
                fontFamily = nexonFont,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.offset(y = 100.dp)
            )
        }
    }

}