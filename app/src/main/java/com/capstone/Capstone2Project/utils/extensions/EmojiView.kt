package com.capstone.Capstone2Project.utils.extensions

import android.view.ContentInfo
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.capstone.Capstone2Project.R
import org.w3c.dom.Text

@Composable
fun EmojiView(unicode: Int) {
    AndroidView(
        factory = {context->
            TextView(context).apply  {
                text = String(Character.toChars(unicode))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                alpha = 1f
                setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }
    )
}

@Composable
fun WithEmojiView(
    modifier: Modifier = Modifier,
    unicode: Int,
    spacing: Dp = 8.dp,
    content: @Composable ()->Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        modifier = modifier
    ) {
        EmojiView(unicode = unicode)
        content()
    }
}