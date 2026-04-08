package com.example.bookstore.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookstore.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(navController: NavController) {
    val context    = LocalContext.current
    val uriHandler = LocalUriHandler.current

    var name    by remember { mutableStateOf("") }
    var email   by remember { mutableStateOf("") }
    var phone   by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Liên hệ", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.PrimaryBlue)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Hero section ──────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Liên hệ với chúng tôi",
                    fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Chúng tôi luôn sẵn sàng lắng nghe và giải đáp mọi thắc mắc của bạn. " +
                    "Hãy liên hệ với BookVerse qua các kênh dưới đây.",
                    fontSize = 13.sp, color = Color.Gray,
                    lineHeight = 20.sp
                )
            }

            // ── Contact form ─────────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Gửi tin nhắn cho chúng tôi",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold
                )

                ContactFormField("Họ và tên",   name,    { name    = it }, "Nhập họ và tên")
                ContactFormField("Email",        email,   { email   = it }, "Nhập email")
                ContactFormField("Số điện thoại", phone,  { phone   = it }, "Nhập số điện thoại")
                ContactFormField("Tiêu đề",      subject, { subject = it }, "Nhập tiêu đề")
                ContactFormField(
                    label       = "Nội dung",
                    value       = content,
                    onValueChange = { content = it },
                    placeholder = "Nhập nội dung liên hệ",
                    minLines    = 4
                )

                Button(
                    onClick = {
                        Toast.makeText(context, "Đã gửi tin nhắn thành công!", Toast.LENGTH_SHORT).show()
                        name = ""; email = ""; phone = ""; subject = ""; content = ""
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape  = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue)
                ) {
                    Text("Gửi tin nhắn", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Info cards ───────────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ContactInfoCard(
                    iconEmoji   = "📍",
                    iconBgColor = Color(0xFFE3F2FD),
                    title       = "Địa chỉ",
                    lines       = listOf("123 Nguyễn Văn Linh, Quận 7, TP. Hồ Chí Minh")
                )
                ContactInfoCard(
                    iconEmoji   = "📞",
                    iconBgColor = Color(0xFFE8F5E9),
                    title       = "Điện thoại",
                    lines       = listOf("Tổng đài: 1900 1234", "Hotline: 0901 234 567")
                )
                ContactInfoCard(
                    iconEmoji   = "✉️",
                    iconBgColor = Color(0xFFFFF3E0),
                    title       = "Email",
                    lines       = listOf("info@bookverse.vn", "support@bookverse.vn")
                )
                ContactInfoCard(
                    iconEmoji   = "🕐",
                    iconBgColor = Color(0xFFF3E5F5),
                    title       = "Giờ làm việc",
                    lines       = listOf("Thứ Hai – Thứ Sáu: 8:00 – 22:00", "Thứ Bảy – Chủ Nhật: 9:00 – 21:00")
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Social media ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Kết nối với chúng tôi", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SocialCircle("f",  Color(0xFF1877F2)) { uriHandler.openUri("https://facebook.com") }
                    SocialCircle("ig", Color(0xFFE4405F)) { uriHandler.openUri("https://instagram.com") }
                    SocialCircle("tw", Color(0xFF1DA1F2)) { uriHandler.openUri("https://twitter.com") }
                    SocialCircle("yt", Color(0xFFFF0000)) { uriHandler.openUri("https://youtube.com") }
                    SocialCircle("tk", Color(0xFF010101)) { uriHandler.openUri("https://tiktok.com") }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Map placeholder ──────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Vị trí của chúng tôi", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clickable {
                            uriHandler.openUri(
                                "https://maps.google.com/?q=123+Nguyễn+Văn+Linh,+Quận+7,+TP+Hồ+Chí+Minh"
                            )
                        },
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📍", fontSize = 32.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "123 Nguyễn Văn Linh, Quận 7, TP. Hồ Chí Minh",
                                fontSize = 12.sp, color = Color.Gray
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Xem bản đồ →",
                                fontSize = 12.sp, color = AppColors.PrimaryBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Branch list ──────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Hệ thống cửa hàng", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(Modifier.height(12.dp))

                BranchCard(
                    name    = "BookVerse – Chi nhánh Quận 1",
                    address = "45 Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh",
                    phone   = "028 3822 3456",
                    hours   = "8:00 – 22:00 (Hàng ngày)"
                )
                Spacer(Modifier.height(12.dp))
                BranchCard(
                    name    = "BookVerse – Chi nhánh Quận 7",
                    address = "123 Nguyễn Văn Linh, Quận 7, TP. Hồ Chí Minh",
                    phone   = "028 3776 7890",
                    hours   = "8:00 – 22:00 (Hàng ngày)"
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Reusable components ───────────────────────────────────────────────────────

@Composable
private fun ContactFormField(
    label: String, value: String, onValueChange: (String) -> Unit,
    placeholder: String, minLines: Int = 1
) {
    Column {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = { Text(placeholder, color = Color.LightGray, fontSize = 13.sp) },
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(8.dp),
            singleLine    = minLines == 1,
            minLines      = minLines
        )
    }
}

@Composable
private fun ContactInfoCard(
    iconEmoji: String, iconBgColor: Color, title: String, lines: List<String>
) {
    Card(
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier           = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment   = Alignment.Center
            ) {
                Text(iconEmoji, fontSize = 20.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                lines.forEach { line ->
                    Text(line, fontSize = 13.sp, color = Color.Gray, lineHeight = 20.sp)
                }
            }
        }
    }
}

@Composable
private fun SocialCircle(label: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier         = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label.take(1).uppercase(),
            color      = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 16.sp
        )
    }
}

@Composable
private fun BranchCard(name: String, address: String, phone: String, hours: String) {
    Card(
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.Top) {
                Text("📍 ", fontSize = 13.sp)
                Text(address, fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📞 ", fontSize = 13.sp)
                Text(phone, fontSize = 13.sp, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🕐 ", fontSize = 13.sp)
                Text(hours, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

