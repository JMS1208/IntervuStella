package com.capstone.Capstone2Project.ui.screen.interview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.theme.Capstone2ProjectTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonNull.content

@OptIn(ExperimentalPagerApi::class)
@Composable
fun InterviewResultScreen(
    interviewResult: InterviewResult,
    navController: NavController) {

    val systemUiController = rememberSystemUiController()

    val context = LocalContext.current

    var backPressCnt by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(backPressCnt) {
        if(backPressCnt > 0) {
            delay(1000)
            backPressCnt -= 1
        }
    }

    BackHandler {
        if(backPressCnt == 1) {
            navController.navigate(ROUTE_HOME) {
                popUpTo(ROUTE_HOME) {
                    inclusive = true
                }
            }
        } else {
            backPressCnt += 1
            AlertUtils.showToast(context, "한번 더 누르면 홈화면으로 이동해요")
        }
    }


    Capstone2ProjectTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            color = MaterialTheme.colors.background
        ) {
            /*
            Comment
            왠지는 모르겠지만 Surface로 감싸주어야 터치 오류가 안난다
            중첩 스크롤을 사용해서 그런가 싶다
             */

            InterviewResultMotionScreenContent(
                interviewResult = interviewResult,
                navController = navController
            )

        }
    }

    DisposableEffect(systemUiController) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = false,
        )


        onDispose {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = true,
            )
        }
    }

}