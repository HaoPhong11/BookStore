package com.example.bookstore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bookstore.ui.components.BookSection
import com.example.bookstore.ui.components.CategorySection
import com.example.bookstore.ui.components.HomeTopBar
import com.example.bookstore.ui.components.PromoBanner
import com.example.bookstore.ui.components.SearchBar
import com.example.bookstore.utils.displayPrice
import com.example.bookstore.viewmodel.CartViewModel
import com.example.bookstore.viewmodel.HomeViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel,
    onNavigateToCategory: () -> Unit,
    onBookClick: (String) -> Unit,
    onSearchClick: () -> Unit)
{
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val books = viewModel.books
    val isLoading = viewModel.isLoading
    android.util.Log.d("TechLead", "HomeScreen Recomposition: isLoading=$isLoading, books size=${books.size}")

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(innerPadding) // Nhận padding từ Scaffold để không bị lẹm UI
        ) {
            item { HomeTopBar() }
            item { SearchBar(onSearchClick = onSearchClick) }
            item { PromoBanner() }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                CategorySection(onViewAllClick = { onNavigateToCategory() })
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF3B5998))
                    }
                } else {
                    BookSection(
                        title = "Sách nổi bật",
                        books = books,
                        onBookClick = onBookClick,
                        onAddToCart = { selectedBook ->

                            // 🌟 4. Logic chuẩn của ông giáo
                            cartViewModel.addBook(selectedBook.copy(price = selectedBook.displayPrice()))

                            // Bắn thông báo lên!
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

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
