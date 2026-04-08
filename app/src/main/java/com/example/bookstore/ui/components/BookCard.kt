package com.example.bookstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bookstore.data.model.Book
import kotlin.math.ceil

@Composable
fun BookCard(book: Book, onBookClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBookClick(book.id) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = "Bìa sách ${book.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFF0F0F0)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tiêu đề sách
            Text(
                text = book.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 2,
                minLines = 2
            )

            // Tác giả
            Text(
                text = book.author,
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1
            )

            // Đánh giá sao
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                repeat(5) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp))
                }
            }

            val roundedPrice = ceil(book.price / 1000.0) * 1000.0
            val priceString = if (roundedPrice > 0) {
                String.format("%,.0fđ", roundedPrice).replace(",", ".")
            } else {
                "Miễn phí"
            }

            Text(
                text = priceString,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}