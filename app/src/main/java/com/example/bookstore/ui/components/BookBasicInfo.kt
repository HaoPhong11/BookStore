package com.example.bookstore.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookstore.data.model.Book
import kotlin.math.ceil

@Composable
fun BookBasicInfo(book: Book) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(book.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))
        Row {
            Text("Tác giả: ", color = Color.Gray, fontSize = 14.sp)
            Text(book.author, color = Color(0xFF3B5998), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(4) { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp)) }
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("4.0", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(" (120)", color = Color.Gray, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))


        val roundedPrice = ceil(book.price / 1000.0) * 1000.0
        val priceString = if (roundedPrice > 0) {
            String.format("%,.0fđ", roundedPrice).replace(",", ".")
        } else {
            "Miễn phí"
        }
        Text(priceString, color = Color.Red, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))
        Text("✅ Còn hàng", color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}