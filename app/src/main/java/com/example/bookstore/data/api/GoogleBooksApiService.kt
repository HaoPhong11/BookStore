package com.example.bookstore.data.api

import com.example.bookstore.data.dto.BookItem
import com.example.bookstore.data.dto.GoogleBookResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApiService {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20
    ): Response<GoogleBookResponse>

    @GET("volumes/{volumeId}")
    suspend fun getBookById(@Path("volumeId") volumeId: String): Response<BookItem>
}