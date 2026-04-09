package com.example.bookstore.ui.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bookstore.data.model.Book
import com.example.bookstore.ui.components.BookBasicInfo
import com.example.bookstore.ui.components.BookDescription
import com.example.bookstore.ui.components.BookImageHeader
import com.example.bookstore.ui.components.BookReviewsSection
import com.example.bookstore.ui.components.BookSpecifications
import com.example.bookstore.ui.components.DetailTopBar
import com.example.bookstore.ui.components.RelatedProductsSection
import com.example.bookstore.utils.displayPrice
import com.example.bookstore.viewmodel.BookDetailViewModel
import com.example.bookstore.viewmodel.CartViewModel
import kotlinx.coroutines.launch

enum class BadgeType { None, Discount, New }
data class MockReview(val name: String, val date: String, val rating: Int, val content: String)
data class MockBookRelated(val imageUrl: String, val title: String, val author: String, val price: String, val badgeType: BadgeType, val badgeValue: String)

@Composable
fun BookDetailScreen(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onCartClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onSearchSubmit: (String) -> Unit,
    cartViewModel: CartViewModel,
    onNavigateToCheckout: () -> Unit = {},
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val book = viewModel.book
    val isLoading = viewModel.isLoading
    var quantity by remember { mutableStateOf(1) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            DetailTopBar(
                onBackClick = onBackClick,
                onHomeClick = onHomeClick,
                onCartClick = onCartClick,
                onAccountClick = onAccountClick,
                onCategoryClick = onCategoryClick,
                onSearchSubmit = onSearchSubmit
            )
        },
        bottomBar = {
            DetailBottomBar(
                book = book,
                quantity = quantity,
                onDecrement = { if (quantity > 1) quantity-- },
                onIncrement = { quantity++ },
                onAddToCart = {
                    book?.let {
                        cartViewModel.addBook(it.copy(price = it.displayPrice()), quantity)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Đã thêm $quantity \"${it.title}\" vào giỏ hàng",
                                duration = SnackbarDuration.Short
                            )
                        }
                        quantity = 1
                    }
                },
                onBuyNow = {
                    book?.let {
                        cartViewModel.addBook(it.copy(price = it.displayPrice()), quantity)
                        quantity = 1
                        onNavigateToCheckout()
                    }
                }
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        if (isLoading) {
            // Đang load thì xoay vòng
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF3B5998))
            }
        }else if (book != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                item {BookImageHeader(imageUrl = book.imageUrl) }
                item {BookBasicInfo(book = book) }
                item { Divider(color = Color(0xFFEEEEEE), thickness = 8.dp) }
                item { BookDescription(description = book.describe) }
                item { Divider(color = Color(0xFFEEEEEE), thickness = 8.dp) }
                item { BookSpecifications(book= book) }
                item { BookReviewsSection() }
                item { Divider(color = Color(0xFFEEEEEE), thickness = 8.dp) }
                item { RelatedProductsSection() }


                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun DetailBottomBar(
    book: Book?,
    quantity: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit,
) {
    // Dùng Surface để tạo bóng (elevation) đổ lên trên nội dung cuộn
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // NavigationBarsPadding giúp thanh này không bị lẹm vào nút điều hướng ảo của điện thoại
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Bộ chọn số lượng
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFEEEEEE))
            ) {
                Text(
                    "−",
                    modifier = Modifier
                        .clickable(enabled = book != null) { onDecrement() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$quantity",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "+",
                    modifier = Modifier
                        .clickable(enabled = book != null) { onIncrement() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nút Thêm vào giỏ
            OutlinedButton(
                onClick = onAddToCart,
                enabled = book != null,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF3B5998))
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Thêm", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Nút Mua ngay
            Button(
                onClick = onBuyNow,
                enabled = book != null,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998))
            ) {
                Text("Mua ngay", fontSize = 12.sp)
            }
        }
    }
}