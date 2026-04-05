package com.example.bookstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.api.ApiService
import com.example.bookstore.data.dto.request.LoginRequest
import com.example.bookstore.data.dto.request.RegisterRequest
import com.example.bookstore.data.dto.response.JwtResponse
import com.example.bookstore.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class LoginSuccess(val response: JwtResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    val isLoggedIn = tokenManager.token.map { it != null }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.login(request)
                if (response.isSuccessful && response.body() != null) {
                    val jwtResponse = response.body()!!
                    tokenManager.saveToken(jwtResponse.token)
                    _authState.value = AuthState.LoginSuccess(jwtResponse)
                } else {
                    _authState.value = AuthState.Error("Đăng nhập thất bại: Sai tài khoản hoặc mật khẩu")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun loginWithSocial(platform: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Giả lập quá trình xác thực với Social API (Google/Facebook)
            delay(1500)

            // Giả lập kết quả trả về từ backend sau khi verify social token
//            val mockJwt = JwtResponse(
//                token = "mock_social_token_for_$platform",
//                type = "Bearer",
//                id = 999L,
//                username = "Social User",
//                email = "social@example.com",
//                roles = listOf("ROLE_USER"),
//                userId = 999L
//            )
//
//            tokenManager.saveToken(mockJwt.token)
//            tokenManager.saveUsername(mockJwt.username ?: "Social User")
//            mockJwt.userId?.let { tokenManager.saveUserId(it) }
//
//            _authState.value = AuthState.LoginSuccess(mockJwt)
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.registerUser(request)
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success(response.body() ?: "Đăng ký thành công")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Đăng ký thất bại"
                    _authState.value = AuthState.Error(errorBody)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
