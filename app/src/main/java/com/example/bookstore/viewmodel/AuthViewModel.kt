package com.example.bookstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.dto.request.LoginRequest
import com.example.bookstore.data.dto.response.JwtResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class LoginSuccess(val response: JwtResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)


    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
                }
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
