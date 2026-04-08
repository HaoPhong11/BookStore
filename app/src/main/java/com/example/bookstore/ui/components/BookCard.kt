package com.example.bookstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bookstore.data.model.Book
import com.example.bookstore.ui.theme.AppColors
import com.example.bookstore.utils.displayPrice
import com.example.bookstore.utils.toVnd
import kotlin.math.absoluteValue

@Composable
fun BookCard(
    book: Book,
    onBookClick: (String) -> Unit,
    onAddToCart: () -> Unit
) {
    // Các logic tính toán UI từ phiên bản đẹp (1)
    val price         = book.displayPrice()
    val discountPct   = (book.id.hashCode().absoluteValue % 20 + 5)
    val originalPrice = price / (1 - discountPct / 100.0)
    val rating      = 3 + (book.id.hashCode().absoluteValue % 3)
    val reviewCount = 50 + (book.id.hashCode().absoluteValue % 200)

    Card(
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .clickable { onBookClick(book.id) }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Ảnh bìa + badge giảm giá
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model              = book.imageUrl,
                    contentDescription = book.title,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )

                // Discount badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppColors.PriceColor)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text       = "-$discountPct%",
                        color      = Color.White,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier            = Modifier.padding(8.dp).weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // --- Phần trên: Tiêu đề, tác giả, sao ---
                Column {
                    Text(
                        text       = book.title,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 13.sp,
                        minLines   = 2,
                        maxLines   = 2,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Text(
                        text     = book.author,
                        color    = Color.Gray,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Sao + số lượt đánh giá
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { i ->
                            Text(
                                text  = if (i < rating) "★" else "☆",
                                color = if (i < rating) AppColors.StarYellow else Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text     = "($reviewCount)",
                            color    = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }

                Column {
                    Text(
                        text       = price.toVnd(),
                        color      = AppColors.PriceColor,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 13.sp
                    )
                    Text(
                        text           = originalPrice.toVnd(),
                        color          = Color.Gray,
                        fontSize       = 11.sp,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(Modifier.height(6.dp))


                    Button(
                        onClick        = onAddToCart,
                        shape          = RoundedCornerShape(8.dp),
                        colors         = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue),
                        modifier       = Modifier.fillMaxWidth().height(34.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint     = Color.White
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Thêm vào giỏ", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}