package com.example.bookstore.ui.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
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
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route

    // CartViewModel được tạo ở đây để chia sẻ giữa CartScreen và CheckoutScreen
    val cartViewModel: CartViewModel = hiltViewModel()
    val accountViewModel: AccountViewModel = hiltViewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

//    // Hiển thị bottom nav trên 4 tab chính và màn hình chi tiết danh mục
//    val mainRoutes    = setOf("home", "category", "cart", "account")
//    val showBottomBar = currentRoute in mainRoutes ||
//            currentRoute?.startsWith("category_detail/") == true


    val showBottomBar = currentRoute in listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Category.route,
        BottomNavItem.Cart.route,
        BottomNavItem.Account.route
    )|| currentRoute?.startsWith("category_detail/") == true

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
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(0)) },
            exitTransition = { fadeOut(animationSpec = tween(0)) },
            popEnterTransition = { fadeIn(animationSpec = tween(0)) },
            popExitTransition = { fadeOut(animationSpec = tween(0)) }
        ) {

            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onNavigateToCategory = {
                        navController.navigate(BottomNavItem.Category.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onBookClick = { bookId ->
                        navController.navigate("book_detail/$bookId")
                    },
                    onSearchClick = {
                        navController.navigate("search_screen")
                    }
                )
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
            composable(
                route = "search_results/{query}",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query") ?: ""
                BookSearchResultsScreen(
                    initialQuery = query,
                    onBackClick = { navController.popBackStack() },
                    onBookClick = { bookId ->
                        navController.navigate("book_detail/$bookId")
                    }
                )
            }
            composable(
                route = "search_screen",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToResults = { query ->
                        navController.navigate("search_results/$query")
                    }
                )
            }
            composable(
                route = "book_detail/{bookId}",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                BookDetailScreen(
                    onBackClick = { navController.popBackStack() },
                    onHomeClick = {
                        navController.navigate(BottomNavItem.Home.route)
                    },
                    onCartClick = {
                        navController.navigate(BottomNavItem.Cart.route)
                    },
                    onCategoryClick = {
                        navController.navigate(BottomNavItem.Category.route)
                    },
                    onAccountClick = {
                        navController.navigate(BottomNavItem.Account.route)
                    },
                    onSearchSubmit = { query ->
                        navController.navigate("search_results/$query")
                    }
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
//            // ---- Chi tiết danh mục (index → Category.all[index]) ----
//            composable(
//                route     = "category_detail/{index}",
//                arguments = listOf(navArgument("index") { type = NavType.IntType; defaultValue = 0 })
//            ) { backStackEntry ->
//                val index    = backStackEntry.arguments?.getInt("index") ?: 0
//                val category = Category.all.getOrElse(index) { Category.all[0] }
//                CategoryDetailScreen(
//                    category      = category,
//                    cartViewModel = cartViewModel,
//                    navController = navController
//                )
//            }
//            composable("profile") {
//                ProfileScreen(navController = navController, viewModel = accountViewModel)
//            }
//            composable("settings") {
//                SettingsScreen(navController = navController)
//            }
//            composable("change_password") {
//                ChangePasswordScreen(navController = navController)
//            }
//            composable("order_history") {
//                OrderHistoryScreen(navController = navController)
//            }
//            composable("contact") {
//                ContactScreen(navController = navController)
//            }
//            // ---- Đăng nhập (có returnRoute để quay lại sau khi login) ----
//            composable(
//                route = "login/{returnRoute}",
//                arguments = listOf(navArgument("returnRoute") {
//                    type = NavType.StringType
//                    defaultValue = "home"
//                })
//            ) { backStackEntry ->
//                val returnRoute = backStackEntry.arguments?.getString("returnRoute") ?: "home"
//                LoginScreen(
//                    navController    = navController,
//                    returnRoute      = returnRoute,
//                    accountViewModel = accountViewModel
//                )
//            }
//            // ---- Đăng ký ----
//            composable("register") {
//                RegisterScreen(navController = navController)
//            }
//        }
//    }
//}


