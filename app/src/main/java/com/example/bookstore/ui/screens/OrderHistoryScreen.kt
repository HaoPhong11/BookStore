package com.example.bookstore.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.bookstore.data.dto.response.OrderResponse
import com.example.bookstore.utils.toVnd
import com.example.bookstore.viewmodel.AccountViewModel
import com.example.bookstore.ui.theme.AppColors

// ── Status helpers ────────────────────────────────────────────────────────────
private data class StatusInfo(val label: String, val color: Color, val icon: ImageVector)

private fun getStatusInfo(status: String): StatusInfo = when (status.uppercase()) {
    "PENDING"                  -> StatusInfo("Chờ xác nhận",   Color(0xFFFF9800), Icons.Outlined.HourglassEmpty)
    "PROCESSING", "CONFIRMED"  -> StatusInfo("Đang xử lý",     Color(0xFF2196F3), Icons.Outlined.Inventory2)
    "SHIPPED", "SHIPPING"      -> StatusInfo("Đang giao hàng", Color(0xFF9C27B0), Icons.Outlined.LocalShipping)
    "DELIVERED"                -> StatusInfo("Đã giao hàng",   Color(0xFF4CAF50), Icons.Outlined.CheckCircle)
    "CANCELLED"                -> StatusInfo("Đã hủy",         Color(0xFFF44336), Icons.Outlined.Cancel)
    else                       -> StatusInfo(status,            Color.Gray,        Icons.Outlined.HourglassEmpty)
}

private fun normalizeForTab(raw: String): String = when (raw.uppercase()) {
    "CONFIRMED"              -> "PROCESSING"
    "SHIPPING", "DELIVERED"  -> "SHIPPED"
    else                     -> raw.uppercase()
}

private val ORDER_TABS = listOf(
    null         to "Tất cả",
    "PENDING"    to "Chờ xác nhận",
    "PROCESSING" to "Đang xử lý",
    "SHIPPED"    to "Đang giao",
    "CANCELLED"  to "Đã hủy"
)

private fun formatDate(iso: String): String = try {
    val parts = iso.substringBefore("T").split("-")
    "${parts[2]}/${parts[1]}/${parts[0]}"
} catch (_: Exception) { iso }

