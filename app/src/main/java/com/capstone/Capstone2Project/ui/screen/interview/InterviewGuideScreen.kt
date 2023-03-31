package com.capstone.Capstone2Project.ui.screen.interview

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.navigation.ROUTE_CAMERA
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.navigation.ROUTE_INTERVIEW_GUIDE
import com.capstone.Capstone2Project.utils.RequestPermissions
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.service.ScreenRecordService
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Preview(showBackground = true)
@Composable
private fun Preview() {
    InterviewGuideScreen(navController = rememberNavController(), script = null)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun InterviewGuideScreen(
    navController: NavController,
    script: Script?
) {

    val spacing = LocalSpacing.current

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/circle_rocket.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

//    val isChecked = remember {
//        mutableStateOf(true)
//    }


    val context = LocalContext.current

    val requestPermissionsForRecording = remember {
        mutableStateOf(false)
    }

    val permissions = remember {
        listOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.INTERNET)
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

//    val screenRecordLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) {
//        if (it.resultCode != RESULT_OK) {
//            return@rememberLauncherForActivityResult
//        }
//        if (it.data == null) {
//            return@rememberLauncherForActivityResult
//        }
//
//        startRecordingService(context, it.resultCode, it.data!!)
//
//    }


    if (requestPermissionsForRecording.value) {
        RequestPermissions(permissionState = permissionState) {
            requestPermissionsForRecording.value = false
        }
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Text(
                            "시작 전 안내",
                            style = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color.DarkGray,
                            modifier = Modifier.clickable {
                                navController.popBackStack()
                            }
                        )
                    }
                )
            }
        ) { innerPadding ->


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = bg_grey)
                    .padding(spacing.medium),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = White,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = spacing.medium)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.medium),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "AI 면접 가이드", style = LocalTextStyle.current.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 22.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                "면접은 아래와 같이 진행돼요 !",
                                style = LocalTextStyle.current.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Gray
                                )
                            )
                        }



                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            bright_blue,
                                            bright_purple
                                        )
                                    )
                                )
                        ) {}

                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(150.dp)
                                .padding(spacing.medium)
                        )

                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.medium)

                    ) {

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "1. 면접 질문",
                                style = LocalTextStyle.current.copy(
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                "면접 질문은 최대 4~5개로 구성돼요.\n자기소개서와 관심주제를 바탕으로 질문이 만들어져요.",
                                style = LocalTextStyle.current.copy(
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.medium))

                        }
                        Spacer(modifier = Modifier.height(spacing.medium))
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "2. 면접 답변",
                                style = LocalTextStyle.current.copy(
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                "음성인식을 통해 답변할 수 있어요.\n인터뷰에 집중하기 위해 한번에 쭉 답변하고\n다음 질문으로 넘어가기 전에\n답변에 오타가 있다면 수정해보세요.",
                                style = LocalTextStyle.current.copy(
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.medium))

                        }
                        Spacer(modifier = Modifier.height(spacing.medium))
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "3. 실시간 피드백",
                                style = LocalTextStyle.current.copy(
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                "웃는 모습, 목소리 크기, 자세 등 면접 도중\n피드백을 실시간으로 받아볼 수 있어요.",
                                style = LocalTextStyle.current.copy(
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.medium))

                        }
                        Spacer(modifier = Modifier.height(spacing.medium))
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "4. 면접 점수",
                                style = LocalTextStyle.current.copy(
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing.small))

                            Text(
                                "면접을 보고 나의 점수를 확인해보세요.\n점수는 마이페이지에 업데이트됩니다.",
                                style = LocalTextStyle.current.copy(
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Start
                                )
                            )


                        }

                        Spacer(modifier = Modifier.height(spacing.medium))
//                        Column(
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Text(
//                                "5. 동영상 녹화",
//                                style = LocalTextStyle.current.copy(
//                                    color = Color.Black,
//                                    fontSize = 16.sp,
//                                    fontWeight = FontWeight.SemiBold,
//                                    textAlign = TextAlign.Start
//                                )
//                            )
//
//                            Spacer(modifier = Modifier.height(spacing.small))
//
//                            Text(
//                                "면접과정을 녹화할 수 있어요.\n면접이 종료된 후 동영상으로 다시 확인해보세요.",
//                                style = LocalTextStyle.current.copy(
//                                    color = Color.Black,
//                                    fontSize = 14.sp,
//                                    fontWeight = FontWeight.Normal,
//                                    textAlign = TextAlign.Start
//                                )
//                            )
//
//
//                        }

                    }


//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = spacing.medium),
//                        horizontalAlignment = Alignment.End,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//
//                        Row(
//                            modifier = Modifier.wrapContentWidth(),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//
//                            Checkbox(
//                                checked = isChecked.value,
//                                onCheckedChange = {
//                                    isChecked.value = it
//                                },
//                                colors = CheckboxDefaults.colors(
//                                    checkedColor = bright_blue,
//                                    uncheckedColor = Color.LightGray,
//                                    checkmarkColor = White
//                                )
//                            )
//
//                            Text(
//                                "동영상 녹화 사용",
//                                color = Black,
//                                style = LocalTextStyle.current.copy(
//                                    fontSize = 16.sp,
//                                    fontWeight = FontWeight(550)
//                                )
//
//                            )
//
//
//                        }
//                        Text(
//                            "면접이 종료된 후 동영상으로 다시 확인해보세요",
//                            style = LocalTextStyle.current.copy(
//                                color = Gray,
//                                fontSize = 13.sp,
//                                fontWeight = FontWeight.Normal,
//                                textAlign = TextAlign.End
//                            )
//                        )
//                    }

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.medium)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {

                                if(permissionState.allPermissionsGranted) {
                                    navController.navigate(
                                        "$ROUTE_CAMERA/{script}".replace(
                                            oldValue = "{script}",
                                            newValue = (script ?: Script.makeTestScript()).toJsonString()
                                        )
                                    ) {

                                        popUpTo(ROUTE_HOME) {

                                        }
                                    }
                                } else {
                                    requestPermissionsForRecording.value = true
                                }


                            }
                        ) {
                            Text(
                                "면접 시작",
                                style = LocalTextStyle.current.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = text_blue
                                ),
                                modifier = Modifier

                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                contentDescription = null,
                                imageVector = Icons.Default.ChevronRight,
                                tint = text_blue
                            )
                        }

                    }


                }
            }


        }

    }
}


fun startRecordingService(context: Context, resultCode: Int, data: Intent) {
    if (!isServiceRunning(
            context,
            ScreenRecordService::class.java
        )
    ) {
        val intent = ScreenRecordService.newIntent(context, resultCode, data)
        context.startService(intent)
    }
}

fun stopRecordingService(context: Context) {
    if (isServiceRunning(
            context,
            ScreenRecordService::class.java
        )
    ) {
        val intent = Intent(context, ScreenRecordService::class.java)
        context.stopService(intent)
    }
}


fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}