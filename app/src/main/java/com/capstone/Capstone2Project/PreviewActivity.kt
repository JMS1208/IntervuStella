package com.capstone.Capstone2Project

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.capstone.Capstone2Project.ui.screen.interesting.topic.TopicViewModel
import com.capstone.Capstone2Project.ui.screen.interview.InterviewResultScreen
import com.capstone.Capstone2Project.ui.screen.othersanswers.OthersAnswersScreen
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PreviewActivity : BaseActivity() {

    private val viewModel by viewModels<TopicViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBaseContent {

            OthersAnswersScreen("uuid", rememberNavController())
        }
    }
}

