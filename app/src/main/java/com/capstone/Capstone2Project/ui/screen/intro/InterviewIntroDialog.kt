package com.capstone.Capstone2Project.ui.screen.intro

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.ScriptItem
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.navigation.ROUTE_INTERVIEW_GUIDE
import com.capstone.Capstone2Project.navigation.ROUTE_SCRIPT_WRITING
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.HighlightText
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.shimmerEffect
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.pager.*
import com.webtoonscorp.android.readmore.foundation.ToggleArea
import com.webtoonscorp.android.readmore.material.ReadMoreText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue


@Preview(showBackground = true)
@Composable
private fun Preview() {
    InterviewIntroDialog(navController = rememberNavController())
}


@Composable
fun InterviewIntroDialog(
    navController: NavController,
    onDismissRequest: () -> Unit = {}
) {

    val viewModel: InterviewIntroViewModel = hiltViewModel()

    val scriptsFlow = viewModel.scriptsFlow.collectAsStateWithLifecycle()


    val context = LocalContext.current


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {

        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {

            scriptsFlow.value?.let {
                when (it) {
                    is Resource.Error -> {
                        it.error?.message?.let { message ->
                            AlertUtils.showToast(context, message, Toast.LENGTH_LONG)
                        }
                    }
                    Resource.Loading -> {
                        LoadingScreen()
                    }
                    is Resource.Success -> {
                        SelectScriptContent(
                            navController = navController, scripts = it.data
                        )
                    }
                }
            }


        }

    }

}

@OptIn(ExperimentalPagerApi::class, ExperimentalTextApi::class)
@Composable
private fun SelectScriptContent(
    navController: NavController, scripts: List<Script>
) {


    val context = LocalContext.current

//    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/spaceship.json"))
//
//    val progress by animateLottieCompositionAsState(
//        composition, iterations = LottieConstants.IterateForever
//    )

    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


//        Spacer(modifier = Modifier.height(spacing.small))

        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter
        ) {
//            LottieAnimation(
//                composition = composition, progress = { progress }, modifier = Modifier.size(180.dp)
//            )
            ExplainsContent(
                modifier = Modifier
            )

        }

        val pagerState = rememberPagerState()

        Spacer(modifier = Modifier.height(spacing.large))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = spacing.medium),
            activeColor = White,
            inactiveColor = mustard_yellow,
            indicatorShape = RoundedCornerShape(50)
        )

        HorizontalPager(
            state = pagerState,
            count = scripts.size,
            modifier = Modifier.fillMaxWidth(),
            itemSpacing = spacing.medium,
            contentPadding = PaddingValues(start = 40.dp, end = 40.dp)
        ) { page ->
            val script = scripts[page]
            val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
            PagerContent(pageOffset = pageOffset, page = page, script = script)
        }

        BottomButtons(
            navController = navController,
            onClickWriteNewScript = {
                navController.navigate(ROUTE_SCRIPT_WRITING) {
                    popUpTo(ROUTE_HOME) {

                    }
                }
            },
            onClickSelectScript = {
                val currentPage = pagerState.currentPage

                val script = scripts[currentPage]

                navController.navigate(
                    "$ROUTE_INTERVIEW_GUIDE/{script}".replace(
                        oldValue = "{script}",
                        newValue = script.toJsonString()
                    )
                ) {
//                    launchSingleTop = true
//                    popUpTo(ROUTE_HOME) {
//                        inclusive = true
//
//                    }
                }
            }
        )
    }
}


@Composable
private fun BottomButtons(
    navController: NavController,
    onClickWriteNewScript: () -> Unit,
    onClickSelectScript: () -> Unit
) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            color = White,
            fontFamily = nexonFont,
            fontWeight = FontWeight(550)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = spacing.small),
            horizontalArrangement = Arrangement.spacedBy(
                spacing.medium,
                Alignment.CenterHorizontally
            )
        ) {


            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .shadow(
//                            5.dp,
//                            shape = RoundedCornerShape(30.dp)
//                        )
//                        .background(
//                            brush = Brush.linearGradient(
//                                colors = listOf(
//                                    mustard_yellow,
//                                    orange_yellow
//                                )
//                            ),
//                            shape = RoundedCornerShape(30.dp)
//                        )
                        .border(
                            width = 1.dp,
                            color = White ,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .clickable {
                            onClickWriteNewScript()
                        },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "새로 작성",
                        modifier = Modifier.padding(vertical = spacing.medium),
                        fontSize = 16.sp
                    )
                }




                Text(
                    "New",
                    modifier = Modifier
                        .shadow(1.dp, shape = RoundedCornerShape(5.dp))
                        .background(
                            color = text_red,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .align(Alignment.TopStart)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 12.sp
                )

            }


            Box(
                modifier = Modifier
                    .weight(1f)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            5.dp,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    bright_blue,
                                    text_sharp_blue
                                )
                            ),
                            shape = RoundedCornerShape(30.dp)
                        )
                        .shimmerEffect(2000)
                        .clickable {
                            onClickSelectScript()
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = White
                    )
                    Text(
                        "선택 완료",
                        modifier = Modifier.padding(vertical = spacing.medium),
                        fontSize = 16.sp
                    )
                }

            }


        }
    }


}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun ExplainsContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        Text(
            "AI 면접에 사용할\n자기소개서를 선택해주세요 !",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = LocalTextStyle.current.copy(
                color = Black, fontSize = 20.sp, fontWeight = FontWeight(550), shadow = Shadow(
                    color = Black, offset = Offset(1f, 1f), blurRadius = 4f
                ), drawStyle = Stroke(
                    miter = 10f, width = 10f, join = StrokeJoin.Round
                )
            )
        )
        Text(
            "AI 면접에 사용할\n자기소개서를 선택해주세요 !",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = LocalTextStyle.current.copy(
                brush = Brush.linearGradient(
                    colors = listOf(
                        bright_sky_blue, bright_blue, bright_purple
                    )
                ), fontSize = 20.sp, fontWeight = FontWeight(550),
//                    shadow = Shadow(
//                        color = Black,
//                        offset = Offset(4f, 4f),
//                        blurRadius = 8f
//                    ),
                drawStyle = Stroke(
                    miter = 10f, width = 10f, join = StrokeJoin.Round
                )
            )
        )
        Text(
            "AI 면접에 사용할\n자기소개서를 선택해주세요 !",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = LocalTextStyle.current.copy(
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight(550),
//                    shadow = Shadow(
//                        color = Black,
//                        offset = Offset(4f, 4f),
//                        blurRadius = 8f
//                    ),

            )
        )
    }
}

