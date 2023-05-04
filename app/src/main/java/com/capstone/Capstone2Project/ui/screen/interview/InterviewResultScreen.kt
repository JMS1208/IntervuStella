package com.capstone.Capstone2Project.ui.screen.interview

import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.InterviewResultContent
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.theme.Capstone2ProjectTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.serialization.json.JsonNull.content

@OptIn(ExperimentalPagerApi::class)
@Composable
fun InterviewResultScreen(
    interviewUUID: String,
    navController: NavController) {

    val systemUiController = rememberSystemUiController()

    val interviewResultViewModel: InterviewResultViewModel = hiltViewModel()

    val interviewResultFlow = interviewResultViewModel.interviewResultFlow.collectAsStateWithLifecycle()

    val context = LocalContext.current

    interviewResultFlow.value?.let {
        when(it) {
            is Resource.Error -> {
                navController.popBackStack()
                AlertUtils.showToast(context, "오류가 발생하였습니다. 잠시 후 시도해주세요.")
            }
            Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {
//                InterviewResultContent(it.data, navController)

                Capstone2ProjectTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                        color = MaterialTheme.colors.background
                    ) {
                        /*
                        Comment
                        왠지는 모르겠지만 Surface로 감싸주어야 터치 오류가 안난다
                        중첩 스크롤을 사용해서 그런가 싶다
                         */

                        InterviewResultMotionScreenContent(
                            interviewResult = it.data,
                            navController = navController
                        )

                    }
                }


            }
        }
    }

    DisposableEffect(systemUiController) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = false,
        )

        interviewResultViewModel.fetchInterviewResult(interviewUUID)

        onDispose {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = true,
            )
        }
    }




}