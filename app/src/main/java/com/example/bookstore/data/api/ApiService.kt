package com.example.bookstore.data.api

import com.example.bookstore.data.dto.request.ChangePasswordRequest
import com.example.bookstore.data.dto.request.LoginRequest
import com.example.bookstore.data.model.Book
import com.example.bookstore.data.dto.request.OrderRequest
import com.example.bookstore.data.dto.request.RegisterRequest
import com.example.bookstore.data.dto.request.ReviewRequest
import com.example.bookstore.data.dto.request.UserRequest
import com.example.bookstore.data.dto.response.JwtResponse
import com.example.bookstore.data.dto.response.OrderResponse
import com.example.bookstore.data.dto.response.PaymentUrlResponse
import com.example.bookstore.data.dto.response.UserResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    //Dang nhap
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<JwtResponse>

    //dang ky — backend trả text/plain nên dùng ResponseBody thay vì String
    @POST("api/auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<ResponseBody>

     @GET("api/books/search")
     suspend fun searchBooks(@Query("query") query: String): Response<List<Book>>

    // --- PHẦN USER ---
    @PUT("api/users/{userId}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long,
        @Body request: UserRequest
    ): Response<ResponseBody> // dùng ResponseBody để tránh Gson parse text/plain

    @GET("api/users/{userId}")
    suspend fun getUserProfile(
        @Header("Authorization") token: String, // "Bearer <token>"
        @Path("userId") userId: Long
    ): Response<UserResponse>

    @POST("api/auth/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ResponseBody>


    // --- PHẦN ĐÁNH GIÁ (REVIEWS) ---
    @POST("api/reviews")
    suspend fun addReview(
        @Header("Authorization") token: String,
        @Body request: ReviewRequest
    ): Response<ResponseBody> // backend trả text/plain

    @GET("api/reviews/book/{bookId}/average-rating")
    suspend fun getAverageRating(
        @Path("bookId") bookId: String
    ): Response<Double>


    // --- PHẦN ĐƠN HÀNG (ORDERS) ---
    /** Tạo đơn hàng. Trả về PaymentUrlResponse (payUrl=null nếu COD, có URL nếu MOMO). */
    @POST("api/orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: OrderRequest
    ): Response<PaymentUrlResponse>

    @GET("api/orders/user/{userId}")
    suspend fun getOrderHistory(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long
    ): Response<List<OrderResponse>>

    /** Hủy đơn hàng PENDING. */
    @PATCH("api/orders/{id}/cancel")
    suspend fun cancelOrder(
        @Header("Authorization") token: String,
        @Path("id") orderId: Long
    ): Response<ResponseBody>

    /** Lấy lại MoMo payUrl cho đơn PENDING. */
    @GET("api/orders/{id}/repay")
    suspend fun repayOrder(
        @Header("Authorization") token: String,
        @Path("id") orderId: Long
    ): Response<PaymentUrlResponse>
}