package com.example.bookstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.api.ApiService
import com.example.bookstore.data.dto.request.LoginRequest
import com.example.bookstore.data.dto.request.RegisterRequest
import com.example.bookstore.data.dto.response.JwtResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val apiService: ApiService
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

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

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = apiService.login(request)
                if (response.isSuccessful && response.body() != null) {
                    _authState.value = AuthState.LoginSuccess(response.body()!!)
                } else {
                    _authState.value = AuthState.Error("Đăng nhập thất bại: Sai tài khoản hoặc mật khẩu")
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
