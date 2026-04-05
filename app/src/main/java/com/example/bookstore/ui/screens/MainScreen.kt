package com.example.bookstore.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookstore.data.model.Category
import com.example.bookstore.ui.components.AppBottomNavigation
import com.example.bookstore.ui.components.BottomNavItem
import com.example.bookstore.viewmodel.AccountViewModel
import com.example.bookstore.viewmodel.CartViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // CartViewModel được tạo ở đây để chia sẻ giữa CartScreen và CheckoutScreen
    val cartViewModel: CartViewModel = hiltViewModel()

    // AccountViewModel được tạo ở đây để chia sẻ giữa ProfileScreen và CheckoutScreen
    // → dữ liệu profile tự động pre-fill vào form thanh toán
    val accountViewModel: AccountViewModel = hiltViewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hiển thị bottom nav trên 4 tab chính và màn hình chi tiết danh mục
    val mainRoutes    = setOf("home", "category", "cart", "account")
    val showBottomBar = currentRoute in mainRoutes ||
            currentRoute?.startsWith("category_detail/") == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigation(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen()
            }
            composable(BottomNavItem.Category.route) {
                CategoryScreen(navController = navController)
            }
            composable(BottomNavItem.Cart.route) {
                CartScreen(cartViewModel = cartViewModel, navController = navController)
            }
            composable(BottomNavItem.Account.route) {
                AccountScreen(navController = navController, viewModel = accountViewModel)
            }
            composable("checkout") {
                CheckoutScreen(
                    cartViewModel    = cartViewModel,
                    accountViewModel = accountViewModel,
                    navController    = navController
                )
            }
            // ---- Chi tiết danh mục (index → Category.all[index]) ----
            composable(
                route     = "category_detail/{index}",
                arguments = listOf(navArgument("index") { type = NavType.IntType; defaultValue = 0 })
            ) { backStackEntry ->
                val index    = backStackEntry.arguments?.getInt("index") ?: 0
                val category = Category.all.getOrElse(index) { Category.all[0] }
                CategoryDetailScreen(
                    category      = category,
                    cartViewModel = cartViewModel,
                    navController = navController
                )
            }
            composable("profile") {
                ProfileScreen(navController = navController, viewModel = accountViewModel)
            }
            composable("settings") {
                SettingsScreen(navController = navController)
            }
            composable("change_password") {
                ChangePasswordScreen(navController = navController)
            }
            composable("order_history") {
                OrderHistoryScreen(navController = navController)
            }
            composable("contact") {
                ContactScreen(navController = navController)
            }
            // ---- Đăng nhập (có returnRoute để quay lại sau khi login) ----
            composable(
                route = "login/{returnRoute}",
                arguments = listOf(navArgument("returnRoute") {
                    type = NavType.StringType
                    defaultValue = "home"
                })
            ) { backStackEntry ->
                val returnRoute = backStackEntry.arguments?.getString("returnRoute") ?: "home"
                LoginScreen(
                    navController    = navController,
                    returnRoute      = returnRoute,
                    accountViewModel = accountViewModel
                )
            }
            // ---- Đăng ký ----
            composable("register") {
                RegisterScreen(navController = navController)
            }
        }
    }
}


