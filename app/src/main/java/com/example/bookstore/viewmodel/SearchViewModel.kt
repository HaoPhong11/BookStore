package com.example.bookstore.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.model.Book
import com.example.bookstore.data.repo.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    // Danh sách sách lấy về
    var books by mutableStateOf<List<Book>>(emptyList())
        private set

    // Trạng thái đang tải
    var isLoading by mutableStateOf(false)
        private set

    fun searchBooks(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            isLoading = true
            books = repository.getBooks(query) ?: emptyList()
            isLoading = false
        }
    }
}