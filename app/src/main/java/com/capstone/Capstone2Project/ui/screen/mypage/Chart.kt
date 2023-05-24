package com.capstone.Capstone2Project.ui.screen.home

import android.graphics.Paint.Align
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.android.animation.SegmentType
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.InterviewScore
import com.capstone.Capstone2Project.utils.composable.GlassMorphismCardBackground
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.WithEmojiView
import com.capstone.Capstone2Project.utils.extensions.shimmerEffect
import com.capstone.Capstone2Project.utils.extensions.string
import com.capstone.Capstone2Project.utils.extensions.toDp
import com.capstone.Capstone2Project.utils.theme.*
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import kotlin.math.absoluteValue


@Composable
fun ChartScreen(rankRecords: InterviewScore) {

    val spacing = LocalSpacing.current

//    val dataPointList = getDataPointList()

    fun getDataPointList(): List<DataPoint> {

        return rankRecords.ranks.mapIndexed { idx, rank->
            DataPoint(
                x = idx.toFloat(),
                y = rank.rankToDataPointY()
            )
        }
    }




    val density = LocalDensity.current

    val simpleDateFormat = remember {
        SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = White
        )
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.medium)
                    .shadow(3.dp,shape = RoundedCornerShape(5.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                darker_blue,
                                deep_darker_blue
                            )
                        ),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(spacing.medium)
            ) {

                Text(
                    "지난 면접 기록",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight(550),
                    fontSize = 20.sp,
                    color = White,
                    fontFamily = nexonFont
                )

                ConstraintLayout(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = spacing.small)
                        .wrapContentSize()
                ) {
                    val (lowRef, highRef, newRef) = createRefs()

                    Text(
                        "최저 등급\n${rankRecords.minRank}",
                        modifier = Modifier.constrainAs(lowRef) {
                            bottom.linkTo(parent.bottom, margin = spacing.small)
                            start.linkTo(parent.start, margin = spacing.small)
                            end.linkTo(highRef.start, margin = spacing.small)
                            width = Dimension.wrapContent
                        },
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = nexonFont
                    )

                    Text(
                        "최고 등급\n${rankRecords.maxRank}",
                        modifier = Modifier
                            .wrapContentSize()
                            .constrainAs(highRef) {
                                bottom.linkTo(parent.bottom, margin = spacing.small)
                                end.linkTo(parent.end, margin = spacing.small)
                                start.linkTo(lowRef.end, margin = spacing.small)
                                width = Dimension.wrapContent
                            },
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = nexonFont
                    )

                    Text(
                        "New",
                        modifier = Modifier
                            .background(
                                color = text_red,
                                shape = RoundedCornerShape(3.dp)
                            )
                            .padding(horizontal = 5.dp)
                            .constrainAs(newRef) {
                                start.linkTo(lowRef.end, margin = spacing.extraSmall)
                                bottom.linkTo(highRef.top, margin = 3.dp)
                            },
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = White
                    )
                }


                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .height(200.dp)
                ) {

                    ChartContent(
                        modifier = Modifier
                            .padding(end = spacing.small),
                        dataPointList = getDataPointList()
                    )


                }

                Spacer(modifier = Modifier.height(spacing.medium))

                Text("마지막 기록 ${simpleDateFormat.format(Date(rankRecords.recentlyDate))}",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp,
                    color = White,
                    textAlign = TextAlign.End,
                    fontFamily = nexonFont
                )

            }


            Image(
                painter = painterResource(id = R.drawable.ic_rocket),
                contentDescription = null,
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .height(200.dp)
                    .align(Alignment.TopEnd)
            )


        }


    }
}


@Composable
fun ChartContent(
    modifier: Modifier = Modifier,
    dataPointList: List<DataPoint>
) {

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont
        )
    ) {
        MaterialTheme(
            colors = MaterialTheme.colors.copy(surface = Color.Transparent)
        ) {

            LineGraph(
                modifier = modifier
                    .padding(horizontal = 16.dp),
                plot = LinePlot(
                    lines = listOf(createLinePlotLine(dataPointList)), //여러 그래프 선 쓰면 여기에 추가해주면 됨 근데 그게 아니니까
                    selection = selection,
                    xAxis = xAxis, //X축 좌표 표기
                    yAxis = yAxis, //Y축 좌표 표기
                    grid = grid //그래프 뒤에 점선
                )
            )
        }


    }


}

private val grid = LinePlot.Grid( //뒤에 점선
    color = Color(0x85FFFFFF),
    steps = 5,
    draw = { region, _, _ ->
        val (left, top, right, bottom) = region
        val availableHeight = bottom - top
        val steps = 5
        val color = Color(0x85FFFFFF)
        val lineWidth = 1.dp
        val offset = availableHeight / if (steps > 1) steps - 1 else 1
        (0 until steps).forEach {
            val y = bottom - (it * offset)
            drawLine(
                color,
                Offset(left, y),
                Offset(right, y),
                lineWidth.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

        }
    }
)

private val yAxis = LinePlot.YAxis(
    content = { min, offset, _ ->
        val steps = 5
        for (it in 0 until steps) {
            Spacer(
                modifier = Modifier
                    .height(10.dp)
                    .width(0.dp)
            )
        }

    }
)

private val xAxis = LinePlot.XAxis(
    steps = 7,
    stepSize = 20.dp,
    unit = 0.4f,
    content = { min, offset, max ->
        val steps = 7
        for (it in 0 until steps) {
            val value = it * offset + min
            androidx.compose.material.Text(
                text = value.string(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption,
                color = Color.White
            )
            if (value > max) {
                break
            }
        }
    }
)

private val selection = LinePlot.Selection(
    highlight = LinePlot.Connection(
        Color.Gray,
        strokeWidth = 2.dp,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 20f))
    )
)

private fun createLinePlotLine(dataPoints: List<DataPoint>): LinePlot.Line {
    return LinePlot.Line(
        dataPoints = dataPoints,//그래프 선 하나에 해당하는 데이터들
        connection = LinePlot.Connection(White, 3.dp), //선
        intersection = LinePlot.Intersection( //연결부위 동그라미
            color = White,
            radius = 5.dp,
            alpha = 0.9f
        ),
        highlight = LinePlot.Highlight(
            draw = { center -> // 클릭시 원 크게 보이는 것
                val color = dim_red
                drawCircle(color, 9.dp.toPx(), center, alpha = 0.3f)
                drawCircle(color, 6.dp.toPx(), center)
                drawCircle(Color.White, 3.dp.toPx(), center)
            }
        ),
        areaUnderLine = LinePlot.AreaUnderLine( //그래프 아래 그라데이션 넣기
            draw = { path ->
                drawPath(
                    path,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White,
                            Color.Transparent,
                        )
                    )
                )
            }
        )
    )
}


private fun getLines(): List<List<DataPoint>> {
    return listOf(
        listOf(
            DataPoint(0f, 0f),
            DataPoint(1f, 20f),
            DataPoint(2f, 50f),
            DataPoint(3f, 10f),
            DataPoint(4f, 0f)
        )
    )
}

private fun getDataPointList(): List<DataPoint> {
    return listOf(
        DataPoint(1f, 0f),
        DataPoint(2f, 50f),
        DataPoint(3f, 50f),
        DataPoint(4f, 100f),
        DataPoint(5f, 0f)
    )
}

internal fun Dp.toPx(density: Density) = value * density.density
