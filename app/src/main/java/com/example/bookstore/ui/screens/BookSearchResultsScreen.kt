package com.example.bookstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bookstore.data.model.Book
import com.example.bookstore.ui.components.BookCard
import com.example.bookstore.ui.components.FilterBar
import com.example.bookstore.ui.components.SearchTopBar
import com.example.bookstore.utils.displayPrice
import com.example.bookstore.viewmodel.CartViewModel
import com.example.bookstore.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@Composable
fun BookSearchResultsScreen(
    initialQuery: String,
    onBackClick: () -> Unit,
    onBookClick: (String) -> Unit,
    cartViewModel: CartViewModel,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf(initialQuery) }
    LaunchedEffect(initialQuery) {
        viewModel.searchBooks(initialQuery)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                //Thanh Search
                SearchTopBar(
                    searchText = searchQuery,
                    onSearchTextChange = { newText ->
                        searchQuery = newText
                        // TODO: Sau này gọi API search lại ở đây nếu cần
                    },
                    onBackClick = onBackClick,
                    onSearchAction = { query ->
                        viewModel.searchBooks(query)
                    }
                )
                // Thanh Lọc 
                FilterBar()
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {

            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF3B5998)
                )
            } else if (viewModel.books.isEmpty()) {
                EmptyResultView()
            } else {
                SearchResultContentView(
                    books = viewModel.books,
                    query = searchQuery,
                    onBookClick = onBookClick,
                    onAddToCart = { selectedBook ->
                        // Chuẩn hóa giá tiền
                        cartViewModel.addBook(selectedBook.copy(price = selectedBook.displayPrice()))

                        // Bắn thông báo lên
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Đã thêm \"${selectedBook.title}\" vào giỏ",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )
            }
        }
    }
}



@Composable
fun SearchResultContentView(
    books: List<Book>,
    query: String,
    onBookClick: (String) -> Unit,
    onAddToCart: (Book) -> Unit) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEEEEE)) // Màu xám nhạt
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text("${books.size} kết quả cho “$query”", color = Color.Black, fontSize = 14.sp)
        }

        // Tiêu đề phần Sách
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sách", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
//            Text("Xem tất cả", fontSize = 14.sp, color = Color(0xFF3B5998), modifier = Modifier.clickable { })
        }

        // Lưới sách 2 cột
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 0.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(books) { book ->
                BookCard(book = book, onBookClick = onBookClick,onAddToCart = { onAddToCart(book) })
            }
        }
    }
}

@Composable
fun EmptyResultView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Căn giữa màn hình
    ) {
        // Icon kính lúp lớn
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tiêu đề chính
        Text(
            text = "Không tìm thấy kết quả",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dòng mô tả phụ
        Text(
            text = "Không tìm thấy sản phẩm phù hợp với tìm kiếm của bạn",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}