package com.capstone.Capstone2Project.utils.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.theme.dim_sky_blue
import com.capstone.Capstone2Project.utils.theme.highlight_yellow

@Preview(showBackground = true)
@Composable
fun HighlightTextPreview() {
    HighlightText(text = "아무말")
}

@Composable
fun HighlightText(
    text: String,
    highlightColor: Color = highlight_yellow,
    offset: Dp = 10.dp,
    alpha: Float = 0.4f,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont
        )
    ) {
        ConstraintLayout(
            modifier = Modifier.wrapContentSize()
        ) {
            val (textRef, bgRef) = createRefs()

            Box(
                modifier = Modifier
                    .alpha(alpha)
                    .background(
                        color = highlightColor
                    )
                    .constrainAs(bgRef) {
                        top.linkTo(textRef.top, offset)
                        bottom.linkTo(textRef.bottom)
                        start.linkTo(textRef.start)
                        end.linkTo(textRef.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )
            Text(
                text = text,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                overflow = overflow,
                softWrap = softWrap,
                modifier = modifier.constrainAs(textRef) {

                },
                style = style,
                onTextLayout = onTextLayout,
                maxLines = maxLines
            )

        }
    }


}