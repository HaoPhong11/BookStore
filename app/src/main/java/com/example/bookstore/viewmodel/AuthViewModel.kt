package com.example.bookstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.dto.request.LoginRequest
import com.example.bookstore.data.dto.response.JwtResponse
import com.example.bookstore.data.local.TokenManager
import com.example.bookstore.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── AuthState — sealed class quản lý trạng thái đăng nhập ──────────────────────────────────
// Dùng StateFlow thay vì Channel để UI có thể observe state hiện tại mọi lúc
sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    data class LoginSuccess(val response: JwtResponse) : AuthState()
    data class Error(val message: String)              : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Dùng để kiểm tra đã đăng nhập chưa (auth guard — tương tự LoginViewModel.isLoggedIn)
    val isLoggedIn = tokenManager.token.map { it != null }

    /**
     * Đăng nhập — nhận LoginRequest từ UI, trả về AuthState qua StateFlow
     * email trong LoginRequest được dùng làm username (backend dùng email làm username)
     */
    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.login(request.username, request.password)
            result
                .onSuccess { jwt ->
                    tokenManager.saveToken(jwt.token)
                    jwt.userId?.let { tokenManager.saveUserId(it) }
                    val uname = jwt.username
                        ?: TokenManager.decodeUsernameFromJwt(jwt.token)
                    if (uname != null) tokenManager.saveUsername(uname)
                    _authState.value = AuthState.LoginSuccess(jwt)
                }
                .onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Đăng nhập thất bại")
                }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

