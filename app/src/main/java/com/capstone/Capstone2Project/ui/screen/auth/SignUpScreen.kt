package com.capstone.Capstone2Project.ui.screen.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.navigation.ROUTE_LOGIN
import com.capstone.Capstone2Project.navigation.ROUTE_SIGNUP
import com.capstone.Capstone2Project.navigation.ROUTE_TOPIC
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.extensions.onTouchKeyBoardFocusDismiss
import com.capstone.Capstone2Project.utils.theme.bg_grey
import com.capstone.Capstone2Project.utils.theme.bright_blue
import com.capstone.Capstone2Project.utils.theme.spacing


@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    SignUpScreen(navController = rememberNavController())
}
@OptIn(ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: AuthViewModel? = hiltViewModel()
) {

    var name by remember {
        mutableStateOf("")
    }

    var isNameCorrect by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(name) {
        isNameCorrect = name.isNotBlank()
    }

    var email by remember {
        mutableStateOf("")
    }

    var isEmailCorrect by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(email) {
        isEmailCorrect = email.isValidEmail()
    }


    var password by remember {
        mutableStateOf("")
    }

    var isPasswordCorrect by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(password) {
        isPasswordCorrect = password.isNotBlank()
    }

    var verifyingPassword by remember {
        mutableStateOf("")
    }

    var isVerifyingPasswordCorrect by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(password, verifyingPassword) {
        isVerifyingPasswordCorrect = if (password.isNotBlank() && verifyingPassword.isNotBlank()) {
            password == verifyingPassword
        } else false
    }


    var buttonEnabled by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isNameCorrect, isEmailCorrect, isVerifyingPasswordCorrect) {
        buttonEnabled = isNameCorrect && isEmailCorrect && isVerifyingPasswordCorrect
    }

    val contentHeight = 50.dp

    val interactionSource = remember { MutableInteractionSource() }


    val spacing = MaterialTheme.spacing

    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current

    val signupFlow = viewModel?.signupFlow?.collectAsStateWithLifecycle()

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            color = Color.DarkGray,
            fontSize = 14.sp,
            fontFamily = CustomFont.nexonFont,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start
        )
    ) {

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    title = {
                        Text("회원가입",
                            style = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ))
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color.DarkGray,
                            modifier = Modifier.clickable {
                                navController.navigate(ROUTE_LOGIN) {
                                    popUpTo(ROUTE_SIGNUP) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                )

            }
        ) { innerPadding->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(
                        color = bg_grey
                    )
                    .padding(spacing.medium)
                    .onTouchKeyBoardFocusDismiss(),
                verticalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {


                InfoTextFieldWithTitle(
                    title = "Name",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    onValueChange = {
                        name = it
                    },
                    isCorrect = isNameCorrect
                )

                InfoTextFieldWithTitle(
                    title = "Email",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    onValueChange = {
                        email = it
                    },
                    isCorrect = isEmailCorrect
                )

                InfoTextFieldWithTitle(
                    title = "Password",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    onValueChange = {
                        password = it
                    },
                    useVisualTransformation = true,
                    isCorrect = isPasswordCorrect
                )


                InfoTextFieldWithTitle(
                    title = "Password Verifying",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    },
                    onValueChange = {
                        verifyingPassword = it
                    },
                    useVisualTransformation = true,
                    isCorrect = isVerifyingPasswordCorrect
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(spacing.small))

                AnimatedVisibility(buttonEnabled) {
                    if(buttonEnabled) {
                        Text(
                            "모든 정보를 제대로 입력하셨습니다.",
                            modifier = Modifier.fillMaxWidth(),
                            color = bright_blue,
                            style = LocalTextStyle.current.copy(
                                textAlign = TextAlign.End
                            )
                        )
                    }
                }




                Button(
                    enabled = buttonEnabled,
                    modifier = Modifier
                        .height(contentHeight)
                        .fillMaxWidth(),
                    interactionSource = interactionSource,
                    onClick = {
                        viewModel?.signup(name, email, password)
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    elevation = ButtonDefaults.elevation(0.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AnimatedVisibility(visible = buttonEnabled) {
                            if (buttonEnabled) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(
                                    modifier = Modifier.width(15.dp)
                                )
                            }
                        }
                        Text(
                            "회원가입",
                            color = Color.White,
                            fontFamily = CustomFont.nexonFont,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }

            }

            signupFlow?.value?.let {
                when(it) {
                    is Resource.Error -> {
                        it.error?.message?.let { message->
                            val context = LocalContext.current
                            AlertUtils.showToast(context, message, Toast.LENGTH_LONG)
                        }
                    }
                    Resource.Loading -> {
                        LoadingScreen()
                    }
                    is Resource.Success -> {
                        LaunchedEffect(true) {
                            navController.navigate(ROUTE_TOPIC) {
                                popUpTo(ROUTE_TOPIC) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            }

        }


    }
}