package com.example.bookstore.data.dto.response

import com.google.gson.annotations.SerializedName

data class JwtResponse(
    @SerializedName("accessToken") val token: String,  // backend trả "accessToken"
    val tokenType: String? = null,
    @SerializedName("id") val userId: Long? = null,    // backend trả "id" (không phải "userId")
    val username: String? = null
)
