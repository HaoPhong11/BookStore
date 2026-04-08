package com.example.bookstore.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookstore.data.model.Book
import com.example.bookstore.data.model.CartItem
import com.example.bookstore.utils.toVnd
import com.example.bookstore.viewmodel.CartViewModel
import com.example.bookstore.viewmodel.LoginViewModel
import com.example.bookstore.ui.theme.AppColors

private val PrimaryBlue = AppColors.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    // ---- Auth guard: chưa đăng nhập → về login ----
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsStateWithLifecycle(initialValue = null)
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == false) {
            navController.navigate("login/cart") {
                popUpTo("cart") { inclusive = true }
            }
        }
    }
    // Chờ xác định trạng thái
    if (isLoggedIn == null || isLoggedIn == false) return

    val cartItems = cartViewModel.cartItems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giỏ hàng", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Giỏ hàng trống", color = Color.Gray, fontSize = 16.sp)
            }
            return@Scaffold
        }

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cartItems, key = { it.book.id }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = { cartViewModel.increaseQuantity(item.book.id) },
                        onDecrease = { cartViewModel.decreaseQuantity(item.book.id) },
                        onDelete   = { cartViewModel.removeItem(item.book.id) }
                    )
                }
                item {
                    OrderSummaryCard(
                        subtotal       = cartViewModel.subtotal,
                        shippingFee    = cartViewModel.shippingFee,
                        discountAmount = cartViewModel.discountAmount,
                        total          = cartViewModel.total
                    )
                }
                item {
                    CouponRow(
                        code         = cartViewModel.discountCode,
                        onCodeChange = { cartViewModel.discountCode = it },
                        onApply      = { cartViewModel.applyDiscount() },
                        errorMessage = cartViewModel.discountError
                    )
                }
            }

            // Nút Thanh toán
            Button(
                onClick = { navController.navigate("checkout") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Thanh toán", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.book.imageUrl,
                contentDescription = item.book.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.book.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
                Text(item.book.author, color = Color.Gray, fontSize = 12.sp)
                Text(
                    item.book.price.toVnd(),
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    QuantityButton(label = "−", onClick = onDecrease)
                    Text(
                        "${item.quantity}",
                        modifier = Modifier.padding(horizontal = 14.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    QuantityButton(label = "+", onClick = onIncrease)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
            }
        }
    }
}

@Composable
private fun QuantityButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .border(1.dp, Color.LightGray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(0.dp)) {
            Text(label, fontSize = 16.sp, color = Color.DarkGray)
        }
    }
}

@Composable
private fun OrderSummaryCard(
    subtotal: Double,
    shippingFee: Double,
    discountAmount: Double,
    total: Double
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SummaryRow("Tạm tính", subtotal.toVnd())
            SummaryRow("Phí vận chuyển", shippingFee.toVnd())
            if (discountAmount > 0) {
                SummaryRow("Giảm giá", "-${discountAmount.toVnd()}", valueColor = Color(0xFF2ECC71))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)
            SummaryRow("Tổng cộng", total.toVnd(), bold = true, fontSize = 16)
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    bold: Boolean = false,
    fontSize: Int = 14,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = fontSize.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value,
            fontSize = fontSize.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = valueColor
        )
    }
}

@Composable
private fun CouponRow(
    code: String,
    onCodeChange: (String) -> Unit,
    onApply: () -> Unit,
    errorMessage: String?
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = code,
                onValueChange = onCodeChange,
                placeholder = { Text("Nhập mã giảm giá", color = Color.Gray) },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(28.dp),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onApply,
                modifier = Modifier.height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Áp dụng", color = Color.White)
            }
        }
        if (errorMessage != null) {
            Text(errorMessage, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartItemCardPreview() {
    val sampleBook = Book(
        id       = "preview_1",
        title    = "Đắc Nhân Tâm",
        author   = "Dale Carnegie",
        describe = "",
        imageUrl = "",
        price    = 88000.0
    )
    CartItemCard(
        item       = CartItem(sampleBook, 2),
        onIncrease = {},
        onDecrease = {},
        onDelete   = {}
    )
}

