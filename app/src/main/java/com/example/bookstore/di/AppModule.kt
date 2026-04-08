package com.example.bookstore.di

import android.content.Context
import com.example.bookstore.data.api.ApiService
import com.example.bookstore.data.api.GoogleBooksApiService
import com.example.bookstore.data.api.RetrofitClient
import com.example.bookstore.data.local.TokenManager
import com.example.bookstore.data.repo.BookRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Tồn tại xuyên suốt vòng đời app
object AppModule {

    // 1. Phân phát Két sắt Token
    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    // 2. Phân phát ống nước Google
    @Provides
    @Singleton
    fun provideGoogleBooksApi(): GoogleBooksApiService {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleBooksApiService::class.java)
    }

    // 3. Phân phát Repository (Tự động lấy cái API ở trên bơm vào)
    @Provides
    @Singleton
    fun provideBookRepository(api: GoogleBooksApiService): BookRepository {
        return BookRepository(api)
    }

    // 4. Phân phát ApiService cho backend Railway
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitClient.instance.create(ApiService::class.java)
    }
}