@Composable
private fun PagerContent(
    pageOffset: Float, page: Int, script: Script
) {

    val dateFormat = SimpleDateFormat("yyyy.MM.dd (E) hh:mm", Locale.getDefault())


    val spacing = LocalSpacing.current

    val isChecked = remember {
        mutableStateOf(false)
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall,Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(shape = RoundedCornerShape(15.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                colors = CardDefaults.cardColors(
                    containerColor = bg_grey
                ),
                modifier = Modifier
                    .heightIn(200.dp, 400.dp)
                    .graphicsLayer {
                        lerp(
                            start = ScaleFactor(0.95f, 0.85f),
                            stop = ScaleFactor(1f, 1f),
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale.scaleX
                            scaleY = scale.scaleY
                        }

                        alpha = lerp(
                            start = ScaleFactor(0.5f, 0.5f),
                            stop = ScaleFactor(1f, 1f),
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).scaleX
                    }

            ) {


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = spacing.large, horizontal = spacing.medium
                        ),
                    verticalArrangement = Arrangement.spacedBy(
                        spacing.small, Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Text(
                        text = script.name, style = LocalTextStyle.current.copy(
                            color = DarkGray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight(550),
                            textDecoration = TextDecoration.Underline
                        ), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(spacing.medium))




                    Text(
                        text = "${dateFormat.format(script.date)} 저장",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = text_red,
                        fontSize = 11.sp
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().background(color = White, shape = RoundedCornerShape(5.dp)),
                        verticalArrangement = Arrangement.spacedBy(spacing.medium),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(horizontal = spacing.small, vertical = spacing.medium)
                    ) {
                        items(script.scriptItems.size) { idx ->
                            ScriptItemContent(
                                idx, script.scriptItems[idx]
                            )
                        }
                    }







                }


            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.small)
            ) {
                Checkbox(
                    checked = isChecked.value,
                    onCheckedChange = {
                        isChecked.value = it
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = bright_blue,
                        uncheckedColor = LightGray,
                        checkmarkColor = White
                    ),
                    enabled = script.questionnaireState
                )

                Text("이전에 시도한 면접 진행", color = White, fontSize = 14.sp, fontWeight = FontWeight(550))

            }
        }


    }

}

@Composable
private fun ScriptItemContent(
    index: Int, scriptItem: ScriptItem
) {

    val (expanded, onExpandedChange) = rememberSaveable {
        mutableStateOf(false)
    }

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Box(
                modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart
            ) {
                HighlightText(
                    text = "${index + 1}. ${scriptItem.question}",
                    highlightColor = Color(0xFF50A2FF),
                    style = TextStyle(
                        color = text_sharp_blue,
                        fontSize = 14.sp,
                        fontFamily = nexonFont,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier,
                    textAlign = TextAlign.Start,
                    offset = 8.dp
                )
            }


            Spacer(modifier = Modifier.height(spacing.small))

            ReadMoreText(
                text = scriptItem.answer ?: "",
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        shape = RoundedCornerShape(5.dp), width = 1.dp, color = Gray
                    )
                    .padding(spacing.small),
                readMoreText = "더보기",
                readMoreColor = text_blue,
                readMoreFontWeight = FontWeight(500),
                readMoreMaxLines = 4,
                readLessText = "닫기",
                readLessColor = text_blue,
                readLessFontWeight = FontWeight(500),
                toggleArea = ToggleArea.More,
                color = DarkGray,
                fontSize = 13.sp
            )
        }
    }
}
