package com.example.bookstore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.bookstore.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCategory: () -> Unit,
    onBookClick: (String) -> Unit,
    onSearchClick: () -> Unit) {

    val books = viewModel.books
    val isLoading = viewModel.isLoading
    android.util.Log.d("TechLead", "HomeScreen Recomposition: isLoading=$isLoading, books size=${books.size}")
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        item { HomeTopBar() }
        item { SearchBar(onSearchClick = onSearchClick) }
        item { PromoBanner() }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { CategorySection(
            onViewAllClick = { onNavigateToCategory() }
        ) }
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

                BookSection(title = "Sách nổi bật", books = books, onBookClick = onBookClick)
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}
