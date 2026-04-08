package com.example.bookstore.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookstore.data.model.Book

@Composable
fun BookSpecifications(book: Book) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Thông tin chi tiết", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        SpecRow("Tác giả:", book.author)
        SpecRow("Nhà xuất bản:", "NXB Tổng hợp TP.HCM")
        SpecRow("Năm xuất bản:", "2022")
        SpecRow("Số trang:", "320")
        SpecRow("Kích thước:", "14.5 x 20.5 cm")
        SpecRow("Loại bìa:", "Bìa mềm")
    }
}

// Hàm hỗ trợ vẽ từng dòng trong bảng Thông tin chi tiết
@Composable
fun SpecRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Text(title, modifier = Modifier.weight(1f), color = Color.Gray, fontSize = 14.sp)
        Text(value, modifier = Modifier.weight(2f), fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}