package com.example.bookstore.data.model

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val fullName: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class JwtResponse(
    val token: String
)