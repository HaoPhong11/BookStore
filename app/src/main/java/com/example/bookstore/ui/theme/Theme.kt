package com.example.bookstore.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * ==========================================
 * THEMING — PDF §4.1.3 Các khái niệm cơ bản Jetpack Compose
 * ==========================================
 *
 * BookStoreTheme bọc toàn bộ app trong MaterialTheme,
 * cung cấp colorScheme và typography nhất quán cho tất cả
 * các Composable sử dụng MaterialTheme.colorScheme.*.
 */

private val BookStoreColorScheme = lightColorScheme(
    primary         = Color(0xFF3E5EA5),  // AppColors.PrimaryBlue
    onPrimary       = Color.White,
    secondary       = Color(0xFF6E8CE3),  // AppColors.SecondaryBlue
    onSecondary     = Color.White,
    background      = Color(0xFFF5F5F5),
    onBackground    = Color(0xFF000000),
    surface         = Color.White,
    onSurface       = Color(0xFF000000),
    error           = Color(0xFFF73A33),  // AppColors.PriceColor
    onError         = Color.White,
)

@Composable
fun BookStoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BookStoreColorScheme,
        typography  = Typography(),
        content     = content
    )
}

