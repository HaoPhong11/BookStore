package com.example.bookstore.ui.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.example.bookstore.data.model.Category
import com.example.bookstore.ui.components.AppBottomNavigation
import com.example.bookstore.ui.components.BottomNavItem
import com.example.bookstore.viewmodel.AccountViewModel
import com.example.bookstore.viewmodel.CartViewModel

@Composable
fun MainScreen() {
    val navController    = rememberNavController()
    val cartViewModel    : CartViewModel    = hiltViewModel()
    val accountViewModel : AccountViewModel = hiltViewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Chỉ hiện bottom bar trên các tab chính + category_detail
    val showBottomBar = currentRoute in setOf(
        BottomNavItem.Home.route,
        BottomNavItem.Category.route,
        BottomNavItem.Cart.route,
        BottomNavItem.Account.route
    ) || currentRoute?.startsWith("category_detail/") == true

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showBottomBar) AppBottomNavigation(navController = navController)
        }
    ) { innerPadding ->

        NavHost(
            navController       = navController,
            // startDestination là graph route của Home tab
            startDestination    = BottomNavItem.Home.graphRoute,
            modifier            = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            enterTransition     = { fadeIn(animationSpec = tween(0)) },
            exitTransition      = { fadeOut(animationSpec = tween(0)) },
            popEnterTransition  = { fadeIn(animationSpec = tween(0)) },
            popExitTransition   = { fadeOut(animationSpec = tween(0)) }
        ) {

            // ================================================================
            // HOME GRAPH — Trang chủ, Tìm kiếm, Chi tiết sách
            // back stack riêng: [home] → [home, search_screen] → [home, ..., book_detail]
            // ================================================================
            navigation(
                startDestination = BottomNavItem.Home.route,
                route            = BottomNavItem.Home.graphRoute
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        cartViewModel        = cartViewModel,
                        onNavigateToCategory = {
                            navController.navigate(BottomNavItem.Category.graphRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        onBookClick   = { bookId -> navController.navigate("book_detail/$bookId") },
                        onSearchClick = { navController.navigate("search_screen") }
                    )
                }

                composable(
                    route           = "search_screen",
                    enterTransition = { EnterTransition.None },
                    exitTransition  = { ExitTransition.None }
                ) {
                    SearchScreen(
                        onBackClick         = { navController.popBackStack() },
                        onNavigateToResults = { query -> navController.navigate("search_results/$query") }
                    )
                }

                composable(
                    route           = "search_results/{query}",
                    enterTransition = { EnterTransition.None },
                    exitTransition  = { ExitTransition.None }
                ) { backStackEntry ->
                    val query = backStackEntry.arguments?.getString("query") ?: ""
                    BookSearchResultsScreen(
                        initialQuery  = query,
                        cartViewModel = cartViewModel,
                        onBackClick   = { navController.popBackStack() },
                        onBookClick   = { bookId -> navController.navigate("book_detail/$bookId") }
                    )
                }

                // book_detail nằm trong home_graph:
                // - Accessed từ HomeScreen, SearchResults → Home tab highlighted ✓
                // - Accessed từ CategoryDetail → Home tab highlighted (chấp nhận được)
                // Tất cả tab switching dùng navigateAsTab(graphRoute) với saveState/restoreState
                composable(
                    route           = "book_detail/{bookId}",
                    enterTransition = { EnterTransition.None },
                    exitTransition  = { ExitTransition.None }
                ) {
                    // navigateAsTab: switch tab đúng chuẩn — lưu state tab hiện tại,
                    // restore state tab đích. Dùng cho TẤT CẢ tab buttons (kể cả Cart).
                    fun navigateAsTab(graphRoute: String) {
                        navController.navigate(graphRoute) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                    BookDetailScreen(
                        onBackClick          = { navController.popBackStack() },
                        onHomeClick          = { navigateAsTab(BottomNavItem.Home.graphRoute) },
                        onCartClick          = { navigateAsTab(BottomNavItem.Cart.graphRoute) },
                        onCategoryClick      = { navigateAsTab(BottomNavItem.Category.graphRoute) },
                        onAccountClick       = { navigateAsTab(BottomNavItem.Account.graphRoute) },
                        onSearchSubmit       = { query -> navController.navigate("search_results/$query") },
                        cartViewModel        = cartViewModel,
                        onNavigateToCheckout = { navController.navigate("checkout") }
                    )
                }
            }

            // ================================================================
            // CATEGORY GRAPH — Danh mục, Chi tiết danh mục
            // hierarchy tự động highlight Category tab khi ở category_detail
            // ================================================================
            navigation(
                startDestination = BottomNavItem.Category.route,
                route            = BottomNavItem.Category.graphRoute
            ) {
                composable(BottomNavItem.Category.route) {
                    CategoryScreen(navController = navController)
                }
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
            }

            // ================================================================
            // CART GRAPH — Giỏ hàng, Thanh toán
            // CartScreen tự xử lý auth guard bên trong
            // ================================================================
            navigation(
                startDestination = BottomNavItem.Cart.route,
                route            = BottomNavItem.Cart.graphRoute
            ) {
                composable(BottomNavItem.Cart.route) {
                    CartScreen(cartViewModel = cartViewModel, navController = navController)
                }
                composable("checkout") {
                    CheckoutScreen(
                        cartViewModel    = cartViewModel,
                        accountViewModel = accountViewModel,
                        navController    = navController
                    )
                }
            }

            // ================================================================
            // ACCOUNT GRAPH — Tài khoản và tất cả màn hình con
            // hierarchy tự động highlight Account tab khi ở profile, settings, v.v.
            // ================================================================
            navigation(
                startDestination = BottomNavItem.Account.route,
                route            = BottomNavItem.Account.graphRoute
            ) {
                composable(BottomNavItem.Account.route) {
                    AccountScreen(navController = navController, viewModel = accountViewModel)
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
                    OrderHistoryScreen(navController = navController, initialTab = 0)
                }
                composable(
                    route     = "order_history?success={success}",
                    arguments = listOf(navArgument("success") { type = NavType.BoolType; defaultValue = false }),
                    deepLinks = listOf(navDeepLink { uriPattern = "bookstore://payment/result?success={success}&orderId={orderId}" })
                ) { backStackEntry ->
                    val success = backStackEntry.arguments?.getBoolean("success") ?: false
                    OrderHistoryScreen(navController = navController, initialTab = if (success) 2 else 1)
                }
                composable("contact") {
                    ContactScreen(navController = navController)
                }
            }

            // ================================================================
            // SHARED SCREENS — Nằm ngoài mọi graph (không thuộc tab nào)
            // Dùng cho auth flow: đăng nhập, đăng ký
            // ================================================================
            composable(
                route     = "login/{returnRoute}",
                arguments = listOf(navArgument("returnRoute") {
                    type         = NavType.StringType
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
            composable("register") {
                RegisterScreen(navController = navController)
            }
        }
    }
}
