package com.example.bookstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
fun DetailBottomBar() {
    // Dùng Surface để tạo bóng (elevation) đổ lên trên nội dung cuộn
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // NavigationBarsPadding giúp thanh này không bị lẹm vào nút điều hướng ảo của điện thoại
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Bộ chọn số lượng
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFEEEEEE))
            ) {
                Text("-", modifier = Modifier.clickable {}.padding(horizontal = 12.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)
                Text("1", modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                Text("+", modifier = Modifier.clickable {}.padding(horizontal = 12.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nút Thêm vào giỏ
            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF3B5998))
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Thêm", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Nút Mua ngay
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998))
            ) {
                Text("Mua ngay", fontSize = 12.sp)
            }
        }
    }
}