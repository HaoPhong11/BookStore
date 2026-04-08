package com.example.bookstore.data.dto.request

data class UserRequest(
    val fullName: String,
    val phoneNumber: String,
    val address: String,
    val email: String,
    val province: String,
    val district: String
)
