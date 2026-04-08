package com.example.bookstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.model.Book
import com.example.bookstore.data.repo.BookRepository
import com.example.bookstore.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ==========================================
 * CATEGORY VIEWMODEL — PDF §3.3.2 + §3.3.4 + §Ch.6
 * ==========================================
 *
 * Minh họa kết hợp 3 kỹ thuật Kotlin nâng cao:
 *  1. Sealed Class UiState<out T>  → quản lý 3 trạng thái Loading/Success/Error
 *  2. Coroutines (viewModelScope)  → gọi API bất đồng bộ không chặn Main thread
 *  3. @HiltViewModel               → Hilt tự inject BookRepository qua constructor
 *
 * StateFlow thay vì mutableStateOf để tương thích với collectAsStateWithLifecycle()
 * và dễ test hơn (thuần Kotlin, không phụ thuộc Compose runtime).
 * — PDF §5.1.1 MVVM, §Ch.6 Dependency Injection và Hilt
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    // Trạng thái UI khởi tạo là Loading
    // PDF §3.3.2 — UiState<List<Book>> là ví dụ của out T Covariance
    private val _uiState = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Book>>> = _uiState.asStateFlow()

    // Lưu category query hiện tại để tránh gọi API lại khi không cần thiết
    private var currentQuery = ""

    /**
     * Tải danh sách sách theo keyword danh mục.
     *
     * PDF §3.3.4 — Coroutines trong kiến trúc đồ án:
     *   - viewModelScope.launch: chạy bất đồng bộ, tự hủy khi ViewModel bị destroy
     *   - repository.getBooksResult(): hàm suspend, tạm dừng coroutine không chặn thread
     *   - Dispatchers mặc định của viewModelScope là Main → update UI an toàn
     *
     * PDF §3.3.3 — Null Safety:
     *   - .onSuccess / .onFailure: xử lý Result<T> an toàn không dùng try-catch
     *   - error.message ?: "..."  : Elvis operator khi message có thể null
     */
    fun loadBooks(query: String) {
        if (query == currentQuery && _uiState.value is UiState.Success) return
        currentQuery = query

        viewModelScope.launch {                         // PDF §3.3.4 — Coroutines
            _uiState.value = UiState.Loading

            repository.getBooksResult(query)            // suspend function
                .onSuccess { books ->
                    _uiState.value = UiState.Success(books)
                }
                .onFailure { error ->
                    // Elvis operator (?:) — Null Safety PDF §3.3.3
                    _uiState.value = UiState.Error(error.message ?: "Lỗi không xác định")
                }
        }
    }

    /** Reset lại state khi cần load lại danh mục khác */
    fun reset() {
        currentQuery = ""
        _uiState.value = UiState.Loading
    }
}

