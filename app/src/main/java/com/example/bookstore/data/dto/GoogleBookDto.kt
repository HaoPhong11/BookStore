package com.example.bookstore.data.dto

import com.google.gson.annotations.SerializedName

data class GoogleBookResponse(
    val items: List<BookItem>? = emptyList()
)

data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo?,
    val saleInfo: SaleInfo?
)

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val description: String?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    @SerializedName("thumbnail")
    val thumbnailUrl: String?
)

data class SaleInfo(
    // retailPrice là giá bán lẻ thực tế sau khi đã chiết khấu
    val retailPrice: Price?,
    val listPrice: Price?,
    val saleability: String? // "FOR_SALE", "NOT_FOR_SALE", hoặc "FREE"
)

data class Price(
    val amount: Double?, // Số tiền (ví dụ: 150000.0)
    val currencyCode: String? // Đơn vị tiền tệ (ví dụ: "VND")
)