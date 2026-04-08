package com.example.bookstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bookstore.ui.screens.MockBookRelated


@Composable
fun RelatedBookCard(book: MockBookRelated) {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Ảnh và Badge đè lên
            Box {
                AsyncImage(
                    model = book.imageUrl, contentDescription = book.title,
                    modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFF0F0F0)),
                    contentScale = ContentScale.Crop
                )
                // Hiển thị badge nếu có
//                if (book.badgeType != BadgeType.None) {
//                    val badgeColor = if (book.badgeType == BadgeType.Discount) Color.Red else Color(0xFF4CAF50)
//                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(badgeColor, RoundedCornerShape(2.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
//                        Text(book.badgeValue, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
//                    }
//                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(book.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
            Text(book.author, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Row { repeat(5) { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp)) } }
            Spacer(modifier = Modifier.height(4.dp))

            Text(book.price, color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}