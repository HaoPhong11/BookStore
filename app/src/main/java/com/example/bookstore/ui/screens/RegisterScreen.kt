package com.example.bookstore.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bookstore.data.dto.request.RegisterRequest
import com.example.bookstore.ui.components.AuthTextField
import com.example.bookstore.ui.components.SocialLoginButton
import com.example.bookstore.ui.theme.*
import com.example.bookstore.viewmodel.AccountViewModel
import com.example.bookstore.viewmodel.AuthState
import com.example.bookstore.viewmodel.AuthViewModel

// ── Navigation wrapper ────────────────────────────────────────────────────────────────────
@Composable
fun RegisterScreen(
    navController: NavController,
    accountViewModel: AccountViewModel = hiltViewModel(),
    viewModel:     AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    // Navigation & side-effects xử lý tập trung tại wrapper — nơi navController trực tiếp có mặt
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(context, (authState as AuthState.Success).message, Toast.LENGTH_SHORT).show()
                navController.navigate("login/account") {
                    popUpTo("register") { inclusive = true }
                }
                // Không cần resetState — ViewModel bị destroy khi screen bị pop
            }
            is AuthState.LoginSuccess -> {
                accountViewModel.loadProfile()
                Toast.makeText(context, "Đăng ký và đăng nhập thành công", Toast.LENGTH_SHORT).show()
                navController.navigate("home") {
                    popUpTo("register") { inclusive = true }
                }
            }
            else -> {}
        }
    }

    RegisterScreenContent(
        authState          = authState,
        onBackClick        = { navController.popBackStack() },
        onRegister         = { viewModel.register(it) },
        onFacebookRegister = { viewModel.loginWithSocial("Facebook") },
        onGoogleRegister   = { viewModel.loginWithSocial("Google") },
        onResetState       = { viewModel.resetState() }
    )
}

