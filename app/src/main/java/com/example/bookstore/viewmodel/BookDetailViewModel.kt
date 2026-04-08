package com.example.bookstore.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.model.Book
import com.example.bookstore.data.repo.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val repository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var book by mutableStateOf<Book?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        // Lấy bookId từ URL của Navigation
        val bookId = savedStateHandle.get<String>("bookId")
        if (bookId != null) {
            fetchBookDetail(bookId)
        }
    }

    private fun fetchBookDetail(id: String) {
        viewModelScope.launch {
            isLoading = true
            book = repository.getBookById(id)
            isLoading = false
        }
    }
}