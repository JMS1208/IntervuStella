package com.capstone.Capstone2Project

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.capstone.Capstone2Project.ui.screen.interview.InterviewUIScreenContent
import com.capstone.Capstone2Project.ui.screen.interesting.topic.TopicViewModel
import com.capstone.Capstone2Project.ui.screen.interview.InterviewScreen
import com.capstone.Capstone2Project.ui.screen.interview2.InterviewScreen2
import com.capstone.Capstone2Project.utils.composable.BlurPreview
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviewActivity : BaseActivity() {

    private val viewModel by viewModels<TopicViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBaseContent {
            //CameraScreen(rememberNavController())
            //InterviewScreen2(navController = rememberNavController(), null)
//            InterviewUIScreenContent(
//                exitButtonClick = {},
//                showPreviewClick = {}
//            )

            BlurPreview()
        }
    }
}

