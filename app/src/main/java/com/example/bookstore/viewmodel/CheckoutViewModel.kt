package com.example.bookstore.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.dto.request.OrderItemRequest
import com.example.bookstore.data.dto.request.OrderRequest
import com.example.bookstore.data.local.TokenManager
import com.example.bookstore.data.model.CartItem
import com.example.bookstore.data.repo.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var paymentMethod by mutableStateOf("COD")

    var isLoading    by mutableStateOf(false)
    var orderSuccess by mutableStateOf(false)   // COD: đặt hàng thành công
    var errorMessage by mutableStateOf<String?>(null)

    // MOMO: sau khi tạo order, chứa URL để mở thanh toán
    var pendingPayUrl  by mutableStateOf<String?>(null)
    var pendingDeeplink by mutableStateOf<String?>(null)
    var createdOrderId  by mutableStateOf<Long?>(null)

    /**
     * Đặt hàng — nhận toàn bộ thông tin giao hàng qua tham số.
     * PDF §3.3.4 — Coroutines: viewModelScope.launch + suspend
     * PDF §5.1.2 — Retrofit: gọi API qua Repository
     */
    fun placeOrder(
        cartItems:       List<CartItem>,
        subtotal:        Double,
        shippingFee:     Double,
        discount:        Double,
        receiverName:    String,
        receiverPhone:   String,
        receiverEmail:   String,
        province:        String,
        district:        String,
        detailedAddress: String,
        note:            String
    ) {
        if (receiverName.isBlank() || receiverPhone.isBlank() ||
            province.isBlank() || district.isBlank() || detailedAddress.isBlank()
        ) {
            errorMessage = "Vui lòng điền đầy đủ thông tin giao hàng"
            return
        }

        viewModelScope.launch {
            isLoading    = true
            errorMessage = null

            val userId = tokenManager.userId.first() ?: 0L

            val request = OrderRequest(
                userId          = userId,
                subTotal        = subtotal,
                shippingFee     = shippingFee,
                discount        = discount,
                totalPrice      = subtotal + shippingFee - discount,
                receiverName    = receiverName,
                receiverPhone   = receiverPhone,
                receiverEmail   = receiverEmail,
                province        = province,
                district        = district,
                detailedAddress = detailedAddress,
                note            = note,
                paymentMethod   = paymentMethod,
                items           = cartItems.map { item ->
                    OrderItemRequest(
                        bookId    = item.book.id,
                        quantity  = item.quantity,
                        price     = item.book.price,
                        bookTitle = item.book.title,
                        imgUrl    = null   // không gửi URL dài (có thể > VARCHAR 255), bookId đã đủ để hiển thị ảnh
                    )
                }
            )

            val result = orderRepository.createOrder(request)
            isLoading = false
            result.onSuccess { paymentResponse ->
                createdOrderId = paymentResponse.orderId
                if (paymentMethod == "MOMO") {
                    // Expose URLs cho CheckoutScreen mở browser/app
                    pendingPayUrl   = paymentResponse.payUrl
                    pendingDeeplink = paymentResponse.deeplink
                } else {
                    // COD — hiện dialog thành công
                    orderSuccess = true
                }
            }
            result.onFailure { errorMessage = it.message }
        }
    }

    fun resetState() {
        orderSuccess    = false
        errorMessage    = null
        pendingPayUrl   = null
        pendingDeeplink = null
        createdOrderId  = null
    }
}
