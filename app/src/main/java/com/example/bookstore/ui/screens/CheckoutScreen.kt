package com.example.bookstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.LocalAtm
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import android.content.Intent
import android.net.Uri
import com.example.bookstore.utils.toVnd
import com.example.bookstore.viewmodel.AccountViewModel
import com.example.bookstore.viewmodel.CartViewModel
import com.example.bookstore.viewmodel.CheckoutViewModel
import com.example.bookstore.ui.theme.AppColors

private val PrimaryBlue2 = AppColors.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartViewModel:    CartViewModel,
    navController:    NavController,
    accountViewModel: AccountViewModel = hiltViewModel(),
    checkoutViewModel: CheckoutViewModel = hiltViewModel()
) {
    // ==========================================
    // rememberSaveable — PDF §4.1.3
    // ==========================================
    // Khác với remember{}: rememberSaveable lưu state vào Bundle,
    // giúp form KHÔNG bị xóa khi xoay màn hình (configuration change).
    // → Người dùng không mất dữ liệu đã nhập khi xoay ngang màn hình.
    var receiverName    by rememberSaveable { mutableStateOf("") }
    var receiverPhone   by rememberSaveable { mutableStateOf("") }
    var receiverEmail   by rememberSaveable { mutableStateOf("") }
    var province        by rememberSaveable { mutableStateOf("") }
    var district        by rememberSaveable { mutableStateOf("") }
    var detailedAddress by rememberSaveable { mutableStateOf("") }
    var note            by rememberSaveable { mutableStateOf("") }
    // Track whether initial pre-fill has been done (chỉ fill 1 lần khi mở màn hình)
    var isPrefilled     by rememberSaveable { mutableStateOf(false) }

    // Đảm bảo profile được load khi mở màn hình
    // (user có thể đi thẳng Cart → Checkout mà không qua AccountScreen)
    LaunchedEffect(Unit) {
        accountViewModel.loadProfile()
    }

    // Pre-fill form từ profile khi userProfile được load lần đầu
    // LaunchedEffect chạy lại mỗi khi userProfile thay đổi (load xong từ API)
    val profile = accountViewModel.userProfile
    LaunchedEffect(profile) {
        if (!isPrefilled && profile != null) {
            receiverName    = profile.fullName
            receiverPhone   = profile.phoneNumber ?: ""
            receiverEmail   = profile.email       ?: ""
            province        = profile.province    ?: ""
            district        = profile.district    ?: ""
            detailedAddress = profile.address     ?: ""
            isPrefilled = true
        }
    }

    // Dialog thành công + ghi ngược thông tin giao hàng về profile
    if (checkoutViewModel.orderSuccess) {
        // Lưu thông tin đã dùng khi đặt hàng về profile để lần sau pre-fill sẵn
        LaunchedEffect(Unit) {
            accountViewModel.applyShippingInfo(
                name     = receiverName,
                phone    = receiverPhone,
                email    = receiverEmail,
                province = province,
                district = district,
                address  = detailedAddress
            )
        }
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = {
                    checkoutViewModel.resetState()
                    cartViewModel.clearCart()
                    // Điều hướng đến màn hình đơn hàng, tab "Đang xử lý" (index 2)
                    navController.navigate("order_history?success=true") {
                        popUpTo("home") { inclusive = false }
                    }
                }) { Text("Xem đơn hàng của tôi") }
            },
            title = { Text("Đặt hàng thành công 🎉") },
            text  = { Text("Đơn hàng COD của bạn đã được ghi nhận. Cảm ơn bạn đã mua hàng!") }
        )
    }

    // Mở MoMo khi pendingPayUrl được set
    val context = LocalContext.current
    val payUrl    = checkoutViewModel.pendingPayUrl
    val deeplink  = checkoutViewModel.pendingDeeplink
    LaunchedEffect(payUrl) {
        if (payUrl != null) {
            // Lưu thông tin giao hàng về profile trước khi rời màn hình
            accountViewModel.applyShippingInfo(
                name     = receiverName,
                phone    = receiverPhone,
                email    = receiverEmail,
                province = province,
                district = district,
                address  = detailedAddress
            )
            cartViewModel.clearCart()

            // Thử mở app MoMo qua deeplink, fallback sang browser
            val opened = if (!deeplink.isNullOrBlank()) {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    true
                } catch (_: Exception) { false }
            } else false

            if (!opened) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(payUrl))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }

            checkoutViewModel.resetState()
            // Chuyển về đơn hàng, tab "Đang xử lý" (index 2)
            // ON_RESUME sẽ tự reload danh sách, tab sẽ ở PROCESSING khi MoMo thanh toán thành công
            navController.navigate("order_history?success=true") {
                popUpTo("home") { inclusive = false }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue2)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---- Thông tin đơn hàng ----
            SectionCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🛍 ", fontSize = 18.sp)
                    Text("Thông tin đơn hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                CheckoutSummaryRow("Tạm tính",       cartViewModel.subtotal.toVnd())
                CheckoutSummaryRow("Phí vận chuyển", cartViewModel.shippingFee.toVnd())
                CheckoutSummaryRow("Giảm giá",       cartViewModel.discountAmount.toVnd(), valueColor = Color(0xFF2ECC71))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                CheckoutSummaryRow("Tổng cộng",      cartViewModel.total.toVnd(), bold = true, fontSize = 16)
            }

            // ---- Thông tin giao hàng ----
            SectionCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍 ", fontSize = 18.sp)
                    Text("Thông tin giao hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(Modifier.height(12.dp))

                FormLabel("Họ và tên")
                FormField(value = receiverName, onValueChange = { receiverName = it },
                    placeholder = "Nhập họ và tên")

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Số điện thoại")
                        FormField(value = receiverPhone, onValueChange = { receiverPhone = it },
                            placeholder = "Nhập số điện thoại")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Email")
                        FormField(value = receiverEmail, onValueChange = { receiverEmail = it },
                            placeholder = "Nhập email")
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Tỉnh/Thành phố")
                        FormField(value = province, onValueChange = { province = it },
                            placeholder = "Nhập tỉnh/thành phố")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Quận/Huyện")
                        FormField(value = district, onValueChange = { district = it },
                            placeholder = "Nhập quận/huyện")
                    }
                }

                FormLabel("Địa chỉ chi tiết")
                FormField(value = detailedAddress, onValueChange = { detailedAddress = it },
                    placeholder = "Số nhà, tên đường, phường/xã...")

                FormLabel("Ghi chú (Không bắt buộc)")
                FormField(value = note, onValueChange = { note = it },
                    placeholder = "Ghi chú về đơn hàng, ví dụ: thời gian hay chỉ dẫn địa điểm giao hàng cụ thể",
                    minLines = 2)
            }

            // ---- Phương thức thanh toán ----
            SectionCard {
                Text("Phương thức thanh toán", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))

                data class PaymentMethod(val value: String, val title: String, val subtitle: String, val icon: ImageVector)
                val methods = listOf(
                    PaymentMethod("COD",  "Thanh toán khi nhận hàng (COD)", "Thanh toán bằng tiền mặt khi nhận hàng", Icons.Outlined.LocalAtm),
                    PaymentMethod("MOMO", "Ví MoMo", "Thanh toán qua ứng dụng MoMo (chuyển hướng đến MoMo)", Icons.Outlined.PhoneIphone)
                )
                methods.forEach { method ->
                    PaymentMethodItem(
                        icon     = method.icon,
                        title    = method.title,
                        subtitle = method.subtitle,
                        selected = checkoutViewModel.paymentMethod == method.value,
                        onClick  = { checkoutViewModel.paymentMethod = method.value }
                    )
                    if (method.value != "MOMO") HorizontalDivider(color = Color(0xFFEEEEEE))
                }
            }

            // Error message
            checkoutViewModel.errorMessage?.let {
                Text(it, color = Color.Red, fontSize = 13.sp)
            }

            // ---- Nút Đặt hàng ----
            Button(
                onClick = {
                    // Truyền rememberSaveable state vào ViewModel khi submit
                    checkoutViewModel.placeOrder(
                        cartItems       = cartViewModel.cartItems.toList(),
                        subtotal        = cartViewModel.subtotal,
                        shippingFee     = cartViewModel.shippingFee,
                        discount        = cartViewModel.discountAmount,
                        receiverName    = receiverName,
                        receiverPhone   = receiverPhone,
                        receiverEmail   = receiverEmail,
                        province        = province,
                        district        = district,
                        detailedAddress = detailedAddress,
                        note            = note
                    )
                },
                enabled  = !checkoutViewModel.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(28.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue2)
            ) {
                if (checkoutViewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (checkoutViewModel.paymentMethod == "MOMO") "Đặt hàng & Thanh toán MoMo"
                        else "Đặt hàng",
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun CheckoutSummaryRow(
    label: String, value: String,
    bold: Boolean = false, fontSize: Int = 14,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = fontSize.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontSize = fontSize.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, color = valueColor)
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(text, fontWeight = FontWeight.Medium, fontSize = 13.sp,
        modifier = Modifier.padding(bottom = 4.dp, top = 8.dp))
}

@Composable
private fun FormField(
    value: String, onValueChange: (String) -> Unit,
    placeholder: String, minLines: Int = 1
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = { Text(placeholder, color = Color.Gray, fontSize = 13.sp) },
        modifier      = Modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(10.dp),
        singleLine    = minLines == 1,
        minLines      = minLines
    )
}

@Composable
private fun PaymentMethodItem(
    icon: ImageVector, title: String, subtitle: String,
    selected: Boolean, onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected, onClick = onClick,
            colors   = RadioButtonDefaults.colors(selectedColor = PrimaryBlue2)
        )
        Spacer(Modifier.width(8.dp))
        Icon(icon, contentDescription = null, tint = PrimaryBlue2, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(subtitle, color      = Color.Gray,        fontSize = 12.sp)
        }
    }
}
