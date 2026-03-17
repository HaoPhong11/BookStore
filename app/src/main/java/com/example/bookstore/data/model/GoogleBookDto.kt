package com.example.bookstore.data.model

import com.google.gson.annotations.SerializedName

data class GoogleBookResponse(
    val items: List<BookItem>? = emptyList()
)

data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo?
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