package com.capstone.Capstone2Project.ui.screen.animation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.capstone.Capstone2Project.data.model.InterviewLogLine
import com.capstone.Capstone2Project.data.model.LogLine
import com.capstone.Capstone2Project.data.model.QuestionItem
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.generateRandomText
import com.capstone.Capstone2Project.utils.theme.LocalSpacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.random.Random


@Preview(showBackground = true)
@Composable
private fun Preview() {

    val f: Flow<InterviewLogLine> = remember {
        flow {
            while (true) {
                emit(
                    InterviewLogLine(
                        progress = Random.nextLong(0, 100),
                        date = System.currentTimeMillis(),
                        questionItem = QuestionItem(
                            uuid = UUID.randomUUID().toString(),
                            question = "질문 예시",
                            questionType = 0
                        ),
                        logLine = LogLine(
                            type = LogLine.Type.Camera,
                            message = generateRandomText()
                        )
                    )
                )
                delay(3000)

            }
        }
    }

    val flowValue = f.collectAsStateWithLifecycle(null)

    var newInterviewLogLine: InterviewLogLine? by remember {
        mutableStateOf(flowValue.value)
    }

    var oldInterviewLogLine: InterviewLogLine? by remember {
        mutableStateOf(null)
    }


    val spacing = LocalSpacing.current

    LaunchedEffect(flowValue.value) {
        oldInterviewLogLine = newInterviewLogLine
        newInterviewLogLine = flowValue.value
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                spacing.small,
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small)
        ) {
            oldInterviewLogLine?.let {
                OldLogContent(interviewLogLine = it)
            }
            newInterviewLogLine?.let {
                NewLogContent(interviewLogLine = it)
            }

        }

    }


}


@Composable
private fun ItemContent(
    modifier: Modifier = Modifier,
    interviewLogLine: InterviewLogLine,
    fontSize: TextUnit,
    maxLines: Int = Int.MAX_VALUE,
    isNew: Boolean
) {

    val progressText = remember {
        interviewLogLine.progressToString()
    }

    val message = remember {
        interviewLogLine.logLine.message
    }

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
                progressText,
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
                message,
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
    interviewLogLine: InterviewLogLine
) {

    val oldInterviewLog by rememberUpdatedState(newValue = interviewLogLine)

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
        modifier = modifier
    ) { il ->
        ItemContent(
            interviewLogLine = il,
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
    interviewLogLine: InterviewLogLine
) {

    val oldInterviewLog by rememberUpdatedState(newValue = interviewLogLine)

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
            interviewLogLine = il,
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