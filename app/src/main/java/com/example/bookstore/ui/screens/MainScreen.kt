package com.example.bookstore.ui.screens


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookstore.ui.components.AppBottomNavigation
import com.example.bookstore.ui.components.BottomNavItem
import com.example.bookstore.ui.screens.AccountScreen
import com.example.bookstore.ui.screens.CartScreen
import com.example.bookstore.ui.screens.CategoryScreen
import com.example.bookstore.ui.screens.HomeScreen


@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavigation(navController = navController) }
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
                CategoryScreen()
            }
            composable(BottomNavItem.Cart.route) {
                CartScreen()
            }
            composable(BottomNavItem.Account.route) {
                AccountScreen(
                    onLoginClick = { navController.navigate("login") },
                    onRegisterClick = { navController.navigate("register") }
                )
            }
            composable("login") {
                LoginScreen(
                    onBackClick = { navController.popBackStack() },
                    onRegisterClick = { navController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    onBackClick = { navController.popBackStack() },
                    onLoginClick = { navController.navigate("login") }
                )
            }
        }
    }
}
