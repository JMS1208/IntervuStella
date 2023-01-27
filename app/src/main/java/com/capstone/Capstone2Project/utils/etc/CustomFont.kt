package com.capstone.Capstone2Project.utils.etc

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.capstone.Capstone2Project.R

object CustomFont {
    val nexonFont = FontFamily(
        Font(R.font.nexon_lv1_gothic, FontWeight.Normal, FontStyle.Normal),
        Font(R.font.nexon_lv1_gothic_light, FontWeight.Light, FontStyle.Normal),
        Font(R.font.nexon_lv1_gothic_bold, FontWeight.Bold, FontStyle.Normal)
    )

}