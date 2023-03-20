package com.capstone.Capstone2Project.ui.screen

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capstone.Capstone2Project.utils.composable.AnimatedCounter
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.theme.LocalSpacing
import com.capstone.Capstone2Project.utils.theme.bg_grey
import com.capstone.Capstone2Project.utils.theme.bright_blue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun CounterTextPreview() {
    CounterTextSample()
}

@Composable
fun CounterTextSample() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var count by remember {
            mutableStateOf(0)
        }

        val coroutineScope = rememberCoroutineScope()

        SideEffect {
            coroutineScope.launch {
                while (count < 1000) {
                    count++
                    delay(600)
                }
            }
        }


        Box(
            modifier = Modifier.padding(16.dp)
        ){
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


        Button(onClick = {
            count++
        }) {
            Text("증가시키기")
        }
    }

}


