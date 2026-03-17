package com.example.bookstore.data.repo

import com.example.bookstore.data.api.GoogleBooksApiService
import com.example.bookstore.data.model.Book
import com.example.bookstore.data.model.BookItem

class BookRepository(private val api: GoogleBooksApiService) {

    suspend fun getBooks(query: String): List<Book> {
        return try {
            val response = api.searchBooks(query)
            if (response.isSuccessful) {
                // Lấy cái ruột bên trong, nếu rỗng thì trả về danh sách trống
                val items = response.body()?.items ?: emptyList()

                items.map { it.toDomainModel() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Lỗi mạng cũng trả về rỗng cho app khỏi crash
        }
    }


    private fun BookItem.toDomainModel(): Book {
        return Book(
            id = this.id,
            title = this.volumeInfo?.title ?: "Chưa rõ tên sách",
            author = this.volumeInfo?.authors?.firstOrNull() ?: "Khuyết danh",

            imageUrl = this.volumeInfo?.imageLinks?.thumbnailUrl?.replace("http:", "https:") ?: "",
            price = 150000.0 // nay se lay gia trog api gg lun, tam thoi de v

        )
    }
}