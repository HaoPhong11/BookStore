package com.example.bookstore.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://bookstore-backend-production-4b7e.up.railway.app/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Thằng Gson này sẽ tự động dịch cục JSON từ Railway thành Object Kotlin
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}