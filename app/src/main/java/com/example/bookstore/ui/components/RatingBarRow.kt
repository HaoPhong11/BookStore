package com.example.bookstore.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*


@Composable
fun RatingBarRow(starNum: Int, progress: Float, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text("$starNum", fontSize = 12.sp, modifier = Modifier.width(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            Icons.Default.Star,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = Color(0xFFFFC107),
            trackColor = Color(0xFFEEEEEE)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "$count",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.End,
            modifier = Modifier.width(28.dp)
        )
    }
}