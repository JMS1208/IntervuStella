package com.capstone.Capstone2Project.ui.screen.animation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.capstone.Capstone2Project.data.model.LiveFeedback
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.theme.LocalSpacing
import java.util.*


@Composable
private fun ItemContent(
    modifier: Modifier = Modifier,
    liveFeedback: LiveFeedback,
    fontSize: TextUnit,
    maxLines: Int = Int.MAX_VALUE,
    isNew: Boolean
) {

    val spacing = LocalSpacing.current


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont,
            color = Color.White,
            shadow = Shadow(
                Color.Black,
                offset = Offset(1f, 1f),
                blurRadius = 4f
            ),
            fontSize = fontSize
        )
    ) {

        ConstraintLayout(
            modifier = modifier.padding(horizontal = spacing.small)
        ) {

            val (progressRef, messageRef, newRef) = createRefs()


            Text(
                liveFeedback.progressToString(),
                modifier = Modifier
                    .constrainAs(progressRef) {
                        end.linkTo(messageRef.start, margin = spacing.extraSmall)
                        bottom.linkTo(messageRef.bottom)
                    },
                style = LocalTextStyle.current.copy(
                    fontSize = fontSize.div(3).times(2),
                    color = White,
                    fontWeight = FontWeight(550),
                    shadow = Shadow(
                        color = DarkGray,
                        offset = Offset(1f, 1f),
                        blurRadius = 4f
                    )
                )
            )



            Text(
                liveFeedback.message,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .constrainAs(messageRef) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)

                        width = Dimension.percent(if (isNew) 0.9f else 0.8f)
                    }
                    .background(
                        color = Color(0x66000000),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(horizontal = spacing.medium, vertical = spacing.small),
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Medium,
                    shadow = Shadow(
                        color = DarkGray,
                        offset = Offset(1f, 1f),
                        blurRadius = 4f
                    )
                )
            )

            if (isNew) {
                Text(
                    "New",
                    style = TextStyle(
                        fontFamily = nexonFont,
                        fontSize = 10.sp,
                        color = White,
                        shadow = Shadow(
                            color = DarkGray,
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    ),
                    modifier = Modifier
                        .constrainAs(newRef) {
                            end.linkTo(messageRef.start)
                            top.linkTo(messageRef.top)
                            width = Dimension.wrapContent
                        }
                        .offset(x = spacing.small, y = spacing.extraSmall)
                        .background(
                            color = Red,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)

                )
            }


        }


    }


}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NewLogContent(
    modifier: Modifier = Modifier,
    liveFeedback: LiveFeedback
) {

    val oldLiveFeedback by rememberUpdatedState(newValue = liveFeedback)

    AnimatedContent(
        targetState = oldLiveFeedback,
        transitionSpec = {
            slideInVertically {
                it
            } + fadeIn(
                initialAlpha = 0f
            ) with fadeOut(
                targetAlpha = 0f
            ) + slideOutVertically {
                -it
            }
        },
        modifier = modifier
    ) { il ->
        ItemContent(
            liveFeedback = il,
            fontSize = 16.sp,
            isNew = true,
            maxLines = 2
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OldLogContent(
    modifier: Modifier = Modifier,
    liveFeedback: LiveFeedback
) {

    val oldInterviewLog by rememberUpdatedState(newValue = liveFeedback)

    AnimatedContent(
        targetState = oldInterviewLog,
        transitionSpec = {
            slideInVertically {
                it
            } + fadeIn(
                initialAlpha = 0f
            ) with fadeOut(
                targetAlpha = 0f
            ) + slideOutVertically {
                -it
            }
        },
        modifier = modifier.alpha(0.5f)
    ) { il ->
        ItemContent(
            liveFeedback = il,
            fontSize = 14.sp,
            maxLines = 1,
            isNew = false
        )
    }
}


private fun generateRandomText(): String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
    return (1..100)
        .map { charset.random() }
        .joinToString("")
}