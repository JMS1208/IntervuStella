package com.capstone.Capstone2Project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.capstone.Capstone2Project.data.model.InterviewResult
import com.capstone.Capstone2Project.data.model.Questionnaire
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.ui.screen.auth.AuthViewModel
import com.capstone.Capstone2Project.ui.screen.auth.LoginScreen
import com.capstone.Capstone2Project.ui.screen.auth.SignUpScreen
import com.capstone.Capstone2Project.ui.screen.home.HomeScreen
import com.capstone.Capstone2Project.ui.screen.home.HomeViewModel
import com.capstone.Capstone2Project.ui.screen.interesting.topic.TopicScreen
import com.capstone.Capstone2Project.ui.screen.interview.InterviewFinishScreen
import com.capstone.Capstone2Project.ui.screen.interview.InterviewGuideScreen
import com.capstone.Capstone2Project.ui.screen.interview.InterviewResultScreen
import com.capstone.Capstone2Project.ui.screen.mypage.MyPageScreen
import com.capstone.Capstone2Project.ui.screen.comment.CommunityScreen
import com.capstone.Capstone2Project.ui.screen.interview.InterviewScreen2
import com.capstone.Capstone2Project.ui.screen.script.ScriptScreen
import com.capstone.Capstone2Project.ui.screen.script.ScriptViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_LOGIN
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(navController)
        }

        composable(ROUTE_SIGNUP) {
            SignUpScreen(navController)
        }

        composable(ROUTE_HOME) {

            val homeViewModel: HomeViewModel = hiltViewModel()

            val authViewModel: AuthViewModel = hiltViewModel()

            HomeScreen(navController, homeViewModel, authViewModel)
        }

        composable(ROUTE_TOPIC) {
            TopicScreen(navController)
        }


        composable(
            "$ROUTE_CAMERA?questionnaire={questionnaire}",
            arguments = listOf(
                navArgument(
                    "questionnaire"
                ) {
                    defaultValue = null
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            navBackStackEntry ->

            val questionnaireJson = navBackStackEntry.arguments?.getString("questionnaire")

            val questionnaire = if(questionnaireJson == null) null else Questionnaire.jsonToObject(questionnaireJson)

            questionnaire?.let {
                InterviewScreen2(navController = navController, questionnaire = it)
            }
        }


        composable(
            "$ROUTE_INTERVIEW_GUIDE?questionnaire={questionnaire}",
            arguments = listOf(
                navArgument(
                    "questionnaire"
                ) {
                    defaultValue = null
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
                navBackStackEntry ->

            val questionnaireJson = navBackStackEntry.arguments?.getString("questionnaire")

            val questionnaire = if(questionnaireJson == null) null else Questionnaire.jsonToObject(questionnaireJson)

            questionnaire?.let {
                InterviewGuideScreen(navController = navController, questionnaire = it)
            }
        }

        composable(
            "$ROUTE_INTERVIEW_FINISHED?interview_result={interview_result}",
            arguments = listOf(
                navArgument("interview_result") {
                    defaultValue = null
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { navBackStackEntry->
            val interviewResultJson = navBackStackEntry.arguments?.getString("interview_result")

            val interviewResult = if(interviewResultJson == null) null else InterviewResult.jsonStringToInterviewResult(interviewResultJson)

            interviewResult?.let {
                InterviewFinishScreen(interviewResult = it, navController = navController)
            }
        }



        composable(
            "$ROUTE_INTERVIEW_RESULT?interview_result={interview_result}",
            arguments = listOf(
                navArgument("interview_result") {
                    defaultValue = null
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { navBackStackEntry ->

            val interviewResultJson = navBackStackEntry.arguments?.getString("interview_result")

            val interviewResult = if(interviewResultJson == null) null else InterviewResult.jsonStringToInterviewResult(interviewResultJson)

            interviewResult?.let {
                InterviewResultScreen(interviewResult = it, navController = navController)
            }
        }

        composable("$ROUTE_SCRIPT_WRITING?script={script}",
            arguments = listOf(
                navArgument("script") {
                    defaultValue = null
                    type = NavType.StringType
                    nullable = true
                }
            )) { navBackStackEntry ->

            val scriptJson = navBackStackEntry.arguments?.getString("script")

            val script = if (scriptJson == null) null else Script.jsonStringToScript(scriptJson)

            val authViewModel: AuthViewModel = hiltViewModel()

            authViewModel.currentUser?.let { firebaseUser ->

                val viewModel: ScriptViewModel = hiltViewModel()


                ScriptScreen(
                    navController = navController,
                    oriScript = script,
                    firebaseUser = firebaseUser,
                    viewModel = viewModel
                )
            }

        }


        composable(ROUTE_MY_PAGE) {
            MyPageScreen(navController = navController)
        }

        composable("$ROUTE_OTHERS_ANSWERS/{question_uuid}") { navBackStackEntry ->

            val questionUUID = navBackStackEntry.arguments?.getString("question_uuid")
            val authViewModel = hiltViewModel<AuthViewModel>()
            if(authViewModel.currentUser != null) {
                questionUUID?.let {
                    CommunityScreen(questionUUID = it, navController = navController, firebaseUser = authViewModel.currentUser!!)
                }
            }

        }
    }
}