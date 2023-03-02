package com.capstone.Capstone2Project.utils.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.capstone.Capstone2Project.ui.screen.home.toPx
import com.capstone.Capstone2Project.utils.theme.bright_blue


@Preview(showBackground = true)
@Composable
private fun Preview() {
//    Canvas(
////        modifier = Modifier.size(100.dp)
////    ) {
////        val roundedCornerPath = createPocketBookPath(10.dp.value, size = Size(100f, 100f))
////
////        val circlePath = createCirclePath(Size(10f, 10f))
////
////        val punchHolesPath = createPunchHoles(size = Size(500f,200f), circleSize = Size(10f,10f))
////
////        clipPath(punchHolesPath, clipOp = ClipOp.Difference) {
////            drawPath(roundedCornerPath, color = Black)
////        }
////
//////        drawPath(punchHolesPath, color = Black)
////    }

    Box(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
            .shadow(
                5.dp, shape = PocketBookShape(15.dp, circleWidth = 20.dp)
            )
            .background(
                color = bright_blue,
                shape = PocketBookShape(15.dp, circleWidth = 20.dp)
            )

    ) {

    }
}


class PocketBookShape(
    private val cornerRadius: Dp,
    private val circleWidth: Dp,
    private val circleHeight: Dp = circleWidth,
    private val circleCount: Int? = null,
    private val abovePadding: Dp? = null,
    private val horizontalPadding: Dp? = null,
    private val itemSpace: Dp? = null
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val punchHolesPath =
                    createPunchHoles(
                        size = size,
                        circleSize = Size(
                            circleWidth.toPx(density),
                            circleHeight.toPx(density)
                        ),
                        circleCount = circleCount,
                        abovePadding = abovePadding?.toPx(density),
                        horizontalPadding = horizontalPadding?.toPx(density),
                        itemSpace = itemSpace?.toPx(density)
                    )

        return Outline.Generic(
            createRoundedCornerPath(cornerRadius.toPx(density), size).apply {
                op(this, punchHolesPath, PathOperation.Difference)
            }

        )
    }
}


private fun createCirclePath(size: Size): Path {
    return Path().apply {
        moveTo(0f, 0f)

        addOval(
            Rect(
                0f,
                0f,
                size.width,
                size.height
            )
        )
        close()
    }

}

private fun createRoundedCornerPath(radius: Float, size: Size): Path {
    return Path().apply {

        moveTo(radius, 0f)
        lineTo(size.width - radius, 0f)
        arcTo(
            rect = Rect(
                size.width - 2 * radius,
                0f,
                size.width,
                radius * 2
            ),
            startAngleDegrees = 270f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        lineTo(size.width, size.height - 2 * radius)

        arcTo(
            rect = Rect(
                size.width - 2 * radius,
                size.height - 2 * radius,
                size.width,
                size.height
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        lineTo(radius, size.height)

        arcTo(
            rect = Rect(
                0f,
                size.height - 2 * radius,
                2 * radius,
                size.height
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

private fun createPunchHoles(
    size: Size,
    circleSize: Size,
    circleCount: Int? = null,
    abovePadding: Float? = null,
    horizontalPadding: Float? = null,
    itemSpace: Float? = null
): Path {
    return Path().apply {
        val _horizontalPadding = horizontalPadding ?: 0f
        val _abovePadding = abovePadding ?: circleSize.height
        val _itemSpace = itemSpace ?: circleSize.width
        val _circleCount = circleCount
            ?: ((size.width - 2 * _horizontalPadding + _itemSpace) / (circleSize.width + _itemSpace)).toInt()
        val xOffsetAdjustment =
            (size.width - 2 * _horizontalPadding - _circleCount * circleSize.width - (_circleCount - 1) * _itemSpace) / 2


        for (i in 0 until _circleCount) {
            val xOffset =
                _horizontalPadding + i * (_itemSpace + circleSize.width) + xOffsetAdjustment
            val yOffset = _abovePadding
            moveTo(xOffset, yOffset)
            addOval(
                Rect(
                    xOffset,
                    yOffset,
                    xOffset + circleSize.width,
                    yOffset + circleSize.height
                )
            )

        }

        close()
    }
}
