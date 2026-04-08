package com.example.bookstore.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookstore.data.model.Book
import com.example.bookstore.utils.displayPrice
import kotlinx.coroutines.launch

@Composable
fun BookSection(title: String, books: List<Book>, onBookClick: (String) -> Unit, onAddToCart: (Book) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Xem chi tiết", color = Color(0xFF3B5998), fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))

        val chunkedBooks = books.chunked(2)

        chunkedBooks.forEach { rowBooks ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // Cuốn sách bên Trái
                Box(modifier = Modifier.weight(1f)) {
                    BookCard(book = rowBooks[0], onBookClick = onBookClick, onAddToCart = { onAddToCart(rowBooks[0]) })
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (rowBooks.size > 1) {
                        BookCard(book = rowBooks[1],
                            onBookClick = onBookClick,
                            onAddToCart = { onAddToCart(rowBooks[1]) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp)) // Khoảng cách giữa các hàng
        }
    }
}
