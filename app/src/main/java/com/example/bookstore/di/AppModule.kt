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
import okhttp3.OkHttpClient
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
        val apiKey = "AIzaSyCB6PVlT939N3rD-La0y52Y0bEYydSeF0o"
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val url = original.url.newBuilder()
                    .addQueryParameter("key", apiKey)
                    .build()
                chain.proceed(original.newBuilder().url(url).build())
            }
            .build()
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .client(okHttpClient)
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
