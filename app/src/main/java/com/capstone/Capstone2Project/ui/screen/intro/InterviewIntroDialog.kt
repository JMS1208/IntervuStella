package com.capstone.Capstone2Project.ui.screen.intro

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
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
import com.capstone.Capstone2Project.utils.extensions.toFormatString
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.pager.*
import com.webtoonscorp.android.readmore.foundation.ToggleArea
import com.webtoonscorp.android.readmore.material.ReadMoreText
import kotlinx.coroutines.flow.collectLatest
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

    val scriptsFlow = viewModel.scriptsFlow.collectAsState()

    val state = viewModel.state.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchScripts()
    }

    LaunchedEffect(viewModel) {

        viewModel.effect.collectLatest {
            when(it) {
                is InterviewIntroViewModel.Effect.NavigateTo -> {
                    val route = it.route
                    navController.navigate(route)
                }
                is InterviewIntroViewModel.Effect.ShowMessage -> {
                    val message = it.message
                    AlertUtils.showToast(context, message)
                }
            }
        }
    }


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
                        when(state.value.networkState) {
                            is InterviewIntroViewModel.NetworkState.Error -> {
                                val message = (state.value.networkState as InterviewIntroViewModel.NetworkState.Error).message
                                LaunchedEffect(message) {
                                    AlertUtils.showToast(context, message)
                                }
                            }
                            is InterviewIntroViewModel.NetworkState.Loading -> {
                                val message = (state.value.networkState as InterviewIntroViewModel.NetworkState.Loading).message
                                LoadingScreen(message)
                            }
                            InterviewIntroViewModel.NetworkState.Normal -> {
                                SelectScriptContent(
                                    navController = navController,
                                    scripts = it.data,
                                    scriptSelected = { selectedScript, page ->
                                        viewModel.fetchQuestionnaire(selectedScript, page)
                                    },
                                    reuseChecked = { page, isChecked ->
                                        viewModel.reuseCheck(page, isChecked)
                                    }
                                )
                            }

                            else -> Unit
                        }

                    }
                }
            }


        }

    }

}

@OptIn(ExperimentalPagerApi::class, ExperimentalTextApi::class)
@Composable
private fun SelectScriptContent(
    navController: NavController,
    scripts: List<Script>,
    scriptSelected: (Script, Int) -> Unit,
    reuseChecked: (Int, Boolean) -> Unit
) {


    val context = LocalContext.current


    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter
        ) {

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
            PagerContent(pageOffset = pageOffset, page = page, script = script) {
                reuseChecked(page, it)
            }
        }

        Spacer(modifier = Modifier.height(spacing.large))

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

                scriptSelected(script, currentPage)


            },
            pagerState = pagerState
        )
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun BottomButtons(
    navController: NavController,
    onClickWriteNewScript: () -> Unit,
    onClickSelectScript: () -> Unit,
    pagerState: PagerState
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
                            color = White,
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



            if (pagerState.pageCount > 0) {
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
    pageOffset: Float, page: Int, script: Script, checked: (Boolean) -> Unit
) {

    val dateFormat = SimpleDateFormat("yyyy.MM.dd (E) hh:mm", Locale.getDefault())


    val spacing = LocalSpacing.current

    val isChecked = remember {
        mutableStateOf(false)
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/profil.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                spacing.extraSmall,
                Alignment.CenterVertically
            ),
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
                        .verticalScroll(rememberScrollState())
                        .background(
                            color = White,
                            shape = RoundedCornerShape(spacing.medium)
                        )
                        .padding(
                            vertical = spacing.large,
                            horizontal = spacing.medium
                        ),
                    verticalArrangement = Arrangement.spacedBy(
                        spacing.large,
                        Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            spacing.medium,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        LottieAnimation(
                            composition = composition,
                            progress = { progress }
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(spacing.small),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                script.title,
                                color = Black,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                fontFamily = nexonFont
                            )

                            /*
                            아래 Row의 사이즈 측정해서 자식 Box 크기 정할때 씀
                             */

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    spacing.small,
                                    Alignment.Start
                                )
                            ) {

                                Box(
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .background(
                                            color = bright_pink,
                                            shape = RoundedCornerShape(50)
                                        )
                                        .padding(
                                            vertical = spacing.small,
                                            horizontal = spacing.medium
                                        )
                                ) {
                                    Text(
                                        script.jobRole,
                                        color = White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        fontFamily = nexonFont,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }


                                Box(
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .background(
                                            color = orange_yellow,
                                            shape = RoundedCornerShape(50)
                                        )
                                        .padding(
                                            vertical = spacing.small,
                                            horizontal = spacing.medium
                                        )
                                ) {
                                    Text(
                                        if (script.interviewed) "면접기록 있음" else "면접기록 없음",
                                        color = White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        fontFamily = nexonFont,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }

                            }

                            if (script.date != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        "작성일: ${script.date!!.toFormatString("yyyy.MM.dd")}",
                                        color = Gray,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.sp,
                                        fontFamily = nexonFont
                                    )
                                }
                            }

                        }
                    }

                    script.scriptItems.forEachIndexed { index, scriptItem ->

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(
                                spacing.small,
                                Alignment.CenterVertically
                            ),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "${index + 1}. ${scriptItem.question} (${scriptItem.maxLength})",
                                color = Black,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                fontFamily = nexonFont
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = bg_mono_grey
                                    )
                                    .padding(vertical = spacing.medium, horizontal = spacing.small),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = scriptItem.answer,
                                    color = DarkGray,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13.sp,
                                    fontFamily = nexonFont,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }

                    }
                }


            }

            if (script.interviewed) {
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
                            checked(it)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = bright_blue,
                            uncheckedColor = LightGray,
                            checkmarkColor = White
                        )
                    )

                    Text(
                        "면접 재시도",
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight(550)
                    )

                }
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
