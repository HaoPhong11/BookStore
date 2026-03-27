package com.example.bookstore.data.dto.response

data class OrderItemResponse(
    val bookId: String,
    val quantity: Int,
    val price: Double
)