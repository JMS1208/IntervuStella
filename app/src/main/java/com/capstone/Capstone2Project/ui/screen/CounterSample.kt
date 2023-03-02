package com.capstone.Capstone2Project.ui.screen

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
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
                while(count < 1000) {
                    count++
                    delay(400)
                }
            }
        }


        AnimatedCounter(count = count)

        Button(onClick = {
            count++
        }) {
            Text("증가시키기")
        }
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

    Row(
        modifier = modifier
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

            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    slideInVertically {
                        it
                    } with slideOutVertically {
                        -it
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