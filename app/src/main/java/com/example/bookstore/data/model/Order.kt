package com.example.bookstore.data.model

data class Order(
    val orderId: Int,
    val userId: Int,
    val books: List<Book>,
    val totalAmount: Double,
    val status: String
)
