package com.example.bookstore.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bookstore.data.dto.request.LoginRequest
import com.example.bookstore.data.dto.response.JwtResponse
import com.example.bookstore.ui.components.AuthTextField
import com.example.bookstore.ui.components.SocialLoginButton
import com.example.bookstore.ui.theme.*
import com.example.bookstore.viewmodel.AccountViewModel
import com.example.bookstore.viewmodel.AuthState
import com.example.bookstore.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.first

// ── Navigation wrapper — tích hợp Nghĩa's UI với NavController của dự án ──────────────────
@Composable
fun LoginScreen(
    navController:    NavController,
    returnRoute:      String         = "home",
    accountViewModel: AccountViewModel = hiltViewModel(),
    viewModel:        AuthViewModel    = hiltViewModel()
) {
    // Check đã login rồi → navigate ngay
    LaunchedEffect(Unit) {
        val alreadyLoggedIn = viewModel.isLoggedIn.first()
        if (alreadyLoggedIn) {
            navController.navigate(returnRoute) {
                popUpTo("login/{returnRoute}") { inclusive = true }
            }
        }
    }

    LoginScreenContent(
        authState             = viewModel.authState.collectAsState().value,
        onBackClick           = { navController.popBackStack() },
        onLoginSuccess        = {
            // Load profile ngay sau login → pre-fill ProfileScreen & CheckoutScreen
            accountViewModel.loadProfile()
            navController.navigate(returnRoute) {
                popUpTo("login/{returnRoute}") { inclusive = true }
            }
        },
        onLogin               = { viewModel.login(it) },
        onRegisterClick       = { navController.navigate("register") },
        onForgotPasswordClick = { /* TODO: quên mật khẩu */ },
        onResetState          = { viewModel.resetState() }
    )
}

// ── UI thuần (của Nghĩa) — không phụ thuộc NavController, dễ test/preview ─────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    authState:             AuthState,
    onBackClick:           () -> Unit              = {},
    onLoginSuccess:        (JwtResponse) -> Unit   = {},
    onLogin:               (LoginRequest) -> Unit  = {},
    onRegisterClick:       () -> Unit              = {},
    onForgotPasswordClick: () -> Unit              = {},
    onResetState:          () -> Unit              = {}
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe      by remember { mutableStateOf(false) }
    var emailError      by remember { mutableStateOf(false) }
    var passwordError   by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.LoginSuccess -> {
                Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                onLoginSuccess(authState.response)
                onResetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, authState.message, Toast.LENGTH_SHORT).show()
                onResetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Đăng nhập", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Text("BOOKVERSE", color = PrimaryBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                "Khám phá thế giới qua từng trang sách",
                color = GrayText, fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(32.dp))

            // Email
            AuthTextField(
                label        = "Email",
                value        = email,
                onValueChange = { email = it; if (emailError) emailError = false },
                placeholder  = "Nhập địa chỉ email",
                leadingIcon  = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
                isError      = emailError
            )

            Spacer(Modifier.height(16.dp))

            // Mật khẩu
            AuthTextField(
                label           = "Mật khẩu",
                value           = password,
                onValueChange   = { password = it; if (passwordError) passwordError = false },
                placeholder     = "Nhập mật khẩu",
                leadingIcon     = Icons.Outlined.Lock,
                isPasswordField = true,
                passwordVisible = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible },
                keyboardType    = KeyboardType.Password,
                isError         = passwordError
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe, onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                    )
                    Text("Ghi nhớ mật khẩu", fontSize = 14.sp)
                }
                TextButton(onClick = onForgotPasswordClick, contentPadding = PaddingValues(0.dp)) {
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    emailError    = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    passwordError = password.isBlank()

                    if (!emailError && !passwordError) {
                        // In many backends username is email
                        onLogin(LoginRequest(email, password))
                    } else {
                        val msg = when {

                            email.isBlank() || password.isBlank() -> "Vui lòng điền đầy đủ thông tin"
                            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không đúng định dạng"
                            else -> "Thông tin không hợp lệ"
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled  = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Đăng nhập", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
                Text("  Hoặc đăng nhập bằng  ", modifier = Modifier.padding(horizontal = 4.dp), color = GrayText, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
            }

            Spacer(Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SocialLoginButton(
                    text            = "Đăng nhập với Facebook",
                    icon            = "f",
                    backgroundColor = FacebookBlue,
                    modifier        = Modifier.weight(1f),
                    onClick         = {}
                )
                SocialLoginButton(
                    text            = "Đăng nhập với Google",
                    icon            = "G",
                    backgroundColor = GoogleButtonRed,
                    modifier        = Modifier.weight(1f),
                    onClick         = {}
                )
            }

            Spacer(Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text("Bạn chưa có tài khoản? ", fontSize = 14.sp)
                TextButton(onClick = onRegisterClick, contentPadding = PaddingValues(0.dp)) {
                    Text(
                        text = "Đăng kí ngay",
                        color = PrimaryBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(authState = AuthState.Idle)
}
