package com.example.bookstore.data.model

/**
 * ==========================================
 * DATA CLASS CATEGORY — PDF §2.3.2
 * ==========================================
 *
 * Data class tự động sinh equals(), hashCode(), toString(), copy().
 * [queryKeyword] là từ khóa tiếng Anh để tìm kiếm trên Google Books API,
 * tách biệt với [name] tiếng Việt hiển thị trên UI.
 * — PDF §2.3.2 Định nghĩa Data Class trong đồ án
 */
data class Category(
    val name: String,           // Tên hiển thị trên UI (tiếng Việt)
    val queryKeyword: String,   // Từ khóa tìm kiếm Google Books (tiếng Anh)
    val bookCount: Int = 568    // Số sách (hiển thị tĩnh trên danh mục)
) {
    companion object {
        /**
         * Danh sách 12 danh mục cố định của ứng dụng.
         * Dùng companion object để truy cập không cần tạo instance — PDF §2.1.3
         */
        val all: List<Category> = listOf(
            Category("Văn học",    "subject:fiction literature"),
            Category("Kinh tế",    "subject:economics business"),
            Category("Tâm lý học", "subject:psychology self-help"),
            Category("Thiếu nhi",  "subject:children juvenile"),
            Category("Giáo khoa",  "subject:textbook education"),
            Category("Lịch sử",    "subject:history"),
            Category("Nghệ thuật", "subject:art"),
            Category("Khoa học",   "subject:science"),
            Category("Tôn giáo",   "subject:religion spirituality"),
            Category("Chính trị",  "subject:politics government"),
            Category("Nấu ăn",     "subject:cooking food"),
            Category("Du lịch",    "subject:travel")
        )
    }
}

