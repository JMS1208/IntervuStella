package com.capstone.Capstone2Project.utils.extensions

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.capstone.Capstone2Project.R

@Composable
fun EmojiView(unicode: Int) {
    AndroidView(
        factory = {context->
            AppCompatTextView(context).apply  {
                text = String(Character.toChars(unicode))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                alpha = 1f
                setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }
    )
}