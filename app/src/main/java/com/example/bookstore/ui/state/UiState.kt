package com.example.bookstore.ui.state

/**
 * ==========================================
 * SEALED CLASS UISTATE — PDF §3.1.4 + §3.3.2
 * ==========================================
 *
 * Sealed Class (lớp niêm phong) giới hạn tập hợp con có thể có của một kiểu.
 * Từ khóa `out T` là Covariance (hiệp biến):
 *   → Cho phép UiState<Book> được coi là UiState<Any>
 *   → Giúp Sealed Class có thể tái sử dụng cho mọi loại dữ liệu
 *     (sách, đơn hàng, người dùng) mà không cần tạo class riêng biệt.
 *
 * Kết hợp với `when` expression trong Compose UI:
 *   → Loading  : hiển thị CircularProgressIndicator
 *   → Success  : hiển thị danh sách dữ liệu
 *   → Error    : hiển thị thông báo lỗi + nút thử lại
 * — PDF §3.3.2 Sealed Class quản lý trạng thái giao diện
 */
sealed class UiState<out T> {

    /** Trạng thái đang tải dữ liệu */
    data object Loading : UiState<Nothing>()

    /** Trạng thái tải thành công — chứa dữ liệu kiểu T */
    data class Success<T>(val data: T) : UiState<T>()

    /** Trạng thái lỗi — chứa thông báo lỗi */
    data class Error(val message: String) : UiState<Nothing>()
}

