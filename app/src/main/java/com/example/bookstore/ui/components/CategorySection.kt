package com.example.bookstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CategorySection(onViewAllClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Danh mục", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                text = "Xem chi tiết",
                color = Color(0xFF3B5998),
                fontSize = 12.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onViewAllClick() }
                    .padding(4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))


        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CategoryItem("Văn học")
            CategoryItem("Kinh tế")
            CategoryItem("Tâm lý học")
            CategoryItem("Thiếu nhi")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CategoryItem("Giáo khoa")
            CategoryItem("Nghệ thuật")
            CategoryItem("Lịch sử")
            CategoryItem("Khoa học")
        }
    }
}