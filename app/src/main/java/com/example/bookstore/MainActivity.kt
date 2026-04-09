package com.example.bookstore


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bookstore.ui.screens.MainScreen
import com.example.bookstore.ui.theme.BookStoreTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // BookStoreTheme cung cấp MaterialTheme (colorScheme, typography)
            // cho toàn bộ app — PDF §4.1.3 Theming trong Jetpack Compose
            BookStoreTheme {
                MainScreen()
            }
        }
    }
}