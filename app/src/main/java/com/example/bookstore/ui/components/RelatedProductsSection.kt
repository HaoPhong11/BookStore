package com.example.bookstore.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookstore.ui.screens.BadgeType
import com.example.bookstore.ui.screens.MockBookRelated

@Composable
fun RelatedProductsSection() {
    val mockRelatedBooks = listOf(
        MockBookRelated("https://via.placeholder.com/150x200.png?text=Bia+1", "Đắc Nhân Tâm", "Dale Carnegie", "76.500đ", com.example.bookstore.ui.screens.BadgeType.Discount, "-15%"),
        MockBookRelated("https://via.placeholder.com/150x200.png?text=Bia+2", "Đắc Nhân Tâm", "Dale Carnegie", "76.500đ", com.example.bookstore.ui.screens.BadgeType.None, ""),
        MockBookRelated("https://via.placeholder.com/150x200.png?text=Bia+3", "Đắc Nhân Tâm", "Dale Carnegie", "76.500đ", com.example.bookstore.ui.screens.BadgeType.New, "Mới"),
        MockBookRelated("https://via.placeholder.com/150x200.png?text=Bia+4", "Đắc Nhân Tâm", "Dale Carnegie", "76.500đ", BadgeType.Discount, "-15%")
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Sản phẩm liên quan", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Xem tất cả", color = Color(0xFF3B5998), fontSize = 14.sp, modifier = Modifier.clickable {})
        }
        Spacer(modifier = Modifier.height(16.dp))

        val chunkedBooks = mockRelatedBooks.chunked(2)
        chunkedBooks.forEach { rowBooks ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // Cuốn bên trái
                Box(modifier = Modifier.weight(1f)) { RelatedBookCard(rowBooks[0]) }
                Spacer(modifier = Modifier.width(12.dp))
                // Cuốn bên phải (check lỡ list lẻ)
                Box(modifier = Modifier.weight(1f)) { if (rowBooks.size > 1) { RelatedBookCard(rowBooks[1]) } }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}