package com.example.bookstore.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            TopAppBar(
                title = { Text("Đăng ký", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RegPrimaryBlue)
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
            Spacer(Modifier.height(28.dp))

            // ---- Logo + Slogan ----
            Text(
                "BOOKVERSE",
                color = RegPrimaryBlue,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Tham gia cùng chúng tôi",
                color = Color.Gray,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(28.dp))

            // ---- Họ và tên ----
            RegOutlinedField(
                label       = "Họ và tên",
                value       = viewModel.regFullName,
                onValueChange = { viewModel.regFullName = it; viewModel.clearError() },
                placeholder = "Nhập họ và tên",
                leadingIcon = { Icon(Icons.Default.Person, null, tint = Color.Gray) }
            )
            Spacer(Modifier.height(14.dp))

            // ---- Email ----
            RegOutlinedField(
                label       = "Email",
                value       = viewModel.regEmail,
                onValueChange = { viewModel.regEmail = it; viewModel.clearError() },
                placeholder = "Nhập email",
                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color.Gray) },
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(14.dp))

            // ---- Số điện thoại ----
            RegOutlinedField(
                label       = "Số điện thoại",
                value       = viewModel.regPhone,
                onValueChange = { viewModel.regPhone = it },
                placeholder = "Nhập số điện thoại",
                leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color.Gray) },
                keyboardType = KeyboardType.Phone
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

            Spacer(Modifier.height(16.dp))

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

            // ---- Nút Đăng ký ----
            Button(
                onClick  = { viewModel.register {} },
                enabled  = !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = RegPrimaryBlue)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Đăng ký", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---- Divider ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDDDDD))
                Text("  Hoặc đăng ký bằng  ", color = Color.Gray, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDDDDD))
            }

            Spacer(Modifier.height(16.dp))

            // ---- Facebook + Google ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick  = { /* TODO */ },
                    modifier = Modifier.weight(1f).height(48.dp),
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

            Spacer(Modifier.height(24.dp))

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
