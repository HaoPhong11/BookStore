package com.example.bookstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookstore.data.model.Category
import com.example.bookstore.ui.theme.AppColors

private val PrimaryBlueCat = AppColors.PrimaryBlue
private val CategoryIconBg = Color(0xFFEEF2FF)

/**
 * Màn hình Danh mục — lưới 2 cột thể loại sách (Figma 4).
 * PDF §4.1.4 — LazyVerticalGrid, Scaffold, Card, @Preview
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh mục", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlueCat)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        LazyVerticalGrid(
            columns               = GridCells.Fixed(2),
            modifier              = Modifier.fillMaxSize().padding(padding).padding(12.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Higher-Order Function lambda truyền vào items{} — PDF §3.1.3
            items(Category.all.size) { index ->
                val category = Category.all[index]
                CategoryCard(
                    category = category,
                    onClick  = { navController.navigate("category_detail/$index") }
                )
            }
        }
    }
}

/** Composable tái sử dụng — hiển thị 1 thẻ danh mục */
@Composable
fun CategoryCard(category: Category, onClick: () -> Unit) {
    Card(
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier         = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(CategoryIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(getCategoryIcon(category.name), contentDescription = category.name,
                    tint = PrimaryBlueCat, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(category.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("${category.bookCount} sách", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

/**
 * Ánh xạ tên danh mục → Material Icon — Control Flow: when — PDF §2.1.4
 */
fun getCategoryIcon(name: String): ImageVector = when (name) {
    "Văn học"    -> Icons.AutoMirrored.Outlined.MenuBook
    "Kinh tế"    -> Icons.AutoMirrored.Outlined.TrendingUp
    "Tâm lý học" -> Icons.Outlined.Psychology
    "Thiếu nhi"  -> Icons.Outlined.ChildCare
    "Giáo khoa"  -> Icons.Outlined.School
    "Lịch sử"    -> Icons.Outlined.AccountBalance
    "Nghệ thuật" -> Icons.Outlined.Palette
    "Khoa học"   -> Icons.Outlined.Science
    "Tôn giáo"   -> Icons.Outlined.SelfImprovement
    "Chính trị"  -> Icons.Outlined.Gavel
    "Nấu ăn"     -> Icons.Outlined.Restaurant
    "Du lịch"    -> Icons.Outlined.Flight
    else         -> Icons.AutoMirrored.Outlined.MenuBook
}

// @Preview — xem trước mà không cần chạy app — PDF §4.1.3
@Preview(showBackground = true, widthDp = 180)
@Composable
private fun CategoryCardPreview() {
    CategoryCard(category = Category("Văn học", "subject:fiction", 568), onClick = {})
}
