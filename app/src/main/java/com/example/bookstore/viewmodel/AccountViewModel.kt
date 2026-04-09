package com.example.bookstore.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.dto.request.ChangePasswordRequest
import com.example.bookstore.data.dto.request.UserRequest
import com.example.bookstore.data.dto.response.OrderResponse
import com.example.bookstore.data.dto.response.PaymentUrlResponse
import com.example.bookstore.data.dto.response.UserResponse
import com.example.bookstore.data.local.TokenManager
import com.example.bookstore.data.repo.OrderRepository
import com.example.bookstore.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // --- Profile ---
    var userProfile by mutableStateOf<UserResponse?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    // Username lấy từ JWT (sub claim) — dùng khi chưa có userId từ backend
    val displayUsername = tokenManager.username  // Flow<String?>

    // --- Edit profile fields ---
    var editFullName    by mutableStateOf("")
    var editEmail       by mutableStateOf("")
    var editPhone       by mutableStateOf("")
    var editProvince    by mutableStateOf("")
    var editDistrict    by mutableStateOf("")
    var editAddress     by mutableStateOf("")   // = detailedAddress
    var updateSuccess   by mutableStateOf(false)
    var profileError    by mutableStateOf<String?>(null)

    // --- Order history ---
    var orderHistory by mutableStateOf<List<OrderResponse>>(emptyList())
        private set
    var orderLoading by mutableStateOf(false)
        private set

    // --- Order actions (cancel / repay) ---
    var orderActionLoading by mutableStateOf(false)
        private set
    var orderActionError   by mutableStateOf<String?>(null)
    var repayResult        by mutableStateOf<PaymentUrlResponse?>(null)
    var cancelSuccess      by mutableStateOf(false)

    // --- Change password ---
    var oldPassword      by mutableStateOf("")
    var newPassword      by mutableStateOf("")
    var confirmPassword  by mutableStateOf("")
    var changePassSuccess  by mutableStateOf(false)
    var changePassError    by mutableStateOf<String?>(null)
    var changePassLoading  by mutableStateOf(false)

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            isLoading = true
            val userId = tokenManager.userId.first() ?: run { isLoading = false; return@launch }
            userRepository.getProfile(userId).onSuccess { user ->
                userProfile  = user
                editFullName = user.fullName
                editEmail    = user.email       ?: ""
                editPhone    = user.phoneNumber ?: ""
                editProvince = user.province    ?: ""
                editDistrict = user.district    ?: ""
                editAddress  = user.address     ?: ""
            }
            isLoading = false
        }
    }

    fun saveProfile(showSuccess: Boolean = true) {
        viewModelScope.launch {
            isLoading   = true
            profileError = null
            val userId = tokenManager.userId.first() ?: run { isLoading = false; return@launch }
            val result = userRepository.updateProfile(
                userId,
                UserRequest(editFullName, editPhone, editAddress, editEmail, editProvince, editDistrict)
            )
            isLoading = false
            result.onSuccess { if (showSuccess) updateSuccess = true; loadProfile() }
            result.onFailure { profileError = it.message }
        }
    }

    fun loadOrderHistory() {
        viewModelScope.launch {
            orderLoading = true
            val userId = tokenManager.userId.first() ?: run { orderLoading = false; return@launch }
            orderRepository.getOrderHistory(userId).onSuccess { orderHistory = it }
            orderLoading = false
        }
    }

    /** Hủy đơn PENDING. Sau khi hủy, reload lại danh sách. */
    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            orderActionLoading = true
            orderActionError   = null
            orderRepository.cancelOrder(orderId)
                .onSuccess {
                    cancelSuccess = true
                    loadOrderHistory()
                }
                .onFailure { orderActionError = it.message }
            orderActionLoading = false
        }
    }

    /** Lấy lại MoMo payUrl cho đơn PENDING để thanh toán lại. */
    fun getRepayUrl(orderId: Long) {
        viewModelScope.launch {
            orderActionLoading = true
            orderActionError   = null
            orderRepository.getRepayUrl(orderId)
                .onSuccess  { repayResult = it }
                .onFailure  { orderActionError = it.message }
            orderActionLoading = false
        }
    }

    fun resetOrderAction() {
        orderActionError   = null
        repayResult        = null
        cancelSuccess      = false
    }

    fun changePassword() {
        if (newPassword != confirmPassword) {
            changePassError = "Mật khẩu xác nhận không khớp"
            return
        }
        if (newPassword.length < 6) {
            changePassError = "Mật khẩu mới phải có ít nhất 6 ký tự"
            return
        }
        viewModelScope.launch {
            changePassLoading = true
            changePassError   = null
            val result = userRepository.changePassword(
                ChangePasswordRequest(oldPassword, newPassword, confirmPassword)
            )
            changePassLoading = false
            result.onSuccess { changePassSuccess = true }
            result.onFailure { changePassError = it.message }
        }
    }

    fun resetChangePassState() {
        changePassSuccess = false
        changePassError   = null
        oldPassword       = ""
        newPassword       = ""
        confirmPassword   = ""
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearAll()  // xóa token + userId + username
            onDone()
        }
    }

    /**
     * Ghi ngược thông tin giao hàng từ CheckoutScreen về profile.
     * Được gọi sau khi đặt hàng thành công để lần sau pre-fill sẵn.
     */
    fun applyShippingInfo(
        name:     String,
        phone:    String,
        email:    String,
        province: String,
        district: String,
        address:  String
    ) {
        editFullName = name
        editPhone    = phone
        editEmail    = email
        editProvince = province
        editDistrict = district
        editAddress  = address
        saveProfile(showSuccess = false)   // lưu im lặng, không hiện popup
    }
}

