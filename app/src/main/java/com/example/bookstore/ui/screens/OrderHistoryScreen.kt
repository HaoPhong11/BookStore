package com.example.bookstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bookstore.data.dto.response.OrderResponse
import com.example.bookstore.utils.toVnd
import com.example.bookstore.viewmodel.AccountViewModel
import com.example.bookstore.ui.theme.AppColors

private val PrimaryBlueOH = AppColors.PrimaryBlue


private fun statusLabel(status: String): String = when (status.uppercase()) {
    "PENDING"   -> "Chờ xác nhận"
    "CONFIRMED" -> "Đã xác nhận"
    "SHIPPING"  -> "Đang giao hàng"
    "DELIVERED" -> "Đã giao hàng"
    "CANCELLED" -> "Đã hủy"
    else        -> status
}

private fun statusColor(status: String): Color = when (status.uppercase()) {
    "PENDING"   -> Color(0xFFFF9800)
    "CONFIRMED" -> Color(0xFF2196F3)
    "SHIPPING"  -> Color(0xFF9C27B0)
    "DELIVERED" -> Color(0xFF4CAF50)
    "CANCELLED" -> Color(0xFFF44336)
    else        -> Color.Gray
}

private fun statusIcon(status: String): ImageVector = when (status.uppercase()) {
    "PENDING"   -> Icons.Outlined.HourglassEmpty
    "CONFIRMED" -> Icons.Outlined.Inventory2
    "SHIPPING"  -> Icons.Outlined.LocalShipping
    "DELIVERED" -> Icons.Outlined.CheckCircle
    else        -> Icons.Outlined.HourglassEmpty
}

// Định dạng ngày từ ISO string (2025-06-01T12:00:00) → 01/06/2025
private fun formatDate(iso: String): String {
    return try {
        val parts = iso.substringBefore("T").split("-")
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } catch (_: Exception) {
        iso
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    viewModel: AccountViewModel = hiltViewModel()
) {
    // Tải lịch sử đơn hàng khi vào màn hình
    LaunchedEffect(Unit) {
        viewModel.loadOrderHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Đơn hàng của tôi",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlueOH)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        when {
            // Đang tải
            viewModel.orderLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryBlueOH)
                        Spacer(Modifier.height(12.dp))
                        Text("Đang tải đơn hàng...", color = Color.Gray)
                    }
                }
            }

            // Không có đơn hàng nào
            viewModel.orderHistory.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.Inventory2,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Bạn chưa có đơn hàng nào",
                            color    = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Hiển thị danh sách đơn hàng
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(viewModel.orderHistory, key = { it.id }) { order ->
                        OrderHistoryCard(order = order)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(order: OrderResponse) {
    Card(
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Dòng tiêu đề: mã đơn + trạng thái
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Đơn hàng #${order.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = PrimaryBlueOH
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector   = statusIcon(order.status),
                        contentDescription = null,
                        tint          = statusColor(order.status),
                        modifier      = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text     = statusLabel(order.status),
                        color    = statusColor(order.status),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Người nhận
            OrderInfoRow(label = "Người nhận", value = order.receiverName)

            // Ngày đặt
            OrderInfoRow(label = "Ngày đặt", value = formatDate(order.createdAt))

            // Số lượng sản phẩm
            val totalItems = order.items.sumOf { it.quantity }
            OrderInfoRow(
                label = "Số sản phẩm",
                value = "$totalItems cuốn"
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Tổng tiền
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Tổng tiền", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    text       = order.totalPrice.toVnd(),
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = PrimaryBlueOH
                )
            }
        }
    }
}

@Composable
private fun OrderInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

