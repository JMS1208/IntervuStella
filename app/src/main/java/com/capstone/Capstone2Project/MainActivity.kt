package com.capstone.Capstone2Project

import InterviewScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.data.model.Questionnaire
import com.capstone.Capstone2Project.navigation.AppNavHost
import com.capstone.Capstone2Project.ui.screen.interview.InterviewResultMotionScreenContent
import com.capstone.Capstone2Project.ui.screen.interview.InterviewScreen2
import com.capstone.Capstone2Project.utils.ThemeHelper
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.theme.Capstone2ProjectTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.interfaces.Detector.TYPE_FACE_DETECTION
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
//                InterviewScreen2(navController = rememberNavController(),
//                    questionnaire = Questionnaire.createTestQuestionnaire())
            }
        }

        setupThemeMode()


    }

    private fun setupThemeMode() {
        ThemeHelper.applyTheme(ThemeHelper.ThemeMode.LIGHT)
    }

}
