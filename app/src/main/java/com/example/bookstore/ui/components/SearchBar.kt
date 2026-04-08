package com.example.bookstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(onSearchClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .clickable { onSearchClick() }
    ) {
        TextField(
            value = "",
            onValueChange = {},
            enabled = false,
            placeholder = { Text("Tìm kiếm sách, tác giả...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F0F0), // Nền xám của ô search
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedIndicatorColor = Color.Transparent, // Tắt cái dòng kẻ gạch dưới
                unfocusedIndicatorColor = Color.Transparent,

                disabledContainerColor = Color(0xFFF0F0F0),
                disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Gray,
                disabledLeadingIconColor = Color.Gray,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}