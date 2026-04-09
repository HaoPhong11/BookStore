package com.example.bookstore.data.repo

import android.util.Log
import com.example.bookstore.data.api.ApiService
import com.example.bookstore.data.dto.request.ChangePasswordRequest
import com.example.bookstore.data.dto.request.LoginRequest
import com.example.bookstore.data.dto.request.RegisterRequest
import com.example.bookstore.data.dto.request.UserRequest
import com.example.bookstore.data.dto.response.JwtResponse
import com.example.bookstore.data.dto.response.UserResponse
import com.example.bookstore.data.local.TokenManager
import kotlinx.coroutines.flow.first
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {

    /** Chuyển exception thành thông báo thân thiện với người dùng */
    private fun networkError(e: Exception): Exception {
        return if (e is SocketTimeoutException) {
            Exception("Máy chủ đang khởi động, vui lòng thử lại sau vài giây...")
        } else {
            Exception("Không thể kết nối máy chủ: ${e.message}")
        }
    }

    suspend fun login(username: String, password: String): Result<JwtResponse> {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Sai tên đăng nhập hoặc mật khẩu (${response.code()})"))
        } catch (e: Exception) {
            Log.e("UserRepository", "login error", e)
            Result.failure(networkError(e))
        }
    }

    suspend fun register(request: RegisterRequest): Result<String> {
        return try {
            val response = api.registerUser(request)
            if (response.isSuccessful) Result.success(response.body()?.string() ?: "Đăng ký thành công")
            else Result.failure(Exception("Đăng ký thất bại (${response.code()})"))
        } catch (e: Exception) {
            Log.e("UserRepository", "register error", e)
            Result.failure(networkError(e))
        }
    }

    suspend fun getProfile(userId: Long): Result<UserResponse> {
        return try {
            val token = tokenManager.token.first()
                ?: return Result.failure(Exception("Chưa đăng nhập"))
            val response = api.getUserProfile("Bearer $token", userId)
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Lấy thông tin thất bại (${response.code()})"))
        } catch (e: Exception) {
            Log.e("UserRepository", "getProfile error", e)
            Result.failure(networkError(e))
        }
    }

    suspend fun updateProfile(userId: Long, request: UserRequest): Result<Unit> {
        return try {
            val token = tokenManager.token.first()
                ?: return Result.failure(Exception("Chưa đăng nhập"))
            val response = api.updateUser("Bearer $token", userId, request)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Cập nhật thất bại (${response.code()})"))
        } catch (e: Exception) {
            Log.e("UserRepository", "updateProfile error", e)
            Result.failure(networkError(e))
        }
    }

    suspend fun changePassword(request: ChangePasswordRequest): Result<String> {
        return try {
            val token = tokenManager.token.first()
                ?: return Result.failure(Exception("Chưa đăng nhập"))
            val response = api.changePassword("Bearer $token", request)
            if (response.isSuccessful) Result.success(response.body()?.string() ?: "Đổi mật khẩu thành công")
            else Result.failure(Exception("Đổi mật khẩu thất bại (${response.code()})"))
        } catch (e: Exception) {
            Log.e("UserRepository", "changePassword error", e)
            Result.failure(networkError(e))
        }
    }
}




