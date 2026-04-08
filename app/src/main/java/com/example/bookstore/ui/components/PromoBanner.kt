package com.example.bookstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PromoBanner() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3B5998)) // Nền xanh
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Khám phá thế giới qua từng trang sách", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Hàng ngàn đầu sách chất lượng đang chờ bạn khám phá", color = Color.White, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* TODO */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Khám phá ngay", color = Color(0xFF3B5998), fontWeight = FontWeight.Bold)
        }
    }
}