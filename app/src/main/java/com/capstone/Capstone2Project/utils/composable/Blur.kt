package com.capstone.Capstone2Project.utils.composable

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.capstone.Capstone2Project.R
import com.github.mmin18.widget.RealtimeBlurView


@Preview(showBackground = true)
@Composable
fun GlassMorphismPreview() {

}


@Composable
fun GlassMorphismCardBackground(
    modifier: Modifier = Modifier,
    blurRadius: Float = 50f,
    overlayColor: Int = 0x30FFFFFF,
    cornerRadius: Dp = 10.dp,
    useShimmerEffect: Boolean = false,
    durationMillis: Int = 5000,
    content: @Composable () -> Unit
) {

    val shimmerModifier = remember {
        if (useShimmerEffect) {
            Modifier.composed {
                var size by remember {
                    mutableStateOf(IntSize.Zero)
                }

                val transition = rememberInfiniteTransition()

                val startOffsetX by transition.animateFloat(
                    initialValue = -2 * size.width.toFloat(),
                    targetValue = 2 * size.width.toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                                durationMillis = durationMillis,
                        )
                    )
                )

                background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color(0x37FFFFFF),
                            Color.Transparent,
                            Color.Transparent
                        ),
                        start = Offset(startOffsetX, 0f),
                        end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                ).onGloballyPositioned {
                    size = it.size
                }
            }
        } else {
            Modifier
        }
    }


    Box(
        modifier = modifier
            .wrapContentHeight()
    ) {
        GlassMorphismCard(
            blurRadius = blurRadius,
            overlayColor = overlayColor,
            cornerRadius = cornerRadius,
            modifier = shimmerModifier
        )
        content()
    }
}

@Composable
fun GlassMorphismCard(
    modifier: Modifier = Modifier,
    blurRadius: Float = 50f,
    overlayColor: Int = 0x30FFFFFF,
    cornerRadius: Dp = 10.dp,
) {
    GlassMorphism(
        modifier
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0x80ffffff),
                            Color.Transparent,
                            Color(0x80ffffff)
                        )
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clip(shape = RoundedCornerShape(cornerRadius))
            .background(color = Color(0x22000000)),
        blurRadius,
        overlayColor
    )
}


@Composable
fun GlassMorphism(
    modifier: Modifier = Modifier,
    blurRadius: Float = 20f,
    overlayColor: Int = 0x30FFFFFF
) {
    AndroidView(
        factory = {
            View.inflate(it, R.layout.blur_view, null)
        },
        modifier = modifier,
        update = {
            val blurView: RealtimeBlurView? = it.findViewById(R.id.blur_view)

            blurView?.apply {
                setBlurRadius(blurRadius)
                setOverlayColor(overlayColor)
            }
        }
    )
}