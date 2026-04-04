package com.example.bookstore.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Bảng màu chính của ứng dụng BookStore
 * Dùng object thay vì class để tránh xung đột tên với androidx.compose.ui.graphics.Color
 */
object AppColors {
    val PrimaryBlue   = Color(0xFF3E5EA5)
    val SecondaryBlue = Color(0xFF6E8CE3)
    val WhiteGray     = Color(0xFFF1F1F1)
    val Gray          = Color(0xFF8F9196)
    val PriceColor    = Color(0xFFF73A33)
    val StarYellow    = Color(0xFFFFC300)
    val BlackText     = Color(0xFF000000)
}

// ── Top-level color constants (dùng trong module Login/Register của Nghĩa) ──────────────────
// import com.example.bookstore.ui.theme.* để dùng trực tiếp
val PrimaryBlue      = AppColors.PrimaryBlue       // Color(0xFF3E5EA5)
val GrayText         = Color(0xFF666666)
val FacebookBlue     = Color(0xFF1877F2)
val GoogleButtonRed  = Color(0xFFDB4437)
