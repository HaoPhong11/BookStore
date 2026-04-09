package com.example.bookstore.data.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ==========================================
    // ĐỔI URL TẠI ĐÂY KHI CẦN THIẾT
    // ==========================================

    // URL production trên Railway (dùng khi deploy)
    private const val PRODUCTION_URL = "https://bookstore-backend-production-4b7e.up.railway.app/"

    // URL local khi chạy backend trên máy tính:
    //   - Máy ảo Android Emulator → dùng 10.0.2.2 (alias của localhost máy tính)
    //   - Điện thoại thật          → dùng IP LAN của máy tính (192.168.1.5)
    //     ⚠️ Điện thoại và máy tính phải cùng WiFi
    private const val LOCAL_URL = "http://192.168.1.4:8081/"

    // *** CHỈNH DÒNG NÀY để chuyển đổi môi trường ***
    // true  = chạy backend local (IntelliJ)
    // false = chạy backend Railway (production)
    private const val USE_LOCAL = true

    private val BASE_URL = if (USE_LOCAL) LOCAL_URL else PRODUCTION_URL

    // Log đầy đủ request & response ra Logcat (tag: OkHttp)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)   // Local thì không cần 90s nữa
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: Retrofit by lazy {
        // setLenient() cho phép Gson parse text/plain ("Đăng ký thành công!")
        // thay vì chỉ chấp nhận JSON chuẩn — tránh lỗi "Use JsonReader.setLenient"
        val gson = GsonBuilder().setLenient().create()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService: GoogleBooksApiService by lazy {
        instance.create(GoogleBooksApiService::class.java)
    }
}