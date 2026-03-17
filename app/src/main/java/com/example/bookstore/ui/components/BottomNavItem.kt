package com.example.bookstore.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Trang chủ", Icons.Outlined.Home)
    object Category : BottomNavItem("category", "Danh mục", Icons.Outlined.GridView)
    object Cart : BottomNavItem("cart", "Giỏ hàng", Icons.Outlined.ShoppingCart)
    object Account : BottomNavItem("account", "Tài khoản", Icons.Outlined.Person)
}