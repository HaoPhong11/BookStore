package com.example.bookstore.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GoogleBooksClient {

    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}