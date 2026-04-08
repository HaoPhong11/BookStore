package com.example.bookstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bookstore.viewmodel.AccountViewModel
import com.example.bookstore.ui.theme.AppColors

private val PrimaryBlueProfile = AppColors.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AccountViewModel = hiltViewModel()
) {
    // Thông báo lưu thành công
    // Chỉ reset updateSuccess khi user bấm OK (không dùng LaunchedEffect vì sẽ reset ngay lập tức)
    if (viewModel.updateSuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.updateSuccess = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateSuccess = false
                    navController.popBackStack()
                }) { Text("OK") }
            },
            title = { Text("Thành công") },
            text  = { Text("Thông tin tài khoản đã được cập nhật.") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin tài khoản", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlueProfile)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileField(label = "Họ tên", value = viewModel.editFullName) {
                        viewModel.editFullName = it
                    }
                    ProfileField(label = "Email", value = viewModel.editEmail) {
                        viewModel.editEmail = it
                    }
                    ProfileField(label = "Số điện thoại", value = viewModel.editPhone) {
                        viewModel.editPhone = it
                    }
                    ProfileField(label = "Tỉnh/Thành phố", value = viewModel.editProvince) {
                        viewModel.editProvince = it
                    }
                    ProfileField(label = "Quận/Huyện", value = viewModel.editDistrict) {
                        viewModel.editDistrict = it
                    }
                    ProfileField(label = "Địa chỉ chi tiết", value = viewModel.editAddress, minLines = 2) {
                        viewModel.editAddress = it
                    }

                    viewModel.profileError?.let {
                        Spacer(Modifier.height(4.dp))
                        Text(it, color = Color.Red, fontSize = 13.sp)
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Hủy", color = Color.Gray)
                        }
                        Button(
                            onClick  = { viewModel.saveProfile() },
                            enabled  = !viewModel.isLoading,
                            colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlueProfile)
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(
                                    color    = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text("Lưu thay đổi", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    minLines: Int = 1,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(8.dp),
            singleLine    = minLines == 1,
            minLines      = minLines
        )
    }
}

