package com.example.bookstore.data.dto.response

data class JwtResponse(
    val token: String,
    val userId: Long,
    val username: String,
    val email: String?
)
