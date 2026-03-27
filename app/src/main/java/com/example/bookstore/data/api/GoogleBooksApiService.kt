package com.example.bookstore.data.api

import com.example.bookstore.data.dto.GoogleBookResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {

    // API tìm kiếm sách của Google. Ví dụ tìm "java"
    // Nó sẽ nối thành: https://www.googleapis.com/books/v1/volumes?q=java
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20 // Lấy tối đa 20 cuốn
    ): Response<GoogleBookResponse>
}