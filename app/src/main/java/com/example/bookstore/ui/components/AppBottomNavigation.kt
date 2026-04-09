package com.example.bookstore.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Bottom Navigation Bar với Nested Navigation Graphs.
 *
 * Cơ chế hoạt động:
 * - Mỗi tab là một navigation graph riêng (home_graph, category_graph, cart_graph, account_graph).
 * - selected: dùng NavDestination.hierarchy để check xem destination hiện tại
 *   có nằm trong graph của tab đó không — tự động highlight đúng tab kể cả
 *   khi đang ở màn hình con (category_detail, profile, settings...).
 * - onClick: navigate đến graphRoute với saveState + restoreState — mỗi tab
 *   giữ nguyên vị trí và trạng thái khi switch qua lại.
 */
@Composable
fun AppBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Category,
        BottomNavItem.Cart,
        BottomNavItem.Account
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor   = MaterialTheme.colorScheme.onSurface
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            NavigationBarItem(
                icon  = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                // hierarchy: tự động highlight tab khi ở BẤT KỲ màn hình nào trong graph đó.
                // Không cần xử lý thủ công từng route (startsWith("category_detail/"), v.v.)
                selected = currentDestination?.hierarchy?.any { it.route == item.graphRoute } == true,
                onClick = {
                    navController.navigate(item.graphRoute) {
                        // Xóa back stack về start destination để tránh stack quá sâu khi switch tab
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true   // Lưu state của tab vừa rời khỏi
                        }
                        launchSingleTop = true  // Không tạo duplicate nếu đã ở tab đó
                        restoreState    = true  // Khôi phục state khi quay lại tab này
                    }
                }
            )
        }
    }
}