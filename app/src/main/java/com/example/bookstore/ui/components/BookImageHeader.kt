package com.example.bookstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun BookImageHeader(imageUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Bìa sách",
            modifier = Modifier.fillMaxHeight().padding(16.dp),
            contentScale = ContentScale.Fit
        )
    }
}