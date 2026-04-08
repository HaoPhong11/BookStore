package com.example.bookstore.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.bookstore.viewmodel.AccountViewModel
import com.example.bookstore.viewmodel.LoginViewModel
import com.example.bookstore.ui.theme.AppColors

private val PrimaryBlueAcc = AppColors.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    // Reload profile mỗi khi AccountScreen được hiển thị
    // (đảm bảo dữ liệu mới nhất sau login hoặc cập nhật profile)
    LaunchedEffect(Unit) { viewModel.loadProfile() }
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsStateWithLifecycle(initialValue = null)
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == false) {
            navController.navigate("login/account") {
                popUpTo("account") { inclusive = true }
            }
        }
    }
    if (isLoggedIn == null || isLoggedIn == false) return

    // Username từ JWT (sub claim) — hiển thị ngay mà không cần gọi API
    val displayUsername by viewModel.displayUsername.collectAsStateWithLifecycle(initialValue = null)

    var showLogoutDialog by remember { mutableStateOf(false) }

    // ==========================================
    // AnimatedVisibility — PDF §4.1.4 Animation
    // Trạng thái visible = false → true khi màn hình hiển thị,
    // tạo hiệu ứng fadeIn + slideIn cho phần Đơn hàng của tôi.
    // ==========================================
    var showOrderStatus by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { showOrderStatus = true }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Đăng xuất") },
            text  = { Text("Bạn có chắc muốn đăng xuất?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }) { Text("Đăng xuất", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Hủy") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tài khoản", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlueAcc)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- User info card ---
            Card(
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier  = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("profile") }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = viewModel.userProfile?.fullName
                                ?: displayUsername
                                ?: "Người dùng",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp
                        )
                        Text("Xem thông tin tài khoản", color = Color.Gray, fontSize = 13.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                }
            }

            // --- Đơn hàng của tôi ---
            Card(
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("order_history") },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            "Đơn hàng của tôi",
                            color      = PrimaryBlueAcc,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp
                        )
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PrimaryBlueAcc)
                    }
                    Spacer(Modifier.height(16.dp))
                    // AnimatedVisibility: icons trạng thái trượt + fade vào khi màn hình load
                    AnimatedVisibility(
                        visible = showOrderStatus,
                        enter   = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            OrderStatusItem(Icons.Outlined.Schedule,     "Chờ xác nhận")
                            OrderStatusItem(Icons.Outlined.Inventory2,   "Chờ lấy hàng")
                            OrderStatusItem(Icons.Outlined.LocalShipping, "Đang giao hàng")
                            OrderStatusItem(Icons.Outlined.CheckCircle,  "Hoàn tất")
                        }
                    }
                }
            }

            // --- Menu list ---
            Card(
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column {
                    AccountMenuItem(
                        icon    = Icons.Outlined.LocationOn,
                        label   = "Địa chỉ",
                        onClick = { navController.navigate("profile") }
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(horizontal = 16.dp))
                    AccountMenuItem(
                        icon    = Icons.Outlined.Settings,
                        label   = "Cài đặt",
                        onClick = { navController.navigate("settings") }
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(horizontal = 16.dp))
                    AccountMenuItem(
                        icon    = Icons.AutoMirrored.Outlined.HelpOutline,
                        label   = "Trung tâm hỗ trợ",
                        onClick = { navController.navigate("contact") }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // --- Đăng xuất ---
            OutlinedButton(
                onClick  = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape    = RoundedCornerShape(24.dp),
                border   = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color.Red)
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.Red)
                Spacer(Modifier.width(8.dp))
                Text("Đăng xuất", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun OrderStatusItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEEEEE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.DarkGray, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.widthIn(max = 72.dp))
    }
}

@Composable
private fun AccountMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = Color.DarkGray, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 14.sp)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}
