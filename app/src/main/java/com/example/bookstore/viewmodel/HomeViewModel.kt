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
class HomeViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {


    var books by mutableStateOf<List<Book>>(emptyList())
        private set


    var isLoading by mutableStateOf(false)
        private set

    init {
        fetchBooks("lập trình android")
    }

    fun fetchBooks(query: String) {
        viewModelScope.launch {
            isLoading = true
            books = repository.getBooks(query)
            isLoading = false
        }
    }
}