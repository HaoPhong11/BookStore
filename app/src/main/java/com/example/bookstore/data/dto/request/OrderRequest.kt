package com.example.bookstore.data.dto.request


data class OrderRequest(
    val userId: Long,
    val subTotal: Double,
    val shippingFee: Double,
    val discount: Double,
    val totalPrice: Double,
    val receiverName: String,
    val receiverPhone: String,
    val receiverEmail: String,
    val province: String,
    val district: String,
    val detailedAddress: String,
    val note: String,
    val paymentMethod: String,
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val bookId: String,
    val quantity: Int,
    val price: Double,
    val bookTitle: String?,
    val imgUrl: String?          // khớp với backend OrderDetailRequest.imgUrl
)