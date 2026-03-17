package com.example.bookstore.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bookstore.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val books = viewModel.books
    val isLoading = viewModel.isLoading

    Column {
        if (isLoading) {
            Text(text = "Đang tải danh sách sách từ anh Gúc-gồ...")
        } else {
            // Hiển thị danh sách sách bằng LazyColumn
            LazyColumn {
                items(books) { book ->
                    Text(text = "📚 ${book.title} - 💰 ${book.price} VNĐ")
                }
            }
        }
    }
}