package com.example.bookstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookstore.ui.components.SearchTopBar

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onNavigateToResults: (String) -> Unit
) {

    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Thanh tìm kiếm
        SearchTopBar(searchText = searchText,
            onSearchTextChange = { searchText = it },
            onBackClick = onBackClick,
            onSearchAction = { query ->
                onNavigateToResults(query)
            } )

        // Nội dung cuộn
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // Xu hướng tìm kiếm
            item { TrendingSearchesSection() }

            // Thanh ngăn cách xám
            item { Divider(color = Color(0xFFF5F5F5), thickness = 8.dp) }

            // Danh mục phổ biến
            item { PopularCategoriesSection() }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}


@Composable
fun TrendingSearchesSection() {
    val trendingList = listOf(
        "Đắc nhân tâm",
        "Nhà giả kim",
        "Tư duy nhanh và chậm",
        "Tư duy phản biện",
        "Người giàu nhất thành Babylon"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Xu hướng tìm kiếm", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        trendingList.forEachIndexed { index, title ->
            val rank = index + 1
            // 🌟 Logic đổi màu: Top 1 Vàng, 2 Xám, 3 Cam, còn lại Xám nhạt
            val bgColor = when (rank) {
                1 -> Color(0xFFFFD54F)
                2 -> Color(0xFFE0E0E0)
                3 -> Color(0xFFFFB74D)
                else -> Color(0xFFF5F5F5)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* TODO: Search cuốn này */ }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cục vuông hiển thị số Rank
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = rank.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(text = title, fontSize = 14.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun PopularCategoriesSection() {
    // Demo data (Icon + Tên danh mục)
    val categories = listOf(
        Pair(Icons.Outlined.Book, "Văn học"),
        Pair(Icons.Outlined.MonetizationOn, "Kinh tế"),
        Pair(Icons.Outlined.Psychology, "Tâm lý học"),
        Pair(Icons.Outlined.Face, "Thiếu nhi"),
        Pair(Icons.Outlined.School, "Giáo dục"),
        Pair(Icons.Outlined.History, "Lịch sử"),
        Pair(Icons.Outlined.Brush, "Nghệ thuật"),
        Pair(Icons.Outlined.Science, "Khoa học"),
        Pair(Icons.Outlined.AccountBalance, "Tôn giáo"),
        Pair(Icons.Outlined.Gavel, "Chính trị"),
        Pair(Icons.Outlined.Restaurant, "Nấu ăn"),
        Pair(Icons.Outlined.Flight, "Du lịch")
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Danh mục phổ biến", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        val chunkedCategories = categories.chunked(2)
        chunkedCategories.forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    CategoryChip(icon = rowItems[0].first, text = rowItems[0].second)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (rowItems.size > 1) {
                        CategoryChip(icon = rowItems[1].first, text = rowItems[1].second)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun CategoryChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5)) // Màu nền xám nhạt
            .clickable { /* TODO: Chuyển trang danh mục */ }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF3B5998), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    }
}