// ── UI thuần — Stateless content ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    authState:    AuthState,
    onBackClick:  () -> Unit               = {},
    onRegister:   (RegisterRequest) -> Unit = {},
    onFacebookRegister: () -> Unit          = {},
    onGoogleRegister:   () -> Unit          = {},
    onResetState: () -> Unit               = {}
) {
    var fullName               by remember { mutableStateOf("") }
    var username               by remember { mutableStateOf("") }
    var email                  by remember { mutableStateOf("") }
    var phone                  by remember { mutableStateOf("") }
    var password               by remember { mutableStateOf("") }
    var confirmPassword        by remember { mutableStateOf("") }
    var passwordVisible        by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreeToTerms           by remember { mutableStateOf(false) }

    var fullNameError        by remember { mutableStateOf(false) }
    var usernameError        by remember { mutableStateOf(false) }
    var emailError           by remember { mutableStateOf(false) }
    var phoneError           by remember { mutableStateOf(false) }
    var passwordError        by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var agreeToTermsError    by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Chỉ xử lý Error tại đây (highlight field lỗi + Toast)
    // Navigation được xử lý tập trung ở wrapper RegisterScreen
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            if (authState.message.contains("email", ignoreCase = true) ||
                authState.message.contains("tài khoản", ignoreCase = true)) {
                emailError = true
            }
            onResetState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Đăng ký", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppColors.PrimaryBlue)
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
            Spacer(modifier = Modifier.height(16.dp))

            Text("BOOKVERSE", color = AppColors.PrimaryBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text("Tham gia cùng chúng tôi", color = AppColors.GrayText, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name
            AuthTextField(
                label         = "Họ và tên",
                value         = fullName,
                onValueChange = { fullName = it; if (fullNameError) fullNameError = false },
                placeholder   = "Nhập họ và tên",
                leadingIcon   = Icons.Outlined.Person,
                isError       = fullNameError
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Username
            AuthTextField(
                label         = "Tên đăng nhập",
                value         = username,
                onValueChange = { username = it; if (usernameError) usernameError = false },
                placeholder   = "Nhập tên đăng nhập",
                leadingIcon   = Icons.Outlined.Person,
                isError       = usernameError
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email
            AuthTextField(
                label         = "Email",
                value         = email,
                onValueChange = { email = it; if (emailError) emailError = false },
                placeholder   = "Nhập email",
                leadingIcon   = Icons.Outlined.Email,
                keyboardType  = KeyboardType.Email,
                isError       = emailError
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Phone
            AuthTextField(
                label         = "Số điện thoại",
                value         = phone,
                onValueChange = { phone = it; if (phoneError) phoneError = false },
                placeholder   = "Nhập số điện thoại",
                leadingIcon   = Icons.Outlined.Phone,
                keyboardType  = KeyboardType.Phone,
                isError       = phoneError
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password
            AuthTextField(
                label            = "Mật khẩu",
                value            = password,
                onValueChange    = { password = it; if (passwordError) passwordError = false },
                placeholder      = "Nhập mật khẩu",
                leadingIcon      = Icons.Outlined.Lock,
                isPasswordField  = true,
                passwordVisible  = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible },
                keyboardType     = KeyboardType.Password,
                isError          = passwordError
            )
            // Xác nhận mật khẩu
            Spacer(modifier = Modifier.height(8.dp))

            // Confirm Password
            AuthTextField(
                label            = "Xác nhận mật khẩu",
                value            = confirmPassword,
                onValueChange    = { confirmPassword = it; if (confirmPasswordError) confirmPasswordError = false },
                placeholder      = "Nhập lại mật khẩu",
                leadingIcon      = Icons.Outlined.Lock,
                isPasswordField  = true,
                passwordVisible  = confirmPasswordVisible,
                onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible },
                keyboardType     = KeyboardType.Password,
                isError          = confirmPasswordError
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Terms
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                    Checkbox(
                        checked = agreeToTerms,
                        onCheckedChange = { agreeToTerms = it; if (it) agreeToTermsError = false },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AppColors.PrimaryBlue,
                            uncheckedColor = if (agreeToTermsError) Color.Red else Color(0xFF8E8E8E)
                        )
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))

                val annotatedText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium)) {
                        append("Tôi đồng ý với điều khoản ")
                    }
                    pushStringAnnotation(tag = "terms", annotation = "terms")
                    withStyle(style = SpanStyle(color = AppColors.PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)) {
                        append("Điều khoản dịch vụ")
                    }
                    pop()
                    withStyle(style = SpanStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium)) {
                        append(" và ")
                    }
                    pushStringAnnotation(tag = "privacy", annotation = "privacy")
                    withStyle(style = SpanStyle(color = AppColors.PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)) {
                        append("Chính sách bảo mật")
                    }
                    pop()
                }

                ClickableText(text = annotatedText, onClick = { })
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Button
            Button(
                onClick = {
                    fullNameError        = fullName.isBlank()
                    usernameError        = username.isBlank()
                    emailError           = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    phoneError           = phone.isBlank() || phone.length < 10
                    passwordError        = password.isBlank()
                    confirmPasswordError = confirmPassword.isBlank() || password != confirmPassword
                    agreeToTermsError    = !agreeToTerms

                    if (!fullNameError && !usernameError && !emailError && !phoneError && !passwordError && !confirmPasswordError && !agreeToTermsError) {
                        onRegister(
                            RegisterRequest(
                                username    = username,
                                password    = password,
                                email       = email,
                                fullName    = fullName,
                                phoneNumber = phone
                            )
                        )
                    } else {
                        val msg = when {
                            fullName.isBlank() || username.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank() -> "Vui lòng điền đầy đủ thông tin"
                            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không đúng định dạng"
                            phone.length < 10 -> "Số điện thoại không đúng định dạng"
                            password != confirmPassword -> "Mật khẩu không khớp"
                            !agreeToTerms -> "Vui lòng đồng ý với điều khoản dịch vụ"
                            else -> "Thông tin không hợp lệ"
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue),
                // Chỉ enable khi Idle — khi Loading/Success/Error đều disable
                // để tránh gửi request 2 lần trong khi navigation đang xảy ra
                enabled  = authState is AuthState.Idle
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Đăng ký", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
                Text("  Hoặc đăng ký bằng  ", modifier = Modifier.padding(horizontal = 16.dp), color = AppColors.GrayText, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SocialLoginButton(
                    text            = "Facebook",
                    icon            = "f",
                    backgroundColor = AppColors.FacebookBlue,
                    modifier        = Modifier.weight(1f).height(48.dp),
                    enabled         = authState !is AuthState.Loading,
                    onClick         = onFacebookRegister
                )
                SocialLoginButton(
                    text            = "Google",
                    icon            = "G",
                    backgroundColor = AppColors.GoogleButtonRed,
                    modifier        = Modifier.weight(1f).height(48.dp),
                    enabled         = authState !is AuthState.Loading,
                    onClick         = onGoogleRegister
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Bạn đã có tài khoản? ", fontSize = 14.sp, color = Color.Gray)
                TextButton(onClick = onBackClick) {
                    Text("Đăng nhập", color = AppColors.PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreenContent(authState = AuthState.Idle)
}
