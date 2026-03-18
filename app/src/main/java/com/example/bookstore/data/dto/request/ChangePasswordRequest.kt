package com.example.bookstore.data.dto.request

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)