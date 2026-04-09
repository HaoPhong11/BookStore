package com.example.bookstore.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GoogleBooksClient {

    private const val BASE_URL = "https://www.googleapis.com/books/v1/"
    private const val API_KEY = "AIzaSyCB6PVlT939N3rD-La0y52Y0bEYydSeF0o"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("key", API_KEY)
                .build()
            chain.proceed(original.newBuilder().url(url).build())
        }
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: GoogleBooksApiService by lazy {
        instance.create(GoogleBooksApiService::class.java)
    }
}