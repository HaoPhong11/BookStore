package com.example.bookstore.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bookstore.data.model.RegisterRequest
import com.example.bookstore.ui.components.AuthTextField
import com.example.bookstore.ui.components.SocialLoginButton
import com.example.bookstore.ui.theme.*
import com.example.bookstore.viewmodel.AuthState
import com.example.bookstore.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()

    RegisterScreenContent(
        authState = authState,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onRegister = { viewModel.register(it) },
        onResetState = { viewModel.resetState() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    authState: AuthState,
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRegister: (RegisterRequest) -> Unit = {},
    onResetState: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }

    // Error states
    var fullNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var agreeToTermsError by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(context, (authState as AuthState.Success).message, Toast.LENGTH_SHORT).show()
                onLoginClick()
                onResetState()
            }
            is AuthState.Error -> {
                val message = (authState as AuthState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                if (message.contains("Email đã tồn tại", ignoreCase = true) || message.contains("409", ignoreCase = true)) {
                    emailError = true
                }
                onResetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Đăng ký",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryBlue
                )
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
            Spacer(modifier = Modifier.height(8.dp))

            // App Title
            Text(
                text = "BOOKVERSE",
                color = Color(0xFF1A73E8),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            // Subtitle
            Text(
                text = "Tham gia cùng chúng tôi",
                color = GrayText,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Họ và tên
            AuthTextField(
                label = "Họ và tên",
                value = fullName,
                onValueChange = { 
                    fullName = it
                    if (fullNameError) fullNameError = false
                },
                placeholder = "Nhập họ và tên",
                leadingIcon = Icons.Outlined.Person,
                isError = fullNameError
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            AuthTextField(
                label = "Email",
                value = email,
                onValueChange = { 
                    email = it
                    if (emailError) emailError = false
                },
                placeholder = "Nhập email",
                leadingIcon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
                isError = emailError
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Số điện thoa
            AuthTextField(
                label = "Số điện thoại",
                value = phone,
                onValueChange = { 
                    phone = it
                    if (phoneError) phoneError = false
                },
                placeholder = "Nhập số điện thoại",
                leadingIcon = Icons.Outlined.Phone,
                keyboardType = KeyboardType.Phone,
                isError = phoneError
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Mật khẩu
            AuthTextField(
                label = "Mật khẩu",
                value = password,
                onValueChange = { 
                    password = it
                    if (passwordError) passwordError = false
                },
                placeholder = "Nhập mật khẩu",
                leadingIcon = Icons.Outlined.Lock,
                isPasswordField = true,
                passwordVisible = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible },
                keyboardType = KeyboardType.Password,
                isError = passwordError
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Xác nhận mật khẩu
            AuthTextField(
                label = "Xác nhận mật khẩu",
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    if (confirmPasswordError) confirmPasswordError = false
                },
                placeholder = "Nhập lại mật khẩu",
                leadingIcon = Icons.Outlined.Lock,
                isPasswordField = true,
                passwordVisible = confirmPasswordVisible,
                onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible },
                keyboardType = KeyboardType.Password,
                isError = confirmPasswordError
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Điều khoản dịch vụ và bảo mật
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                    Checkbox(
                        checked = agreeToTerms,
                        onCheckedChange = { 
                            agreeToTerms = it
                            if (it) agreeToTermsError = false
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4469B0),
                            uncheckedColor = if (agreeToTermsError) Color.Red else Color(0xFF8E8E8E)
                        )
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))

                val annotatedText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)) {
                        append("Tôi đồng ý với điều khoản ")
                    }
                    pushStringAnnotation(tag = "terms", annotation = "terms")
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF1A73E8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Điều khoản dịch vụ")
                    }
                    pop()
                    withStyle(style = SpanStyle(color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)) {
                        append(" và ")
                    }
                    pushStringAnnotation(tag = "privacy", annotation = "privacy")
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF1A73E8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Chính sách bảo mật")
                    }
                    pop()
                }

                ClickableText(
                    text = annotatedText,
                    onClick = { offset ->
                        // Handle links
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nút đăng ký
            Button(
                onClick = {
                    // Reset errors
                    fullNameError = fullName.isBlank()
                    emailError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    phoneError = phone.isBlank() || phone.length < 10
                    passwordError = password.isBlank()
                    confirmPasswordError = confirmPassword.isBlank() || password != confirmPassword
                    agreeToTermsError = !agreeToTerms

                    if (!fullNameError && !emailError && !phoneError && !passwordError && !confirmPasswordError && !agreeToTermsError) {
                        onRegister(RegisterRequest(fullName, password, email, fullName))
                    } else {
                        val errorMsg = when {
                            fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank() -> 
                                "Vui lòng điền đầy đủ thông tin"
                            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                                "Email không đúng định dạng"
                            phone.length < 10 ->
                                "Số điện thoại không đúng định dạng"
                            password != confirmPassword ->
                                "Mật khẩu không khớp"
                            !agreeToTerms ->
                                "Vui lòng đồng ý với điều khoản dịch vụ"
                            else -> "Thông tin không hợp lệ"
                        }
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4469B0)),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Đăng ký",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
                Text(
                    text = "Hoặc đăng ký bằng",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Đăng ký bằng tài khoản xã hội
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SocialLoginButton(
                    text = "Facebook",
                    icon = "f",
                    backgroundColor = FacebookBlue,
                    modifier = Modifier.weight(1f).height(44.dp),
                    onClick = {}
                )
                SocialLoginButton(
                    text = "Google",
                    icon = "G",
                    backgroundColor = GoogleButtonRed,
                    modifier = Modifier.weight(1f).height(44.dp),
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(text = "Bạn đã có tài khoản? ", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onLoginClick, contentPadding = PaddingValues(0.dp)) {
                    Text(
                        text = "Đăng nhập",
                        color = Color(0xFF1A73E8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreenContent(authState = AuthState.Idle)
}
