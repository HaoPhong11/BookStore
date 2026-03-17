package com.example.bookstore.data.api

import com.example.bookstore.data.model.Book
import com.example.bookstore.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // Gọi đúng đường dẫn lúc ông test trên Postman
    @POST("api/auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<String>


     @GET("api/books/search")
     suspend fun searchBooks(@Query("query") query: String): Response<List<Book>>
}