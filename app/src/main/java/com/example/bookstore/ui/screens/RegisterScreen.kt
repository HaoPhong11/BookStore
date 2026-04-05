package com.example.bookstore.ui.screens

import androidx.compose.foundation.BorderStroke
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bookstore.viewmodel.LoginViewModel

private val RegPrimaryBlue  = Color(0xFF3E5EA5)
private val RegFacebookBlue = Color(0xFF1877F2)
private val RegGoogleRed    = Color(0xFFDB4437)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.clearError() }

    // Đăng ký thành công → về login
    LaunchedEffect(viewModel.registerSuccess) {
        if (viewModel.registerSuccess) {
            navController.navigate("login/account") {
                popUpTo("register") { inclusive = true }
            }
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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
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
            Spacer(modifier = Modifier.height(16.dp))

            // ---- Logo + Slogan ----
            Text(
                text = "BOOKVERSE",
                color = Color(0xFF1A73E8),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Tham gia cùng chúng tôi",
                color = GrayText,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

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
            Spacer(Modifier.height(14.dp))

            Spacer(modifier = Modifier.height(8.dp))

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
            Spacer(Modifier.height(14.dp))

            Spacer(modifier = Modifier.height(8.dp))

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
            Spacer(Modifier.height(14.dp))

            // ---- Mật khẩu ----
            RegPasswordField(
                label       = "Mật khẩu",
                value       = viewModel.regPassword,
                onValueChange = { viewModel.regPassword = it; viewModel.clearError() },
                placeholder = "Nhập mật khẩu",
                visible     = viewModel.regPasswordVisible,
                onToggle    = { viewModel.regPasswordVisible = !viewModel.regPasswordVisible }
            )
            Spacer(Modifier.height(14.dp))

            // ---- Xác nhận mật khẩu ----
            RegPasswordField(
                label       = "Xác nhận mật khẩu",
                value       = viewModel.regConfirmPassword,
                onValueChange = { viewModel.regConfirmPassword = it; viewModel.clearError() },
                placeholder = "Nhập lại mật khẩu",
                visible     = viewModel.regConfirmPassVisible,
                onToggle    = { viewModel.regConfirmPassVisible = !viewModel.regConfirmPassVisible }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ---- Điều khoản ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked  = viewModel.agreedToTerms,
                    onCheckedChange = { viewModel.agreedToTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = RegPrimaryBlue)
                )
                Text(
                    buildAnnotatedString {
                        append("Tôi đồng ý với điều khoản ")
                        withStyle(SpanStyle(color = RegPrimaryBlue, fontWeight = FontWeight.SemiBold)) {
                            append("Điều khoản dịch vụ")
                        }
                        append(" và ")
                        withStyle(SpanStyle(color = RegPrimaryBlue, fontWeight = FontWeight.SemiBold)) {
                            append("Chính sách bảo mật")
                        }
                    },
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            // ---- Thông báo lỗi ----
            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---- Nút Đăng ký ----
            Button(
                onClick = {
                    fullNameError = fullName.isBlank()
                    emailError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    phoneError = phone.isBlank() || phone.length < 10
                    passwordError = password.isBlank()
                    confirmPasswordError = confirmPassword.isBlank() || password != confirmPassword
                    agreeToTermsError = !agreeToTerms

                    if (!fullNameError && !emailError && !phoneError && !passwordError && !confirmPasswordError && !agreeToTermsError) {
                        onRegister(RegisterRequest(email, password, email, fullName))
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
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Đăng ký",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---- Divider ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
                Text(
                    text = "Hoặc đăng ký bằng",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = GrayText,
                    fontSize = 12.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---- Facebook + Google ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick  = { /* TODO */ },
                SocialLoginButton(
                    text = "Facebook",
                    icon = "f",
                    backgroundColor = FacebookBlue,
                    modifier = Modifier.weight(1f).height(48.dp),
                    onClick = {}
                )
                SocialLoginButton(
                    text = "Google",
                    icon = "G",
                    backgroundColor = GoogleButtonRed,
                    shape    = RoundedCornerShape(10.dp),
                    border   = BorderStroke(0.dp, Color.Transparent),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        containerColor = RegFacebookBlue,
                        contentColor   = Color.White
                    )
                ) {
                    Text("f", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Spacer(Modifier.width(6.dp))
                    Text("Đăng ký với facebook", fontSize = 11.sp, maxLines = 1)
                }
                OutlinedButton(
                    onClick  = { /* TODO */ },
                    modifier = Modifier.weight(1f).height(48.dp),
                    onClick = {}
                )
                    shape    = RoundedCornerShape(10.dp),
                    border   = BorderStroke(0.dp, Color.Transparent),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        containerColor = RegGoogleRed,
                        contentColor   = Color.White
                    )
                ) {
                    Text("G", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Spacer(Modifier.width(6.dp))
                    Text("Đăng ký với google", fontSize = 11.sp, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---- Link về Đăng nhập ----
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Bạn đã có tài khoản?", fontSize = 14.sp, color = Color.Gray)
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Đăng nhập", color = RegPrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ---- Reusable text field ----
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegOutlinedField(
    label        : String,
    value        : String,
    onValueChange: (String) -> Unit,
    placeholder  : String,
    leadingIcon  : @Composable (() -> Unit)? = null,
    keyboardType : KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = { Text(placeholder, color = Color.LightGray) },
            leadingIcon   = leadingIcon,
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(10.dp),
            singleLine    = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = RegPrimaryBlue,
                unfocusedBorderColor = Color(0xFFDDDDDD)
            )
        )
    }
}

// ---- Password field with toggle ----
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegPasswordField(
    label        : String,
    value        : String,
    onValueChange: (String) -> Unit,
    placeholder  : String,
    visible      : Boolean,
    onToggle     : () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = { Text(placeholder, color = Color.LightGray) },
            leadingIcon   = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
            trailingIcon  = {
                IconButton(onClick = onToggle) {
                    Icon(
                        if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(10.dp),
            singleLine    = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = RegPrimaryBlue,
                unfocusedBorderColor = Color(0xFFDDDDDD)
            )
        )
    }
}
