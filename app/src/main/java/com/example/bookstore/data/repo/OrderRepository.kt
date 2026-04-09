package com.example.bookstore.data.repo

import com.example.bookstore.data.api.ApiService
import com.example.bookstore.data.dto.request.OrderRequest
import com.example.bookstore.data.dto.response.OrderResponse
import com.example.bookstore.data.dto.response.PaymentUrlResponse
import com.example.bookstore.data.local.TokenManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {
    /** Tạo đơn hàng. Trả về PaymentUrlResponse (có payUrl nếu MOMO, null nếu COD). */
    suspend fun createOrder(request: OrderRequest): Result<PaymentUrlResponse> {
        return try {
            val token = tokenManager.token.first()
                ?: return Result.failure(Exception("Chưa đăng nhập"))
            val response = api.createOrder("Bearer $token", request)
            if (response.isSuccessful)
                Result.success(response.body() ?: PaymentUrlResponse(0L, null, null, request.paymentMethod))
            else
                Result.failure(Exception("Đặt hàng thất bại (${response.code()})"))
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

    /** Hủy đơn hàng PENDING. */
    suspend fun cancelOrder(orderId: Long): Result<Unit> {
        return try {
            val token = tokenManager.token.first()
                ?: return Result.failure(Exception("Chưa đăng nhập"))
            val response = api.cancelOrder("Bearer $token", orderId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Hủy đơn thất bại (${response.code()})"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Lấy lại MoMo payUrl cho đơn PENDING. */
    suspend fun getRepayUrl(orderId: Long): Result<PaymentUrlResponse> {
        return try {
            val token = tokenManager.token.first()
                ?: return Result.failure(Exception("Chưa đăng nhập"))
            val response = api.repayOrder("Bearer $token", orderId)
            if (response.isSuccessful)
                Result.success(response.body()!!)
            else
                Result.failure(Exception("Lấy URL thanh toán thất bại (${response.code()})"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
