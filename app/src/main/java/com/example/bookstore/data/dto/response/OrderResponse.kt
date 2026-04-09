package com.example.bookstore.data.dto.response

data class OrderResponse(
    val id: Long,
    val totalPrice: Double,
    val createdAt: String = "",  // Backend trả về chuỗi ISO date
    val status: String = "",
    val receiverName: String = "",
    val items: List<OrderItemResponse> = emptyList()  // Backend trả về "items" (đã fix @JsonProperty)
)