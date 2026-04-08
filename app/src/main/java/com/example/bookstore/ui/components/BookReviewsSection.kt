package com.example.bookstore.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BadgeType { None, Discount, New }
data class MockReview(val name: String, val date: String, val rating: Int, val content: String)
data class MockBookRelated(val imageUrl: String, val title: String, val author: String, val price: String, val badgeType: BadgeType, val badgeValue: String)

@Composable
fun BookReviewsSection() {
    val mockReviews = listOf(
        MockReview("Nguyễn Văn A", "23/05/2023", 5, "Cuốn sách này đã thay đổi cách tôi nhìn nhận về các mối quan hệ xung quanh. Rất đáng đọc."),
        MockReview("Trần Thị B", "21/05/2023", 4, "Sách hay, nội dung sâu sắc. Giao hàng hơi chậm một chút nhưng không sao.")
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Đánh giá từ khách hàng", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Xem tất cả", color = Color(0xFF3B5998), fontSize = 14.sp, modifier = Modifier.clickable {})
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Khu vực Tổng quan điểm số và Biểu đồ thanh
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("4.0", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                Row { repeat(4) { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp)) }; Icon(Icons.Default.Star, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp)) }
                Spacer(modifier = Modifier.height(4.dp))
                Text("120 đánh giá", color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Biểu đồ thanh 5 sao -> 1 sao
            Column(modifier = Modifier.weight(1.5f)) {
                RatingBarRow(starNum = 5, progress = 0.8f, count = 105)
                RatingBarRow(starNum = 4, progress = 0.15f, count = 21)
                RatingBarRow(starNum = 3, progress = 0.05f, count = 10)
                RatingBarRow(starNum = 2, progress = 0.02f, count = 4)
                RatingBarRow(starNum = 1, progress = 0.01f, count = 2)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hiển thị vài comment demo
        mockReviews.forEachIndexed { index, review ->
            ReviewItem(review)
            if (index < mockReviews.size - 1) {
                Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 16.dp))
            }
        }
    }
}