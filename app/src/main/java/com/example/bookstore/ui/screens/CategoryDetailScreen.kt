package com.example.bookstore.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookstore.data.model.Book
import com.example.bookstore.data.model.Category
import com.example.bookstore.ui.components.BookCard
import com.example.bookstore.ui.state.UiState
import com.example.bookstore.ui.theme.AppColors
import com.example.bookstore.utils.toVnd
import com.example.bookstore.utils.displayPrice
import com.example.bookstore.viewmodel.CartViewModel
import com.example.bookstore.viewmodel.CategoryViewModel
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

/**
 * Extension Function: trả về giá hiển thị nhất quán cho sách.
 * Khi Google Books API không cung cấp giá (price == 0.0),
 * tạo giá mock từ hash của ID sách trong khoảng 50.000đ – 240.000đ.
 * PDF §3.1.2 Extension Functions
 */
//private fun Book.displayPrice(): Double =
//    if (price > 0) price
//    else 50_000.0 + (id.hashCode().absoluteValue % 20) * 10_000.0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    category: Category,
    cartViewModel: CartViewModel,
    navController: NavController,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    // Side effect: gọi API khi danh mục thay đổi — PDF §4.1.3 LaunchedEffect
    LaunchedEffect(category.queryKeyword) {
        viewModel.loadBooks(category.queryKeyword)
    }

    // Collect StateFlow<UiState<List<Book>>> — PDF §3.3.2 + §5.1.1 MVVM
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ---- Sort state: DropdownMenu khi nhấn nút Sắp xếp ----
    var showSortMenu by remember { mutableStateOf(false) }
    var sortOption   by remember { mutableStateOf("Mặc định") }
    val sortOptions  = listOf("Mặc định", "Giá tăng dần", "Giá giảm dần", "Tên A-Z")

    // ---- Filter state: AnimatedVisibility panel với khoảng giá ----
    var showFilterPanel    by remember { mutableStateOf(false) }
    var selectedPriceRange by remember { mutableStateOf("Tất cả") }
    val priceRanges = listOf("Tất cả", "Dưới 100k", "100k–200k", "Trên 200k")

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope    = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = category.name, color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.PrimaryBlue)
            )
        },
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ---- Sort + Filter bar ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ---- Nút Sắp xếp → DropdownMenu ----
                // PDF §4.1.3 Material Components: DropdownMenu
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick  = { showSortMenu = true },
                        shape    = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        border   = BorderStroke(1.dp,
                            if (sortOption != "Mặc định") AppColors.PrimaryBlue else Color.LightGray),
                        colors   = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (sortOption != "Mặc định") AppColors.PrimaryBlue else Color.DarkGray)
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Sắp xếp", fontSize = 13.sp)
                    }
                    DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                        sortOptions.forEach { option ->
                            DropdownMenuItem(
                                text        = { Text(option, fontSize = 13.sp) },
                                onClick     = { sortOption = option; showSortMenu = false },
                                leadingIcon = if (sortOption == option) {
                                    { Icon(Icons.Default.Check, contentDescription = null,
                                        tint = AppColors.PrimaryBlue, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }
                }

                // ---- Nút Lọc → AnimatedVisibility panel ----
                OutlinedButton(
                    onClick  = { showFilterPanel = !showFilterPanel },
                    shape    = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    border   = BorderStroke(1.dp,
                        if (showFilterPanel || selectedPriceRange != "Tất cả") AppColors.PrimaryBlue else Color.LightGray),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (showFilterPanel || selectedPriceRange != "Tất cả") AppColors.PrimaryBlue else Color.DarkGray)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Lọc", fontSize = 13.sp)
                }
            }

            // ==========================================
            // AnimatedVisibility — PDF §4.1.4 Animation
            // expandVertically/shrinkVertically: panel lọc trượt xuống/lên mượt mà
            // ==========================================
            AnimatedVisibility(
                visible = showFilterPanel,
                enter   = expandVertically(),
                exit    = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text("Khoảng giá:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    // Row cuộn ngang — FilterChip: Higher-Order Function forEach — PDF §3.1.3
                    Row(
                        modifier              = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        priceRanges.forEach { range ->
                            FilterChip(
                                selected = selectedPriceRange == range,
                                onClick  = { selectedPriceRange = range; showFilterPanel = false },
                                label    = { Text(range, fontSize = 12.sp) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AppColors.PrimaryBlue,
                                    selectedLabelColor     = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // ==========================================
            // when (uiState) — Sealed Class PDF §3.1.4 + §3.3.2
            // ==========================================
            when (val state = uiState) {

                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AppColors.PrimaryBlue)
                            Spacer(Modifier.height(12.dp))
                            Text("Đang tải sách...", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }

                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                            Text("⚠️ Không tải được sách", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(state.message, color = Color.Gray, fontSize = 13.sp)
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadBooks(category.queryKeyword) },
                                colors  = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue)
                            ) { Text("Thử lại") }
                        }
                    }
                }

                is UiState.Success -> {
                    // Bước 1: Sắp xếp — Control Flow: when — PDF §2.1.4
                    // Dùng displayPrice() để sắp xếp đúng kể cả sách price == 0 từ API
                    val sorted: List<Book> = when (sortOption) {
                        "Giá tăng dần"  -> state.data.sortedBy           { it.displayPrice() }
                        "Giá giảm dần"  -> state.data.sortedByDescending { it.displayPrice() }
                        "Tên A-Z"       -> state.data.sortedBy           { it.title }
                        else            -> state.data
                    }

                    // Bước 2: Lọc theo khoảng giá — Higher-Order Function: filter{} — PDF §3.1.3
                    val books: List<Book> = when (selectedPriceRange) {
                        "Dưới 100k"  -> sorted.filter { it.displayPrice() < 100_000 }
                        "100k–200k"  -> sorted.filter { it.displayPrice() in 100_000.0..200_000.0 }
                        "Trên 200k"  -> sorted.filter { it.displayPrice() > 200_000 }
                        else         -> sorted
                    }

                    if (books.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                                Text("Không tìm thấy sách phù hợp", color = Color.Gray, fontSize = 14.sp)
                                if (selectedPriceRange != "Tất cả") {
                                    Spacer(Modifier.height(8.dp))
                                    TextButton(onClick = { selectedPriceRange = "Tất cả" }) {
                                        Text("Xóa bộ lọc", color = AppColors.PrimaryBlue)
                                    }
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns               = GridCells.Fixed(2),
                            modifier              = Modifier.fillMaxSize().padding(12.dp),
                            verticalArrangement   = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(books, key = { it.id }) { book ->
                                BookCard(
                                    book = book,
                                    // 🌟 1. Bổ sung Action nhấn vào thẻ để chuyển trang chi tiết
                                    onBookClick = { bookId ->
                                        navController.navigate("book_detail/$bookId")
                                    },
                                    // 🌟 2. Action thêm giỏ hàng của ông (Giữ nguyên logic rất xịn của ông)
                                    onAddToCart = {
                                        // book.copy(price = book.displayPrice()):
                                        // Normalize giá trước khi lưu vào giỏ hàng.
                                        // Nếu API trả về giá USD thô (vd: 1.71) hoặc 0.0,
                                        // displayPrice() sẽ convert sang giá VND hợp lệ.
                                        // Không dùng book thô vì CartScreen dùng book.price trực tiếp.
                                        cartViewModel.addBook(book.copy(price = book.displayPrice()))

                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message  = "Đã thêm \"${book.title}\" vào giỏ",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Thẻ sách trong màn hình Chi tiết danh mục (Figma 5).
 * Hiển thị: ảnh bìa, badge giảm giá, tiêu đề, tác giả,
 * đánh giá sao, giá sale (đỏ) + giá gốc (gạch ngang), nút thêm giỏ.
 */
//@Composable
//fun BookCard(
//    book: Book,
//    onAddToCart: () -> Unit
//) {
//    // displayPrice() — Extension Function — PDF §3.1.2
//    // Luôn trả về giá > 0, kể cả khi API không có giá
//    val price         = book.displayPrice()
//    // Mock discount: hash ID để mỗi sách có % giảm khác nhau — Null Safety: .absoluteValue
//    val discountPct   = (book.id.hashCode().absoluteValue % 20 + 5)        // 5% – 24%
//    val originalPrice = price / (1 - discountPct / 100.0)
//    // Mock rating 3–5 sao + số lượt đánh giá 50–249 — dựa trên hash ID
//    val rating      = 3 + (book.id.hashCode().absoluteValue % 3)
//    val reviewCount = 50 + (book.id.hashCode().absoluteValue % 200)
//
//    // height(340.dp) cố định — tính toán với minLines=2:
//    // • Image: 150dp | Padding ×2: 16dp → content space: 340-150-16 = 174dp
//    // • Top group  : title(2 dòng ~40) + author(~17) + stars(~17) = 74dp
//    // • Bottom group: price(~20) + origPrice(~17) + spacer(6) + button(34) = 77dp
//    // • Tổng cần: 151dp < 174dp ✓  buffer 23dp → nút luôn hiển thị đầy đủ
//    Card(
//        shape     = RoundedCornerShape(12.dp),
//        colors    = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(2.dp),
//        modifier  = Modifier.fillMaxWidth().height(360.dp)
//    ) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            // Ảnh bìa + badge giảm giá
//            Box(modifier = Modifier.fillMaxWidth()) {
//                AsyncImage(
//                    model              = book.imageUrl,
//                    contentDescription = book.title,
//                    contentScale       = ContentScale.Crop,
//                    modifier           = Modifier
//                        .fillMaxWidth()
//                        .height(150.dp)
//                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
//                )
//                // Discount badge — luôn hiện vì displayPrice() đảm bảo price > 0
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.TopEnd)
//                        .padding(6.dp)
//                        .clip(RoundedCornerShape(4.dp))
//                        .background(AppColors.PriceColor)
//                        .padding(horizontal = 4.dp, vertical = 2.dp)
//                ) {
//                    Text(
//                        text       = "-$discountPct%",
//                        color      = Color.White,
//                        fontSize   = 10.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//
//            // weight(1f): vùng text chiếm hết 135dp còn lại,
//            // SpaceBetween đẩy nút "Thêm vào giỏ" xuống đáy card
//            Column(
//                modifier            = Modifier.padding(8.dp).weight(1f),
//                verticalArrangement = Arrangement.SpaceBetween
//            ) {
//                // --- Phần trên: tiêu đề, tác giả, sao ---
//                Column {
//                    Text(
//                        text       = book.title,
//                        fontWeight = FontWeight.Bold,
//                        fontSize   = 13.sp,
//                        minLines   = 2,   // title ngắn vẫn chiếm 2 dòng → card căn đều
//                        maxLines   = 2,
//                        overflow   = TextOverflow.Ellipsis
//                    )
//                    Text(
//                        text     = book.author,
//                        color    = Color.Gray,
//                        fontSize = 11.sp,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                    // Sao + số lượt đánh giá — Figma style: "★★★★☆ (120)"
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        repeat(5) { i ->
//                            Text(
//                                text  = if (i < rating) "★" else "☆",
//                                color = if (i < rating) AppColors.StarYellow else Color.LightGray,
//                                fontSize = 12.sp
//                            )
//                        }
//                        Spacer(Modifier.width(3.dp))
//                        Text(
//                            text     = "($reviewCount)",
//                            color    = Color.Gray,
//                            fontSize = 10.sp
//                        )
//                    }
//                }
//
//                // --- Phần dưới: giá + nút (luôn ở đáy card) ---
//                Column {
//                    Text(
//                        text       = price.toVnd(),
//                        color      = AppColors.PriceColor,
//                        fontWeight = FontWeight.Bold,
//                        fontSize   = 13.sp
//                    )
//                    Text(
//                        text           = originalPrice.toVnd(),
//                        color          = Color.Gray,
//                        fontSize       = 11.sp,
//                        textDecoration = TextDecoration.LineThrough
//                    )
//                    Spacer(Modifier.height(6.dp))
//                    Button(
//                        onClick        = onAddToCart,
//                        shape          = RoundedCornerShape(8.dp),
//                        colors         = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue),
//                        modifier       = Modifier.fillMaxWidth().height(34.dp),
//                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
//                    ) {
//                        Icon(
//                            Icons.Default.ShoppingCart,
//                            contentDescription = null,
//                            modifier = Modifier.size(14.dp),
//                            tint     = Color.White
//                        )
//                        Spacer(Modifier.width(4.dp))
//                        Text("Thêm vào giỏ", color = Color.White, fontSize = 11.sp)
//                    }
//                }
//            }
//        }
//    }
//}

//// ==========================================
//// @Preview — PDF §4.1.3
//// ==========================================
//@Preview(showBackground = true, widthDp = 200)
//@Composable
//private fun BookCardPreview() {
//    BookCard(
//        book = Book(
//            id       = "preview_1",
//            title    = "Đắc Nhân Tâm",
//            author   = "Dale Carnegie",
//            describe = "",
//            imageUrl = "",
//            price    = 76500.0
//        ),
//        onAddToCart = {}
//    )
//}
