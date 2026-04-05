package com.example.bookstore.utils

import com.example.bookstore.data.model.Book
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.absoluteValue

/**
 * ==========================================
 * EXTENSION FUNCTIONS — PDF §3.1.2
 * ==========================================
 * Extension Function cho phép mở rộng kiểu dữ liệu có sẵn mà không cần kế thừa.
 * Hàm được khai báo ngoài lớp nhưng hoạt động như phương thức của lớp đó.
 */

/**
 * Định dạng Double thành chuỗi tiền tệ VND.
 * Ví dụ: 88000.0 → "88.000đ"
 *
 * Đây là Extension Function trên kiểu Double của Kotlin.
 * Nhờ Extension Function, lập trình viên có thể gọi hàm này
 * như thể nó là một phương thức gốc của Double. — PDF §3.1.2
 */
fun Double.toVnd(): String {
    val fmt = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"))
    fmt.maximumFractionDigits = 0
    return "${fmt.format(this.toLong())}đ"
}

/**
 * Extension Function kết hợp Null Safety — PDF §3.1.1 + §3.1.2
 *
 * Hàm orDefault() được khai báo trên kiểu String? (nullable String).
 * Toán tử Elvis (?:) bên trong trả về giá trị mặc định khi chuỗi là null.
 * Đây là ví dụ điển hình về cách kết hợp Null Safety và Extension Function
 * để tạo ra API ngôn ngữ trực quan. — PDF §3.3.3
 */
fun String?.orDefault(default: String = "Chưa có thông tin"): String =
    this?.ifBlank { default } ?: default

/**
 * Extension Function: trả về giá hiển thị nhất quán cho sách.
 * Khi Google Books API không cung cấp giá (price == 0.0) hoặc trả về
 * giá USD quá nhỏ (< 1000đ), tạo giá mock từ hash ID: 50.000đ – 240.000đ.
 *
 * Lý do cần mock: Google Books API trả về giá USD (vd: 1.71 USD ≈ 171đ thô),
 * hoặc không có giá. Cả 2 trường hợp cần được normalize về khoảng giá hợp lý.
 * — PDF §3.1.2 Extension Functions + §3.1.1 Null Safety
 */
fun Book.displayPrice(): Double =
    if (price >= 1_000.0) price          // Giá hợp lệ (đã là VND)
    else 50_000.0 + (id.hashCode().absoluteValue % 20) * 10_000.0  // Mock VND
