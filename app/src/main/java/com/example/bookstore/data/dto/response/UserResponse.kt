package com.example.bookstore.data.dto.response

data class UserResponse(
    val id: Long,
    val username: String,
    val fullName: String,
    val phoneNumber: String?,
    val address: String?,
    val email: String?,
    val province: String?,
    val district: String?
)