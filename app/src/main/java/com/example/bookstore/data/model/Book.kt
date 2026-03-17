package com.example.bookstore.data.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val imageUrl: String,
    val price: Double = 0.0
)