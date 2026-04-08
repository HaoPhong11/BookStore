package com.example.bookstore.data.repo

import com.example.bookstore.data.api.GoogleBooksApiService
import com.example.bookstore.data.model.Book
import com.example.bookstore.data.dto.BookItem

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

    suspend fun getBookById(id: String): Book? {
        return try {
            val response = api.getBookById(id)
            if (response.isSuccessful) {
                response.body()?.toDomainModel()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Phiên bản trả về Result<List<Book>> thay vì List<Book>.
     * Cho phép CategoryViewModel phân biệt thành công / thất bại
     * để ánh xạ sang UiState.Success / UiState.Error.
     *
     * Null Safety:
     *   - response.body()?.items  → Safe Call Operator (?.)
     *   - ?: emptyList()          → Elvis Operator
     *   - error.message ?: "..."  → Elvis Operator khi message có thể null
     * — PDF §3.3.3 Null Safety trong xử lý dữ liệu API
     */
    suspend fun getBooksResult(query: String): Result<List<Book>> {
        return try {
            val response = api.searchBooks(query)
            if (response.isSuccessful) {
                val items = response.body()?.items ?: emptyList()   // Null Safety: ?.items ?: emptyList()
                Result.success(items.map { it.toDomainModel() })
            } else {
                Result.failure(Exception("Không tải được sách (HTTP ${response.code()})"))
            }
        } catch (e: Exception) {
            // Elvis operator: nếu e.message là null thì dùng chuỗi mặc định
            Result.failure(Exception("Lỗi kết nối: ${e.message ?: "Không xác định"}"))
        }
    }

    private fun BookItem.toDomainModel(): Book {
        val bookPrice = this.saleInfo?.retailPrice?.amount
            ?: this.saleInfo?.listPrice?.amount
            ?: 0.0
        return Book(
            id = this.id,
            title = this.volumeInfo?.title ?: "Chưa rõ tên sách",
            author = this.volumeInfo?.authors?.firstOrNull() ?: "Khuyết danh",

            imageUrl = this.volumeInfo?.imageLinks?.thumbnailUrl?.replace("http:", "https:") ?: "",
            describe = this.volumeInfo?.description?.replace(Regex("<.*?>"), "") ?: "Đang cập nhật mô tả...",
            price = bookPrice

        )
    }


}