package com.example.bookstore.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.dto.request.RegisterRequest
import com.example.bookstore.data.local.TokenManager
import com.example.bookstore.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // Trạng thái đăng nhập (dùng để guard CartScreen/AccountScreen)
    val isLoggedIn = tokenManager.token.map { it != null }

    // ---------- Đăng nhập ----------
    var loginUsername by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var loginPasswordVisible by mutableStateOf(false)
    var rememberMe by mutableStateOf(false)

    // ---------- Đăng ký ----------
    var regFullName            by mutableStateOf("")
    var regEmail               by mutableStateOf("")
    var regPhone               by mutableStateOf("")
    var regPassword            by mutableStateOf("")
    var regConfirmPassword     by mutableStateOf("")
    var regPasswordVisible     by mutableStateOf(false)
    var regConfirmPassVisible  by mutableStateOf(false)
    var agreedToTerms          by mutableStateOf(false)

    // ---------- Trạng thái chung ----------
    var isLoading        by mutableStateOf(false)
    var errorMessage     by mutableStateOf<String?>(null)
    var registerSuccess  by mutableStateOf(false)

    // Dùng Channel (one-shot event) thay vì Boolean flag để tránh race condition
    private val _loginSuccess = Channel<Unit>(Channel.BUFFERED)
    val loginSuccess = _loginSuccess.receiveAsFlow()

    // ---- Đăng nhập ----
    fun login(onSuccess: () -> Unit = {}) {
        if (loginUsername.isBlank() || loginPassword.isBlank()) {
            errorMessage = "Vui lòng nhập đầy đủ thông tin"
            return
        }
        viewModelScope.launch {
            isLoading    = true
            errorMessage = null
            val result = userRepository.login(loginUsername.trim(), loginPassword)
            isLoading = false
            result
                .onSuccess { jwt ->
                    tokenManager.saveToken(jwt.token)
                    jwt.userId?.let { tokenManager.saveUserId(it) }
                    val uname = jwt.username
                        ?: TokenManager.decodeUsernameFromJwt(jwt.token)
                    if (uname != null) tokenManager.saveUsername(uname)
                    _loginSuccess.send(Unit)   // ← gửi event, không reset được
                    onSuccess()
                }
                .onFailure { errorMessage = it.message }
        }
    }

    // ---- Đăng ký ----
    fun register(onSuccess: () -> Unit) {
        if (regFullName.isBlank() || regEmail.isBlank() || regPassword.isBlank()) {
            errorMessage = "Vui lòng nhập đầy đủ thông tin bắt buộc"
            return
        }
        if (regPassword != regConfirmPassword) {
            errorMessage = "Mật khẩu xác nhận không khớp"
            return
        }
        if (!agreedToTerms) {
            errorMessage = "Vui lòng đồng ý với điều khoản dịch vụ"
            return
        }
        viewModelScope.launch {
            isLoading    = true
            errorMessage = null
            val result = userRepository.register(
                RegisterRequest(
                    username    = regEmail.trim(),
                    password    = regPassword,
                    email       = regEmail.trim(),
                    fullName    = regFullName.trim(),
                    phoneNumber = regPhone.trim(),
                    address     = ""
                )
            )
            isLoading = false
            result
                .onSuccess { registerSuccess = true; onSuccess() }
                .onFailure { errorMessage = it.message }
        }
    }

    fun clearError() {
        errorMessage = null
        // KHÔNG reset loginSuccess ở đây nữa – nó là Channel, không có gì để reset
    }
}