// ── Screen ────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    initialTab: Int = 0,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Tải lại danh sách mỗi khi màn hình được resume
    // (bao gồm khi quay lại từ app MoMo hoặc browser thanh toán)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadOrderHistory()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val allOrders = viewModel.orderHistory

    // Khi orderHistory reload xong và initialTab=PENDING (1) nhưng không còn PENDING order nào
    // → tự động chuyển sang tab PROCESSING (2) để user thấy đơn mới thanh toán
    LaunchedEffect(allOrders) {
        if (initialTab == 1 && allOrders.isNotEmpty()) {
            val hasPending = allOrders.any { it.status.uppercase() == "PENDING" }
            if (!hasPending) selectedTab = 2  // Chuyển sang "Đang xử lý"
        }
    }

    val filteredOrders = remember(selectedTab, allOrders) {
        val filter = ORDER_TABS[selectedTab].first
        if (filter == null) allOrders
        else allOrders.filter { normalizeForTab(it.status) == filter }
    }

    // Dialog xử lý đơn PENDING: null = đóng
    var pendingActionOrder by remember { mutableStateOf<OrderResponse?>(null) }

    // Khi getRepayUrl thành công → mở MoMo
    val repayResult = viewModel.repayResult
    LaunchedEffect(repayResult) {
        if (repayResult != null) {
            val opened = if (!repayResult.deeplink.isNullOrBlank()) {
                try {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(repayResult.deeplink))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    true
                } catch (_: Exception) { false }
            } else false

            if (!opened && !repayResult.payUrl.isNullOrBlank()) {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(repayResult.payUrl))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            viewModel.resetOrderAction()
        }
    }

    // Dialog xác nhận hủy/thanh toán cho PENDING order
    pendingActionOrder?.let { order ->
        AlertDialog(
            onDismissRequest = {
                pendingActionOrder = null
                viewModel.resetOrderAction()
            },
            title = {
                Text("Đơn hàng #${order.id}", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Bạn muốn làm gì với đơn hàng này?", color = Color.Gray, fontSize = 14.sp)
                    if (viewModel.orderActionError != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(viewModel.orderActionError ?: "", color = Color.Red, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                if (viewModel.orderActionLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AppColors.PrimaryBlue)
                } else {
                    // "Thanh toán MoMo" chỉ hiện nếu phương thức là MOMO
                    TextButton(onClick = {
                        viewModel.getRepayUrl(order.id)
                        pendingActionOrder = null
                    }) {
                        Text("Thanh toán MoMo", color = AppColors.PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                if (!viewModel.orderActionLoading) {
                    TextButton(onClick = {
                        viewModel.cancelOrder(order.id)
                        pendingActionOrder = null
                    }) {
                        Text("Hủy đơn", color = Color(0xFFE53935))
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đơn hàng của tôi", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.PrimaryBlue)
            )
        },
        containerColor = Color(0xFFF0F0F0)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── Tab bar ──────────────────────────────────────────────────
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor   = Color.White,
                contentColor     = AppColors.PrimaryBlue,
                edgePadding      = 0.dp
            ) {
                ORDER_TABS.forEachIndexed { index, (_, label) ->
                    Tab(
                        selected               = selectedTab == index,
                        onClick                = { selectedTab = index },
                        selectedContentColor   = AppColors.PrimaryBlue,
                        unselectedContentColor = Color.Gray,
                        text = {
                            Text(
                                label,
                                fontSize   = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                maxLines   = 1
                            )
                        }
                    )
                }
            }

            // ── Content ──────────────────────────────────────────────────
            when {
                viewModel.orderLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AppColors.PrimaryBlue)
                            Spacer(Modifier.height(12.dp))
                            Text("Đang tải đơn hàng...", color = Color.Gray)
                        }
                    }
                }

                filteredOrders.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.Inventory2,
                                contentDescription = null,
                                tint     = Color(0xFFD0D0D0),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text("Bạn chưa có đơn hàng nào cả", color = Color.Gray, fontSize = 15.sp)
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier            = Modifier.fillMaxSize(),
                        contentPadding      = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredOrders, key = { it.id }) { order ->
                            OrderCard(
                                order    = order,
                                onClick  = if (order.status.uppercase() == "PENDING") {
                                    { pendingActionOrder = order }
                                } else null
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Order Card ────────────────────────────────────────────────────────────────
@Composable
private fun OrderCard(order: OrderResponse, onClick: (() -> Unit)? = null) {
    val statusInfo     = getStatusInfo(order.status)
    val totalItems     = order.items.sumOf { it.quantity }
    val firstBookTitle = order.items.firstOrNull()?.bookTitle?.takeIf { it.isNotBlank() }
    val isPending      = order.status.uppercase() == "PENDING"

    Card(
        shape     = RoundedCornerShape(0.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier  = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Book, contentDescription = null,
                        tint = AppColors.PrimaryBlue, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("BookVerse", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(statusInfo.icon, contentDescription = null,
                        tint = statusInfo.color, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(statusInfo.label, color = statusInfo.color,
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            // Body
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFFE8F0FE), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Book, contentDescription = null,
                        tint = AppColors.PrimaryBlue, modifier = Modifier.size(36.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        when {
                            firstBookTitle != null && order.items.size == 1 -> firstBookTitle
                            firstBookTitle != null -> "$firstBookTitle và ${order.items.size - 1} sản phẩm khác"
                            else -> "Đơn hàng #${order.id}"
                        },
                        fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Người nhận: ${order.receiverName}", color = Color.Gray,
                        fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    Text("x$totalItems cuốn sách", color = Color.Gray, fontSize = 13.sp)
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Đơn #${order.id}", color = Color.Gray, fontSize = 11.sp)
                    Text("Ngày đặt: ${formatDate(order.createdAt)}", color = Color.Gray, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Thành tiền: ", color = Color.Gray, fontSize = 13.sp)
                    Text(order.totalPrice.toVnd(), fontWeight = FontWeight.Bold,
                        fontSize = 14.sp, color = Color(0xFFE53935))
                }
            }

            // Hint bấm vào nếu PENDING
            if (isPending) {
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.TouchApp, contentDescription = null,
                        tint = Color(0xFFFF9800), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Bấm để hủy hoặc thanh toán lại",
                        color = Color(0xFFFF9800), fontSize = 12.sp)
                }
            }
        }
    }
}
