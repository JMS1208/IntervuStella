package com.capstone.Capstone2Project.ui.screen.interview2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle.Event.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.composable.ComposableLifecycle
import com.capstone.Capstone2Project.utils.etc.AlertUtils

@Composable
fun InterviewScreen2(
    navController: NavController,
    script: Script?
) {

    val interviewViewModel2: InterviewViewModel2 = hiltViewModel()

    val state = interviewViewModel2.state.collectAsStateWithLifecycle()

    val context = LocalContext.current


    LaunchedEffect(interviewViewModel2) {
        interviewViewModel2.fetchCustomQuestionnaire(Script.makeTestScript())
    }

    ComposableLifecycle { _, event ->
        when (event) {
            ON_RESUME -> {
                /*
                커스텀 질문지 패치 전이면 로딩 띄우기
                패치 후면 다이얼로그 띄우기,
                진행중이던게 있으면 다시 시작할지,
                시작전이라면 인터뷰 가이드
                 */
                if (state.value.interviewState == InterviewViewModel2.InterviewState2.OnPaused) {
                    interviewViewModel2.restartInterview()
                }

            }
            ON_PAUSE -> {
                /*
                아직 시작 전이면 처리 X
                진행 중이면 상태 처리 후 일시정지
                 */
                interviewViewModel2.pauseInterview()
            }
            ON_DESTROY -> {
                /*
                백업 또는 날리기
                 */
            }
            else -> Unit
        }
    }


    state.value.let {
        when (it.interviewState) {
            is InterviewViewModel2.InterviewState2.OnError -> {
                AlertUtils.showToast(
                    context,
                    (it.interviewState as InterviewViewModel2.InterviewState2.OnError).message
                )
                navController.popBackStack()
            }
            is InterviewViewModel2.InterviewState2.OnFinished -> {

                //인터뷰가 종료되었음을 보여주는 다이얼로그
                InterviewFinishedDialog {

                }
            }
            is InterviewViewModel2.InterviewState2.OnPaused -> {
                AlertUtils.showToast(context, "OnPaused 로 바뀜")
            }
            is InterviewViewModel2.InterviewState2.OnPrepared -> {
                AlertUtils.showToast(context, "OnPrepared 로 바뀜")
                /*
                준비되었다는 다이얼로그 띄우기
                 */

                InterviewPreparedDialog {
                    interviewViewModel2.startInterview()
                }

            }
            is InterviewViewModel2.InterviewState2.InProgress -> {
                AlertUtils.showToast(context, "InProgress 로 바뀜")
                InterviewUIScreenContent2(interviewViewModel2)
            }
            is InterviewViewModel2.InterviewState2.OnReady -> {
                LoadingScreen()
            }
        }
    }
}

@Composable
fun InterviewUIScreenContent2(
    interviewViewModel2: InterviewViewModel2
) {


    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(
            onClick = {

            }
        ) {
            Text("")
        }
    }
}

@Composable
fun InterviewFinishedDialog(
    dismissClick: () -> Unit
) {

    //인터뷰가 종료되었음을 보여주는 다이얼로그

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Button(
            onClick = dismissClick
        ) {
            Text("인터뷰 종료됨")
        }
    }
}


@Composable
fun InterviewPreparedDialog(
    dismissClick: () -> Unit
) {

    //재시작인지도 파악하기
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {

        Button(
            onClick = dismissClick
        ) {
            Text("ㄱㄱ")
        }
    }
}