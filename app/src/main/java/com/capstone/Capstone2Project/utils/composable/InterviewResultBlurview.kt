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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.utils.theme.*


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.body1
) {

    val oldCount by rememberUpdatedState(count)

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