package com.example.bookstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bookstore.viewmodel.AccountViewModel

private val PrimaryBlueCP = Color(0xFF3E5EA5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: AccountViewModel = hiltViewModel()
) {
    // Hộp thoại đổi mật khẩu thành công
    if (viewModel.changePassSuccess) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetChangePassState()
                    navController.popBackStack()
                }) { Text("OK") }
            },
            title = { Text("Thành công") },
            text  = { Text("Mật khẩu của bạn đã được cập nhật.") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Đổi mật khẩu",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlueCP)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text       = "Thay đổi mật khẩu",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        color      = PrimaryBlueCP
                    )

                    HorizontalDivider()

                    // Mật khẩu cũ
                    PasswordInputField(
                        label       = "Mật khẩu hiện tại",
                        value       = viewModel.oldPassword,
                        onValueChange = { viewModel.oldPassword = it },
                        placeholder = "Nhập mật khẩu hiện tại"
                    )

                    // Mật khẩu mới
                    PasswordInputField(
                        label       = "Mật khẩu mới",
                        value       = viewModel.newPassword,
                        onValueChange = { viewModel.newPassword = it },
                        placeholder = "Nhập mật khẩu mới (tối thiểu 6 ký tự)"
                    )

                    // Xác nhận mật khẩu mới
                    PasswordInputField(
                        label       = "Xác nhận mật khẩu mới",
                        value       = viewModel.confirmPassword,
                        onValueChange = { viewModel.confirmPassword = it },
                        placeholder = "Nhập lại mật khẩu mới"
                    )

                    // Thông báo lỗi
                    viewModel.changePassError?.let { error ->
                        Text(
                            text     = error,
                            color    = Color.Red,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Nút xác nhận
            Button(
                onClick  = { viewModel.changePassword() },
                enabled  = !viewModel.changePassLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlueCP)
            ) {
                if (viewModel.changePassLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color    = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Xác nhận đổi mật khẩu", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Nút hủy
            OutlinedButton(
                onClick  = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlueCP)
            ) {
                Text("Hủy", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PasswordInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = { Text(placeholder, fontSize = 13.sp, color = Color.LightGray) },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true,
            shape         = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon  = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                        tint = Color.Gray
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PrimaryBlueCP,
                unfocusedBorderColor = Color(0xFFDDDDDD)
            )
        )
    }
}

