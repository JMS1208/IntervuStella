package com.capstone.Capstone2Project.ui.screen.canvas

import android.widget.ProgressBar
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.utils.Utils.createPath

@Preview(showBackground = true)
@Composable
private fun Preview() {
//    PieSample(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    )

    Box(
        modifier = Modifier.fillMaxWidth().height(100.dp).background(
            color = Color.Red,
            shape = RoundedCornerShape(30.dp)
        )
    ) {

    }
}

@Composable
fun PieSample(
    modifier: Modifier = Modifier
) {

    var showAnimation by remember {
        mutableStateOf(false)
    }

    val widthState = animateFloatAsState(
        targetValue = if (showAnimation) 50f else 0f
    )

    var size by remember {
        mutableStateOf(Size.Zero)
    }

    val radius = 16.dp

    val path = createPath(Size(300f,100f), radius.value)

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        drawPath(
            path,
            color = Color.Red
        )
    }


    LaunchedEffect(true) {
        showAnimation = true
    }
}

class BarShape(private val radius: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = createPath(size, radius.value)

        return Outline.Generic(path)
    }

}

private fun createPath(size: Size, radius: Float): Path {
    return Path().apply {

        val midLineHeight = if(size.height > 2 * radius) {
            size.height - 2 * radius
        } else {
            0f
        }
        moveTo(radius, 0f)
        lineTo(size.width - radius, 0f)
        arcTo(
            rect = Rect(
                size.width - 2 * radius,
                0f,
                size.width,
                radius *2
            ),
            startAngleDegrees = 270f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        lineTo(size.width, midLineHeight)

        arcTo(
            rect = Rect(
                size.width - 2 * radius,
                0f,
                size.width,
                radius *2
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(radius, radius* 2)
        arcTo(
            rect = Rect(
                0f,
                0f,
                2 * radius,
                2 * radius
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(0f, radius)
        arcTo(
            rect = Rect(
                0f,
                0f,
                2 * radius,
                2 * radius
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )


        close()
    }
}
