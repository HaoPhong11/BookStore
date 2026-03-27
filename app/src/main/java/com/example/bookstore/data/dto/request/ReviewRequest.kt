package com.example.bookstore.data.dto.request

data class ReviewRequest(
    val bookId: String,
    val rating: Int,
    val comment: String
)