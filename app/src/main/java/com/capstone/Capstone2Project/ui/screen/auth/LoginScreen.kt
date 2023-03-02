package com.capstone.Capstone2Project.ui.screen.auth

import android.media.MediaMuxer.OutputFormat
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key.Companion.U
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.navigation.ROUTE_HOME
import com.capstone.Capstone2Project.navigation.ROUTE_LOGIN
import com.capstone.Capstone2Project.navigation.ROUTE_SIGNUP
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.extensions.onTouchKeyBoardFocusDismiss
import com.capstone.Capstone2Project.utils.theme.LocalSpacing
import com.capstone.Capstone2Project.utils.theme.bright_blue
import com.capstone.Capstone2Project.utils.theme.dim_sky_blue
import com.capstone.Capstone2Project.utils.theme.spacing
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun LoginUIPreview() {
    LoginScreen(
        navController = rememberNavController()
    )
}

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current

    val loginFlow = viewModel.loginFlow.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterVertically),
        modifier = Modifier
            .padding(spacing.medium)
            .fillMaxSize()
            .onTouchKeyBoardFocusDismiss()
    ) {
//        LogoUI()

        LoginUI { email, password ->
            if(email.isBlank()) {
                AlertUtils.showToast(context, "이메일을 입력해주세요")
                return@LoginUI
            }
            if(password.isBlank()) {
                AlertUtils.showToast(context, "비밀번호를 입력해주세요")
                return@LoginUI
            }
            viewModel.login(email, password)
        }
        SignUpComment(navController)
    }

    loginFlow.value?.let {
        when(it) {
            is Resource.Error -> {
                it.error?.message?.let {message->
                    AlertUtils.showToast(context, message, Toast.LENGTH_LONG)
                }
            }
            Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {
                LaunchedEffect(Unit) {
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(ROUTE_HOME) {
                            inclusive = true
                        }
                    }
                }
            }

        }
    }
}




@Composable
fun SignUpComment(
    navController: NavController
) {
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont,
            color = Color.Gray,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.End
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                modifier = Modifier
                    .clickable {
                        navController.navigate(ROUTE_SIGNUP) {
                            popUpTo(ROUTE_LOGIN) {
                                inclusive = true
                            }
                        }
                    },
                text = buildAnnotatedString{
                    append("계정이 없으신가요? ")
                    withStyle(
                        style = SpanStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("회원가입")
                    }
                }
            )
        }


    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginUI(
    buttonClickListener: (String, String)-> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val focusManager = LocalFocusManager.current

    val spacing = LocalSpacing.current

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val buttonPressed by interactionSource.collectIsPressedAsState()

    val contentHeight = 50.dp

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        InfoTextFieldWithTitle(
            onValueChange = {
                            email = it
            },
            title = "Email",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
        InfoTextFieldWithTitle(
            onValueChange = {
                            password = it
            },
            title = "Password",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions {
                keyboardController?.hide()
                focusManager.clearFocus()
            },
            useVisualTransformation = true
        )
        Button(
            modifier = Modifier
                .height(contentHeight)
                .fillMaxWidth(),
            interactionSource = interactionSource,
            onClick = {
                buttonClickListener(
                    email,
                    password
                )
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
                AnimatedVisibility(visible = buttonPressed) {
                    if(buttonPressed) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(
                            modifier = Modifier.width(spacing.medium)
                        )
                    }
                }
                Text(
                    "로그인",
                    color = Color.White,
                    fontFamily = CustomFont.nexonFont,
                    fontWeight = FontWeight.Bold
                )
            }

        }

    }
}

@Composable
fun InfoTextFieldWithTitle(
    modifier: Modifier = Modifier,
    focusColor: Color = dim_sky_blue,
    outFocusColor: Color = Color.Gray,
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    onValueChange: (String)->Unit,
    useVisualTransformation: Boolean = false,
    label: @Composable (()->Unit)? = null,
    title: String,
    emoji: @Composable (()->Unit)? = null,
    isCorrect: Boolean = false
) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont,
            color = Color.DarkGray
        )
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
        ) {

            Row(
                modifier = Modifier
                    .padding(horizontal = spacing.extraSmall)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                emoji?.let {
                    it()
                }
                Text(
                    text = title,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Medium
                )
                AnimatedVisibility(isCorrect) {
                    if(isCorrect) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = bright_blue
                        )
                    }
                }
            }
            InfoTextField(
                onValueChange = onValueChange,
                keyboardActions = keyboardActions,
                keyboardOptions = keyboardOptions,
                focusColor = focusColor,
                outFocusColor = outFocusColor,
                useVisualTransformation = useVisualTransformation,
                label = label
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfoTextField(
    modifier: Modifier = Modifier,
    focusColor: Color = dim_sky_blue,
    outFocusColor: Color = Color.Gray,
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    useVisualTransformation: Boolean = false,
    onValueChange: (String) -> Unit,
    label: @Composable (()->Unit)? = null
) {
    var text by remember {
        mutableStateOf("")
    }

    var textFieldFocused by remember {
        mutableStateOf(false)
    }

    val textFieldColor by animateColorAsState(
        targetValue = if (textFieldFocused) focusColor else outFocusColor
    )

    val textFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.White,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        cursorColor = textFieldColor,
        focusedLabelColor = textFieldColor
    )

    val roundedCornerShape = RoundedCornerShape(15.dp)
    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val coroutineScope = rememberCoroutineScope()

    var textVisible by remember {
        mutableStateOf(false)
    }

    val tailingIconPainterResourceId by animateIntAsState(
        targetValue = if(textVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24
    )

    val visualTransformation =
        if (useVisualTransformation) {
            if (textVisible) VisualTransformation.None
            else PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        }


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            color = Color.DarkGray,
            fontFamily = CustomFont.nexonFont,
            textAlign = TextAlign.Start
        )
    ) {
        TextField(
            colors = textFieldColors,
            value = text,
            onValueChange = {
                text = it
                onValueChange(it)
            },
            shape = roundedCornerShape,
            trailingIcon = {
                if(useVisualTransformation) {
                    Icon(
                        painter = painterResource(id = tailingIconPainterResourceId),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                textVisible = !textVisible
                            },
                        tint = textFieldColor
                    )
                } else {
                    if(text.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    text = ""
                                },
                            tint = textFieldColor
                        )
                    }
                }
            },
            modifier = modifier
                .height(50.dp)
                .fillMaxWidth()
                .border(
                    1.dp,
                    color = textFieldColor,
                    shape = roundedCornerShape
                )
                .background(color = Color.Transparent)
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }
                .onFocusChanged { focusState ->
                    textFieldFocused = focusState.isFocused
                },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            label = label,
            visualTransformation = visualTransformation
        )
    }


}


