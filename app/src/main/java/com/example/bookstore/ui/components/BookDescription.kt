package com.example.bookstore.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BookDescription(description: String) {

    var isExpanded by remember { mutableStateOf(false) }

    var hasVisualOverflow by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val cleanDescription = description.replace(Regex("<.*?>"), "")
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mô tả sản phẩm", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (cleanDescription.isNotBlank()) cleanDescription else "Đang cập nhật mô tả...",
            color = Color.DarkGray,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            maxLines = if (isExpanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.hasVisualOverflow) {
                    hasVisualOverflow = true
                }
            },
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                if (hasVisualOverflow || isExpanded) {
                    isExpanded = !isExpanded
                }
            }
        )

        if (hasVisualOverflow || isExpanded) {
            Text(
                text = if (isExpanded) "Thu gọn" else "Xem thêm",
                color = Color(0xFF3B5998),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clickable { isExpanded = !isExpanded }
            )
        }
    }
}