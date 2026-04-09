package com.example.bookstore.data.dto.response

data class OrderItemResponse(
    val bookId: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val bookTitle: String = "",
    val imgUrl: String = ""
)