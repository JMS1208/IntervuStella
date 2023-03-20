package com.capstone.Capstone2Project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.ui.screen.auth.LoginScreen
import com.capstone.Capstone2Project.ui.screen.auth.SignUpScreen
import com.capstone.Capstone2Project.ui.screen.home.HomeScreen
import com.capstone.Capstone2Project.ui.screen.interesting.topic.TopicScreen
import com.capstone.Capstone2Project.ui.screen.interview.InterviewFinishScreen
import com.capstone.Capstone2Project.ui.screen.interview.InterviewResultScreen
import com.capstone.Capstone2Project.ui.screen.interview.InterviewScreen
import com.capstone.Capstone2Project.ui.screen.mypage.MyPageScreen
import com.capstone.Capstone2Project.ui.screen.othersanswers.OthersAnswersScreen
import com.capstone.Capstone2Project.ui.screen.script.ScriptWritingFinishScreen
import com.capstone.Capstone2Project.ui.screen.script.ScriptWritingScreen

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
            HomeScreen(navController)
        }

        composable(ROUTE_TOPIC) {
            TopicScreen(navController)
        }

        composable(
            "$ROUTE_CAMERA/{script}"
        ) { navBackStackEntry ->
            val scriptJson = navBackStackEntry.arguments?.getString("script")

            scriptJson?.let {
                val script = Script.jsonStringToScript(it)
                InterviewScreen(navController, script)
            }
        }

        composable(
            "$ROUTE_INTERVIEW_FINISHED/{interviewUUID}"
        ) { navBackStackEntry ->

            val interviewUUID = navBackStackEntry.arguments?.getString("interviewUUID")

            interviewUUID?.let {
                InterviewFinishScreen(interviewUUID = it, navController = navController)
            }
        }

        composable(
            "$ROUTE_INTERVIEW_RESULT/{interviewUUID}"
        ) { navBackStackEntry ->

            val interviewUUID = navBackStackEntry.arguments?.getString("interviewUUID")

            interviewUUID?.let {
                InterviewResultScreen(interviewUUID = it, navController = navController)
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

            ScriptWritingScreen(navController = navController, _script = script)


        }

        composable("$ROUTE_SCRIPT_WRITING_FINISH/{script}") { navBackStackEntry ->

            val scriptJson = navBackStackEntry.arguments?.getString("script")

            scriptJson?.let {
                val script = Script.jsonStringToScript(it)

                if (script != null) {
                    ScriptWritingFinishScreen(script = script, navController = navController)
                }
            }
        }

        composable(ROUTE_MY_PAGE) {
            MyPageScreen(navController = navController)
        }

        composable("$ROUTE_OTHERS_ANSWERS/{question_uuid}") {navBackStackEntry ->

            val questionUUID = navBackStackEntry.arguments?.getString("question_uuid")

            questionUUID?.let {
                OthersAnswersScreen(questionUUID = it, navController = navController)
            }
        }
    }
}