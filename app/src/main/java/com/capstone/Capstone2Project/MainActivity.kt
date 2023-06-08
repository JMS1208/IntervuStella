package com.capstone.Capstone2Project

import android.os.Bundle
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.core.view.WindowCompat
import com.capstone.Capstone2Project.navigation.AppNavHost
import com.capstone.Capstone2Project.utils.ThemeHelper
import com.capstone.Capstone2Project.utils.etc.CustomFont
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)

        setBaseContent {
            CompositionLocalProvider(
                LocalTextStyle provides TextStyle(
                    fontFamily = CustomFont.nexonFont,
                    color = Color.Black
                )
            ) {
                AppNavHost()
            }
        }

        setupThemeMode()


    }

    private fun setupThemeMode() {
        ThemeHelper.applyTheme(ThemeHelper.ThemeMode.LIGHT)
    }

}
