package com.example.bookstore.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Đại diện cho một tab trong Bottom Navigation Bar.
 *
 * @param route       Route của màn hình chính (dùng để xác định startDestination của graph)
 * @param graphRoute  Route của navigation graph (dùng để switch tab — navigate đến graph)
 * @param title       Nhãn hiển thị
 * @param icon        Icon
 *
 * Với Nested Navigation Graphs, mỗi tab là một sub-graph riêng biệt.
 * AppBottomNavigation dùng [graphRoute] để navigate, và dùng hierarchy của
 * NavDestination để xác định tab nào đang active — không cần check route thủ công.
 */
sealed class BottomNavItem(
    val route: String,
    val graphRoute: String,
    val title: String,
    val icon: ImageVector
) {
    object Home     : BottomNavItem("home",     "home_graph",     "Trang chủ", Icons.Outlined.Home)
    object Category : BottomNavItem("category", "category_graph", "Danh mục",  Icons.Outlined.GridView)
    object Cart     : BottomNavItem("cart",     "cart_graph",     "Giỏ hàng",  Icons.Outlined.ShoppingCart)
    object Account  : BottomNavItem("account",  "account_graph",  "Tài khoản", Icons.Outlined.Person)
}