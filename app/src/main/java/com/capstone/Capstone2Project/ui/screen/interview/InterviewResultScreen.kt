package com.capstone.Capstone2Project.ui.screen.interview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.InterviewResultContent
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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
                InterviewResultContent(it.data, navController)
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