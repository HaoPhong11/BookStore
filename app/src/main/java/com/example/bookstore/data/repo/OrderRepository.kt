package com.example.bookstore.data.repo

import com.example.bookstore.data.api.ApiService
import com.example.bookstore.data.dto.request.OrderRequest
import com.example.bookstore.data.dto.response.OrderResponse
import com.example.bookstore.data.local.TokenManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun createOrder(request: OrderRequest): Result<Unit> {
        return try {
            val token = tokenManager.token.first()
                ?: return Result.failure(Exception("Chưa đăng nhập"))
            val response = api.createOrder("Bearer $token", request)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Đặt hàng thất bại (${response.code()})"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderHistory(userId: Long): Result<List<OrderResponse>> {
        return try {
            val token = tokenManager.token.first()
                ?: return Result.failure(Exception("Chưa đăng nhập"))
            val response = api.getOrderHistory("Bearer $token", userId)
            if (response.isSuccessful) Result.success(response.body() ?: emptyList())
            else Result.failure(Exception("Lấy đơn hàng thất bại (${response.code()})"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

