package com.example.bookstore.data.dto.response

data class PaymentUrlResponse(
    val orderId: Long,
    val payUrl: String?,      // URL mở browser (MoMo web)
    val deeplink: String?,    // URL mở thẳng app MoMo (có thể null)
    val paymentMethod: String = ""
)

